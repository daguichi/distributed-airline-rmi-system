package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.NotificationEventCallback;
import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.model.*;
import ar.edu.itba.pod.service.FlightAdministrationService;
import ar.edu.itba.pod.service.FlightNotificationService;
import ar.edu.itba.pod.service.SeatAdministrationService;
import ar.edu.itba.pod.service.SeatMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Servant implements FlightAdministrationService, FlightNotificationService, SeatAdministrationService, SeatMapService {

    private final static Logger logger = LoggerFactory.getLogger(Servant.class);
    private final Airport airport = new Airport();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void addPlaneModel(String name, List<Section> sections) throws RemoteException {
        for(Section s : sections) {
            if (s.getColumnCount() <= 0 || s.getRowCount() <= 0) {
                throw new InvalidSectionException(name);
            }
        }
        Airplane airplane = new Airplane(name, sections);

        airport.addAirplane(airplane);
    }

    @Override
    public void addFlight(String modelName, String flightCode,
                                   String destinationCode, List<Ticket> tickets) throws RemoteException {
        Airplane airplane = airport.getAirplane(modelName);
        if(airplane == null)
            throw new NoSuchAirplaneException(modelName,flightCode);
        Flight flight = new Flight(airplane, flightCode, destinationCode, tickets, FlightStatus.PENDING);

        airport.addFlight(flight);
    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        return airport.getFlight(flightCode).getStatus();
    }

    @Override
    public FlightStatus cancelFlight(String flightCode) throws RemoteException {
        changeFlightStatus(flightCode, FlightStatus.CANCELLED);
        notifyFlightCancelled(flightCode);
        return FlightStatus.CANCELLED;
    }

    @Override
    public FlightStatus confirmFlight(String flightCode) throws RemoteException, InterruptedException {
        changeFlightStatus(flightCode, FlightStatus.CONFIRMED);
        notifyFlightConfirmed(flightCode);
        executor.awaitTermination(1, TimeUnit.SECONDS);

//        TODO: Ver donde ponemos este remove, deberia estar despues del submit del executor subscribers.remove(flightCode);
        return FlightStatus.CONFIRMED;
    }

    private void changeFlightStatus(String flightCode, FlightStatus status) {
        Flight flight = airport.getFlight(flightCode);

        flight.getLock().readLock().lock();
        try {
            if (flight.getStatus() != FlightStatus.PENDING) {
                throw new NotPendingFlightException(flightCode);
            }
        }
        finally {
            flight.getLock().readLock().unlock();
        }

        flight.getLock().writeLock().lock();
        try {
            flight.setStatus(status);
        }
        finally {
            flight.getLock().writeLock().unlock();
        }
    }

    @Override
    public ReticketWrapper reprogramFlightsTickets() throws RemoteException {
        List<Flight> reprogramFlights = airport.getCancelledFlights();

        ReticketWrapper rw = new ReticketWrapper();
        for (Flight f : reprogramFlights) {
            processFlight(f, rw);
        }
        return rw;
    }

    private void processFlight(Flight oldFlight, ReticketWrapper rw) {
        List<Flight> possibleFlights = airport.getAlternativeFlights(oldFlight.getDestinationCode());
        possibleFlights.forEach(flight -> flight.getLock().writeLock().lock());
        try {
            oldFlight.getLock().writeLock().lock();
            try {
                List<Ticket> tickets = new LinkedList<>(oldFlight.getTickets());
                tickets.sort(Comparator.comparing(Ticket::getPassengerName));
                for(Ticket t : tickets)
                    processTicket(t, possibleFlights, oldFlight, rw);
            }
            finally {
                oldFlight.getLock().writeLock().unlock();
            }
        }
        finally {
            possibleFlights.forEach(flight -> flight.getLock().writeLock().unlock());
        }

    }

    private void processTicket(Ticket ticket, List<Flight> possibleFlights, Flight oldFlight, ReticketWrapper rw) {
        Flight newFlight = null;
        int category = ticket.getCategory().ordinal();
        while(newFlight == null && category >= 0) {
            int currentCategory = category;
            newFlight = possibleFlights.stream().filter(
                    flight -> flight.getAirplane().getSeats().values().stream().anyMatch(
                            row -> row.values().stream().anyMatch(
                                    seat -> seat.isAvailable() && seat.getCategory().ordinal() == currentCategory)
                    )
            ).min(Comparator.comparing(Flight::availableSeats).thenComparing(flight -> flight.getAirplane().getName()))
                    .orElse(null);
            category--;
        }

        if(newFlight == null) {
            rw.addNoAlternativeTicket(new ReticketWrapper.TicketInfo(ticket.getPassengerName(), oldFlight.getFlightCode()));
            return ;
        }

        oldFlight.getTickets().remove(ticket);
        newFlight.getTickets().add(ticket);
        rw.incrementTickets();
    }

    @Override
    public Seat isAvailable(String flightCode, int row, char column) throws RemoteException {
        Flight flight = airport.getFlight(flightCode);
        return getSeat(flight,row,column);
    }

    @Override
    public void assignSeat(String flightCode, String passengerName, int row, char column) throws RemoteException {
        Flight flight = airport.getFlight(flightCode);
        flight.getLock().writeLock().lock();
        try {
            Seat seat = getSeat(flight,row,column);
            Ticket ticket = getTicket(flight,passengerName);
            if(getPassengerSeat(flight, passengerName).isPresent())
                throw new PassengerAlreadyInFlightException(passengerName, flightCode);
            checkSeat(flight, seat, ticket);
            seat.setTicket(ticket);
        }
        finally {
            flight.getLock().writeLock().unlock();
        }
        notifySeatAssigned(flightCode, row, column, passengerName);
    }

    @Override
    public void changeSeat(String flightCode, String passengerName, int row, char column) throws RemoteException {
        Flight flight = airport.getFlight(flightCode);
        Optional<Seat> oldSeat;
        Category oldCategory;
        Seat newSeat;

        flight.getLock().writeLock().lock();
        try {
            newSeat = getSeat(flight, row, column);

            oldSeat = getPassengerSeat(flight,passengerName);
            if(!oldSeat.isPresent())
                throw new PassengerNotInFlightException(passengerName, flightCode);

            Ticket ticket = getTicket(flight, passengerName);
            oldCategory = oldSeat.get().getCategory();
            checkSeat(flight, newSeat, ticket);
            oldSeat.get().setTicket(null);
            newSeat.setTicket(ticket);
        }
        finally {
            flight.getLock().writeLock().unlock();
        }

        notifySeatChanged(passengerName, flightCode, oldSeat.get().getRow(),
                oldSeat.get().getColumn(), row, column, oldCategory.toString(),
                newSeat.getCategory().toString());
    }

    @Override
    public List<AlternativeFlight> getAlternativeFlights(String flightCode, String passengerName) throws RemoteException {
        Flight baseFlight = airport.getFlight(flightCode);
        Ticket baseTicket = getTicket(baseFlight,passengerName);
        List<Flight> alternativeFlights;
        List<AlternativeFlight> toReturn = new ArrayList<>();

        alternativeFlights = airport.getAlternativeFlights(flightCode, baseFlight.getDestinationCode(), passengerName);
        alternativeFlights.forEach(flight -> flight.getLock().readLock().lock());
        try {
            for(int i = baseTicket.getCategory().ordinal(); i >= 0 ; i--) {
                for(Flight f : alternativeFlights) {
                    f.getLock().readLock().lock();
                    int finalI = i;
                    long count = f.getAirplane().getSeats().values().stream().
                            flatMap(row -> row.values().stream()).
                            filter(s -> s.isAvailable() && s.getCategory().ordinal() == finalI).count();
                    if(count != 0) {
                        AlternativeFlight alt = new AlternativeFlight(f.getDestinationCode(), f.getFlightCode(),
                                Category.values()[i],count);
                        toReturn.add(alt);
                    }
                }
            }
        }
        finally {
            alternativeFlights.forEach(flight -> flight.getLock().readLock().unlock());
        }

        toReturn.sort(Comparator.comparing(AlternativeFlight::getCategory).thenComparing(AlternativeFlight::getAvailableSeats).reversed().thenComparing(AlternativeFlight::getFlightCode));
        return toReturn;
    }

    @Override
    public void changeFlight(String oldFlightCode, String newFlightCode, String passengerName) throws RemoteException, InterruptedException {
        Flight oldFlight = airport.getFlight(oldFlightCode);
        Ticket oldTicket = getTicket(oldFlight,passengerName);
        List<Flight> alternativeFlights = airport.getAlternativeFlights(
                oldFlightCode,oldFlight.getDestinationCode(),passengerName);
        Optional<Flight> newFlight = alternativeFlights.stream().filter(flight -> flight.getFlightCode().equals(newFlightCode)).findFirst();

        if(!newFlight.isPresent())
            throw new InvalidAlternativeFlightException(newFlightCode);

        Flight flight = newFlight.get();
        flight.getLock().writeLock().lock();
        oldFlight.getLock().writeLock().lock();
        try {
            flight.getTickets().add(oldTicket);
            oldFlight.getTickets().remove(oldTicket);
        }
        finally {
            flight.getLock().writeLock().unlock();
            oldFlight.getLock().writeLock().unlock();
        }

        notifyTicketChanged(passengerName,oldFlightCode, newFlightCode);
        executor.awaitTermination(1, TimeUnit.SECONDS);
        Map<String, List<NotificationEventCallback>> oldFlightCodeSubs = airport.getSubscribers().get(oldFlightCode);
        airport.getSubscribers().remove(oldFlightCode);
        airport.getSubscribers().put(newFlightCode, oldFlightCodeSubs);
    }
    @Override
    public void registerPassenger(String flightCode, String passengerName,
                                  NotificationEventCallback callback) throws RemoteException {
        Flight flight = airport.getFlight(flightCode);
        flight.getLock().readLock().lock();
        try {
            if(flight.getStatus().equals(FlightStatus.CONFIRMED))
                throw new FlightAlreadyConfirmedException(flightCode);

            airport.addSubscriber(flightCode,passengerName, callback);
        }
        finally {
            flight.getLock().readLock().unlock();
        }

        notifySuccessfulRegistration(flightCode, passengerName);
    }

    @Override
    public List<Row> getFlightMap(String flightCode) throws RemoteException {
        Flight flight = airport.getFlight(flightCode);
        flight.getLock().readLock().lock();
        List<Row> rows = new ArrayList<>();
        try {
            for(Integer key : flight.getAirplane().getSeats().keySet()) {
                List<Seat> seats = getSeatRow(flight, key);
                rows.add(new Row(seats, key, seats.get(0).getCategory()));
            }
        }
        finally {
            flight.getLock().readLock().unlock();
        }

        if(rows.isEmpty())
            throw new EmptySeatMapException();

        return rows;
    }

    @Override
    public List<Row> getFlightMapByCategory(String flightCode, Category category) throws RemoteException {
        Flight flight = airport.getFlight(flightCode);
        flight.getLock().readLock().lock();
        List<Row> rows = new ArrayList<>();
        try {
            for(Integer key : flight.getAirplane().getSeats().keySet()) {
                List<Seat> seats = getSeatRow(flight, key);
                Category rowCategory = seats.get(0).getCategory();
                if(rowCategory.equals(category))
                    rows.add(new Row(seats, key, rowCategory));
                else if(rowCategory.ordinal() < category.ordinal())
                    break ;
            }
        }
        finally {
            flight.getLock().readLock().unlock();
        }

        if(rows.isEmpty())
            throw new EmptySeatMapException();

        return rows;
    }

    @Override
    public Row getFlightMapByRow(String flightCode, int row) throws RemoteException {
        Flight flight = airport.getFlight(flightCode);
        flight.getLock().readLock().lock();
        List<Seat> seats;
        try {
            seats = getSeatRow(flight, row);
        }
        finally {
            flight.getLock().readLock().unlock();
        }

        if(seats.isEmpty())
            throw new EmptySeatMapException();

        return new Row(seats, row, seats.get(0).getCategory());
    }

    private Seat getSeat(Flight flight, int row, char column) {
        flight.getLock().readLock().lock();
        Seat seat;
        try {
            Map<Integer, Seat> specifiedRow = flight.getAirplane().getSeats().get(row);
            if(specifiedRow == null)
                throw new InvalidSeatException(row + 1, column);
            seat = specifiedRow.get((int) column - 'A');
            if(seat == null)
                throw new InvalidSeatException(row + 1, column);
        }
        finally {
            flight.getLock().readLock().unlock();
        }
        return seat;
    }

    private Ticket getTicket(Flight flight, String passengerName) {
        flight.getLock().readLock().lock();
        Optional<Ticket> ticket;
        try {
            ticket = flight.getTickets().stream().filter(
                    t -> t.getPassengerName().equals(passengerName)).findFirst();
            if(!ticket.isPresent())
                throw new NoTicketException(passengerName, flight.getFlightCode());
        }
        finally {
            flight.getLock().readLock().unlock();
        }

        return ticket.get();
    }

    private void checkSeat(Flight flight, Seat seat, Ticket ticket) {
        if(!flight.getStatus().equals(FlightStatus.PENDING) ||
                !seat.isAvailable() ||
                seat.getCategory().ordinal() > ticket.getCategory().ordinal())
            throw new UnassignableSeatException();
    }

    //If a seat is not available, it has a ticket assigned to it, ticket will be present
    private Optional<Seat> getPassengerSeat(Flight flight, String passengerName) {
        return flight.getAirplane().getSeats().values().stream()
                .flatMap(r -> r.values().stream())
                .filter(s -> !s.isAvailable()
                        && s.getTicket().get().getPassengerName().equals(passengerName)).findFirst();
    }

    private List<Seat> getSeatRow(Flight flight, int row) {
        if(!flight.getAirplane().getSeats().containsKey(row))
            throw new InvalidRowException(row, flight.getFlightCode());
        return new ArrayList<>(flight.getAirplane().getSeats().get(row).values());
    }

    private void notifyFlightConfirmed(String flightCode) {
        Flight flight = airport.getFlight(flightCode);
        List<String> toNotify = airport.getSubscribers(flightCode);

        flight.getLock().readLock().lock();
        try {
            for(String subscriber : toNotify) {
                for(NotificationEventCallback callback : airport.getCallbacks(flightCode,subscriber)) {
                    executor.submit(() -> {
                        try {
                            Optional<Seat> seat = getPassengerSeat(flight, subscriber);
                            if(seat.isPresent())
                                callback.confirmedFlight(flightCode, flight.getDestinationCode(), seat.get().getRow(),
                                        seat.get().getColumn(), seat.get().getCategory().toString());
                            else {
                                Ticket t = getTicket(flight, subscriber);
                                callback.confirmedFlight(flightCode, flight.getDestinationCode(), t.getCategory().toString());
                            }
                        } catch (RemoteException e) {
                            logger.error(e.getMessage());
                        }
                    });
                }
            }
        }
        finally {
            flight.getLock().readLock().unlock();
        }
    }

    private void notifyFlightCancelled(String flightCode) {
        Flight flight = airport.getFlight(flightCode);
        List<String> toNotify = airport.getSubscribers(flightCode);

        flight.getLock().readLock().lock();
        try {
            for(String subscriber : toNotify) {
                for(NotificationEventCallback callback : airport.getCallbacks(flightCode,subscriber)) {
                    executor.submit(() -> {
                        try {
                            Optional<Seat> seat = getPassengerSeat(flight, subscriber);
                            if (seat.isPresent()) callback.cancelledFlight(flightCode, flight.getDestinationCode(),
                                    seat.get().getRow(), seat.get().getColumn(), seat.get().getCategory().toString());
                            else {
                                Ticket t = getTicket(flight, subscriber);
                                callback.cancelledFlight(flightCode, flight.getDestinationCode(), t.getCategory().toString());
                            }
                        } catch (RemoteException e) {
                            logger.error(e.getMessage());
                        }
                    });
                }
            }
        }
        finally {
            flight.getLock().readLock().unlock();
        }
    }

    private void notifySeatAssigned(String flightCode, int row, char column, String passengerName) {
        Flight flight = airport.getFlight(flightCode);
        List<NotificationEventCallback> toNotify = airport.getCallbacks(flightCode,passengerName);

        flight.getLock().readLock().lock();
        try {
            toNotify.forEach( t ->  executor.submit(() -> {
                try {
                    Optional<Seat> seat = getPassengerSeat(flight, passengerName);
                    if(!seat.isPresent())
                        throw new PassengerNotInFlightException(passengerName, flight.getFlightCode());
                    t.assignedSeat(flightCode, flight.getDestinationCode(),
                            row, column, seat.get().getCategory().toString());
                } catch (RemoteException e) {
                    logger.error(e.getMessage());
                }
            }));
        }
        finally {
            flight.getLock().readLock().unlock();
        }
    }

    private void notifySeatChanged(String passengerName, String flightCode,
                                   int oldRow, char oldColumn, int newRow,
                                   char newColumn, String category, String oldCategory) {
        Flight flight = airport.getFlight(flightCode);
        List<NotificationEventCallback> toNotify = airport.getCallbacks(flightCode,passengerName);

        flight.getLock().readLock().lock();
        try {
            toNotify.forEach(t ->  executor.submit(() -> {
                try {
                    t.movedSeat(flightCode, flight.getDestinationCode(), category,
                            newRow, newColumn, oldCategory, oldRow, oldColumn );
                } catch (RemoteException e) {
                    logger.error(e.getMessage());
                }
            }));
        }
        finally {
            flight.getLock().readLock().unlock();
        }
    }

    private void notifyTicketChanged(String passengerName, String flightCode, String newFlightCode) {
        Flight flight = airport.getFlight(flightCode);
        List<NotificationEventCallback> toNotify = airport.getCallbacks(flightCode,passengerName);

        flight.getLock().readLock().lock();
        try {
            toNotify.forEach( t -> executor.submit(() -> {
                try {
                    t.changedTicket(flightCode, flight.getDestinationCode(), newFlightCode);
                } catch (RemoteException e) {
                    logger.error(e.getMessage());
                }
            }));
        }
        finally {
            flight.getLock().readLock().unlock();
        }
    }

    private void notifySuccessfulRegistration(String flightCode, String passengerName) {
        List<NotificationEventCallback> toNotify = airport.getCallbacks(flightCode,passengerName);
        toNotify.forEach(t -> executor.submit(() -> {
            try {
                t.successfulRegistration(flightCode, airport.getFlight(flightCode).getDestinationCode());
            } catch (RemoteException e) {
                logger.error(e.getMessage());
            }
        }));
    }

    //Testing
    public Airport getAirport() {
        return airport;
    }
}
