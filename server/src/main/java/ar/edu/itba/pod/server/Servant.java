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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class Servant implements FlightAdministrationService, FlightNotificationService, SeatAdministrationService, SeatMapService {

    private final Map<String, Airplane> airplanes = new HashMap<>();
    private final Map<String, Flight> flights = new HashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Map<String, List<NotificationEventCallback>> subscribers;

    private final static Logger logger = LoggerFactory.getLogger(Servant.class);

    private final ReentrantReadWriteLock reentrantLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = reentrantLock.readLock();
    private final Lock writeLock = reentrantLock.writeLock();


    public Servant() {
        this.subscribers = new HashMap<>();
    }

    @Override
    public void addPlaneModel(String name, List<Section> sections) throws RemoteException {
        if(name.isEmpty() || sections.isEmpty())
            throw new InvalidAirplaneException();

        for(Section s : sections) {
            if(s.getColumnCount() <= 0 || s.getRowCount() <= 0)
                throw new InvalidSectionException();
        }

        writeLock.lock();
        try {
            if(airplanes.containsKey(name))
                throw new AirplaneAlreadyExistsException(name);
            Airplane airplane = new Airplane(name, sections);
            airplanes.put(name, airplane);
            logger.info("Added airplane model: " + name);
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public void addFlight(String modelName, String flightCode, String destinationCode, List<Ticket> tickets) throws RemoteException {
        Airplane airplane = airplanes.get(modelName);

        if (airplane == null)
            throw new NoSuchAirplaneException(modelName);
        writeLock.lock();
        try {
            if(flights.containsKey(flightCode))
                throw new FlightAlreadyExistsException(flightCode);
            Flight flight = new Flight(airplane, flightCode, destinationCode, tickets, FlightStatus.PENDING);
            flights.put(flightCode, flight);
        }
        finally {
            logger.info("Flight {} added", flightCode);
            writeLock.unlock();
        }
    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        readLock.lock();
        Flight flight;
        try {
            flight = getFlight(flightCode);
        }
        finally {
            readLock.unlock();
        }
        return flight.getStatus();
    }

    @Override
    public FlightStatus cancelFlight(String flightCode) throws RemoteException {
        readLock.lock();
        Flight flight;
        try {
            flight = getFlight(flightCode);
            if (flight.getStatus() != FlightStatus.PENDING) {
                throw new NotPendingFlight(flightCode);
            }
        }
        finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            flight.setStatus(FlightStatus.CANCELLED);
        }
        finally {
            writeLock.unlock();
        }
        notifyFlightCancelled(flightCode);
        return FlightStatus.CANCELLED;
    }

    @Override
    public FlightStatus confirmFlight(String flightCode) throws RemoteException {
        readLock.lock();
        Flight flight;
        try {
            flight = getFlight(flightCode);
            if (flight.getStatus() != FlightStatus.PENDING) {
                throw new NotPendingFlight(flightCode);
            }
        }
        finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            flight.setStatus(FlightStatus.CONFIRMED);
        }
        finally {
            writeLock.unlock();
        }
        notifyFlightConfirmed(flightCode);
        return FlightStatus.CONFIRMED;

    }

    @Override
    public ReticketWrapper reprogramFlightsTickets() throws RemoteException {
        readLock.lock();
        List<Flight> reprogramFlights;
        try {
            reprogramFlights = flights.values().stream()
                    .filter(flight -> flight.getStatus().equals(FlightStatus.CANCELLED))
                    .sorted(Comparator.comparing(Flight::getFlightCode)).collect(Collectors.toList());
        }

        finally {
            readLock.unlock();
        }

        ReticketWrapper rw = new ReticketWrapper();
        for (Flight f : reprogramFlights) {
            writeLock.lock();
            try { processFlight(f, rw); }
            finally {
                writeLock.unlock();
             }
        }
        return rw;
    }

    private void processFlight(Flight oldFlight, ReticketWrapper rw) {
        List<Flight> possibleFlights;
        possibleFlights = flights.values().stream().filter(
                flight -> flight.getDestinationCode().equals(oldFlight.getDestinationCode())).filter(
                flight -> flight.getStatus().equals(FlightStatus.PENDING)
        ).collect(Collectors.toList());

        List<Ticket> tickets = new LinkedList<>(oldFlight.getTickets());
        tickets.sort(Comparator.comparing(Ticket::getPassengerName));

        for(Ticket t : tickets)
            processTicket(t, possibleFlights, oldFlight, rw);
    }

    //TODO ver si se puede mejorar
    private void processTicket(Ticket ticket, List<Flight> possibleFlights, Flight oldFlight, ReticketWrapper rw) {
        Flight newFlight = null;
        int category = ticket.getCategory().ordinal();
        while(newFlight == null && category >= 0) {
            newFlight = possibleFlights.stream().filter(
                    flight -> flight.getAirplane().getSeats().values().stream().anyMatch(
                            row -> row.values().stream().anyMatch(
                                    seat -> seat.isAvailable() && seat.getCategory().compareTo(ticket.getCategory()) == 0)
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
    public boolean isAvailable(String flightCode, int row, char column) throws RemoteException {
        readLock.lock();
        Flight flight;
        boolean isSeatAvailable;
        try {
            flight = getFlight(flightCode);
            isSeatAvailable = getSeat(flight, row, column).isAvailable();
        }
        finally {
            readLock.unlock();
        }
        return isSeatAvailable;
    }

    @Override
    public void assignSeat(String flightCode, String passengerName, int row, char column) throws RemoteException {
        Flight flight;
        Seat seat;
        Ticket ticket;
        readLock.lock();
        try {
            flight = flights.get(flightCode);
            seat = getSeat(flight, row, column);
            ticket = getTicket(flight, passengerName);
        }
        finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            if(getOldSeat(flight, passengerName).isPresent())
                throw new PassengerAlreadyInFlightException(passengerName, flightCode);
            checkSeat(flight, seat, ticket);
            seat.setTicket(ticket);
        }
        finally {
            writeLock.unlock();
        }
        notifySeatAssigned(flightCode, row, column);
    }

    @Override
    public void changeSeat(String flightCode, String passengerName, int row, char column) throws RemoteException {
        Flight flight;
        Seat newSeat;
        Ticket ticket;
        Optional<Seat> oldSeat;
        readLock.lock();
        try {
            flight = flights.get(flightCode);
            newSeat = getSeat(flight, row, column);
            ticket = getTicket(flight, passengerName);
            oldSeat = getOldSeat(flight, passengerName);

        }
        finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            if(!oldSeat.isPresent())
                throw new PassengerNotInFlightException(passengerName, flightCode);

            checkSeat(flight, newSeat, ticket);

            oldSeat.get().setTicket(null);
            newSeat.setTicket(ticket);
        }
        finally {
            writeLock.unlock();
        }
        notifySeatChanged(flightCode, oldSeat.get().getRow(), oldSeat.get().getColumn(), row, column,
                newSeat.getCategory().toString(), oldSeat.get().getCategory().toString());
    }

    @Override
    public List<AlternativeFlight> getAlternativeFlights(String flightCode, String passengerName) throws RemoteException {
        Flight baseFlight;
        Ticket baseTicket;
        List<Flight> alternativeFlights;
        List<AlternativeFlight> toReturn = new ArrayList<>();
        readLock.lock();
        try {
            baseFlight = getFlight(flightCode);
            baseTicket = getTicket(baseFlight,passengerName);
            alternativeFlights = getAlternativeFlightsList(baseFlight.getDestinationCode(), passengerName);
            for(int i = 0; i <= baseTicket.getCategory().ordinal(); i++) {
                for(Flight f : alternativeFlights) {
                    int finalI = i;
                    long count = f.getAirplane().getSeats().values().stream().
                            flatMap(row -> row.values().stream()).
                            filter(s -> s.isAvailable() && s.getCategory().ordinal() == finalI).count();
                    if(count != 0) {
                        AlternativeFlight alt = new AlternativeFlight(f.getDestinationCode(), f.getFlightCode(),
                                Category.values()[0],count);
                        toReturn.add(alt);
                    }
                }
            }
        }
        finally {
            readLock.unlock();
        }
        return toReturn;
    }

    @Override
    public void changeFlight(String oldFlightCode, String newFlightCode, String passengerName) throws RemoteException {
        Flight oldFlight;
        Ticket oldTicket;
        List<Flight> alternativeFlights;
        Optional<Flight> newFlight;
        readLock.lock();
        try {
            oldFlight = getFlight(oldFlightCode);
            oldTicket = getTicket(oldFlight,passengerName);
            alternativeFlights = getAlternativeFlightsList(oldFlightCode, passengerName);
            newFlight = alternativeFlights.stream().filter(flight -> flight.getFlightCode().equals(newFlightCode)).findFirst();

        }
        finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            if(!newFlight.isPresent())
                throw new InvalidAlternativeFlightException(newFlightCode);
            newFlight.get().getTickets().add(oldTicket);
            oldFlight.getTickets().remove(oldTicket);
        }
        finally {
            writeLock.unlock();
        }
        notifyTicketChanged(oldFlightCode, newFlightCode);
    }

    @Override
    public void registerPassenger(String flightCode, String passengerName, NotificationEventCallback callback) throws RemoteException {
        readLock.lock();
        Flight flight;
        try {
            flight = getFlight(flightCode);
        }
        finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            if(flight.getStatus().equals(FlightStatus.CONFIRMED))
                throw new FlightAlreadyConfirmedException(flightCode);
            List<NotificationEventCallback> callbacks = subscribers.computeIfAbsent(flightCode, k -> new ArrayList<>());
            callbacks.add(callback);
        }
        finally {
            writeLock.unlock();
        }
        notifySuccessfulRegistration(flightCode);
    }

    @Override
    public List<Row> getFlightMap(String flightCode) throws RemoteException {
        List<Row> rows;
        readLock.lock();
        try {
            Flight flight = getFlight(flightCode);
            rows = new ArrayList<>();
            for(Integer key : flight.getAirplane().getSeats().keySet()) {
                List<Seat> seats = getSeatRow(flight, key);
                rows.add(new Row(seats, key, seats.get(0).getCategory()));
            }
        }
        finally {
            readLock.unlock();
        }

        if(rows.isEmpty())
            throw new EmptyMapException();

        return rows;
    }

    @Override
    public List<Row> getFlightMapByCategory(String flightCode, Category category) throws RemoteException {
        List<Row> rows;
        readLock.lock();
        //TODO REVISAR ESTILO
        try {
            Flight flight = getFlight(flightCode);
            rows = new ArrayList<>();
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
            readLock.unlock();
        }
        if(rows.isEmpty())
            throw new EmptyMapException();

        return rows;
    }

    @Override
    public Row getFlightMapByRow(String flightCode, int row) throws RemoteException {
        List<Seat> seats;
        readLock.lock();
        try {
            Flight flight = getFlight(flightCode);
            seats = getSeatRow(flight, row);
        }
        finally {
            readLock.unlock();
        }
        if(seats.isEmpty())
            throw new EmptyMapException();

        return new Row(seats, row, seats.get(0).getCategory());
    }

    private Flight getFlight(String flightCode) {
        Flight flight = flights.get(flightCode);
        if (flight == null)
            throw new NoSuchFlightException(flightCode);
        return flight;
    }

    private Seat getSeat(Flight flight, int row, char column) {
        Map<Integer, Seat> specifiedRow = flight.getAirplane().getSeats().get(row);
        if(specifiedRow == null)
            throw new InvalidSeatException(row, column);
        Seat seat = specifiedRow.get((int) column - 'A');
        if(seat == null)
            throw new InvalidSeatException(row, column);
        return seat;
    }

    private Ticket getTicket(Flight flight, String passengerName) {
        Optional<Ticket> ticket = flight.getTickets().stream().filter(t -> t.getPassengerName().equals(passengerName)).findFirst();
        if(!ticket.isPresent())
            throw new NoTicketException(passengerName, flight.getFlightCode());
        return ticket.get();
    }

    private void checkSeat(Flight flight, Seat seat, Ticket ticket) {
        if(!flight.getStatus().equals(FlightStatus.PENDING) ||
                !seat.isAvailable() ||
                seat.getCategory().ordinal() > ticket.getCategory().ordinal())
            throw new UnassignableSeatException();
    }

    //If a seat is not available, it has a ticket assigned to it, ticket will be present
    private Optional<Seat> getOldSeat(Flight flight, String passengerName) {
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

    private List<Flight> getAlternativeFlightsList(String oldFlightCode, String passengerName) {
        return flights.values().stream().filter(
                flight -> flight.getDestinationCode().equals(oldFlightCode)).filter(
                flight -> flight.getStatus().equals(FlightStatus.PENDING)).filter(flight -> !flight.getFlightCode().equals(oldFlightCode)
        ).filter( flight -> flight.getTickets().stream().noneMatch(t -> t.getPassengerName().equals(passengerName))).collect(Collectors.toList());
    }

    //Notifications handler
    private void notifyFlightConfirmed(String flightCode) {
        Flight f;
        List<NotificationEventCallback> toNotify;
        readLock.lock();
        try {
            f = getFlight(flightCode);
            toNotify = subscribers.getOrDefault(flightCode, new ArrayList<>());
        }
        finally { readLock.unlock(); }
        if(toNotify != null) {
            for(NotificationEventCallback callback : toNotify) {
                executor.submit(() -> {
                    try {
                        callback.confirmedFlight(flightCode, f.getDestinationCode());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void notifyFlightCancelled(String flightCode) {
        Flight f;
        List<NotificationEventCallback> toNotify;
        readLock.lock();
        try {
            f = getFlight(flightCode);
            toNotify = subscribers.getOrDefault(flightCode, new ArrayList<>());
        }
        finally {
            readLock.unlock();
        }
        if (toNotify != null) {
            for (NotificationEventCallback callback : toNotify) {
                executor.submit(() -> {
                    try {
                        callback.cancelledFlight(flightCode, f.getDestinationCode());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void notifySeatAssigned(String flightCode, int row, char column) {
        Flight f;
        List<NotificationEventCallback> toNotify;
        readLock.lock();
        try {
            f = getFlight(flightCode);
            toNotify = subscribers.getOrDefault(flightCode, new ArrayList<>());
        }
        finally {
            readLock.unlock();
        }
        if (toNotify != null) {
            for (NotificationEventCallback callback : toNotify) {
                executor.submit(() -> {
                    try {
                        callback.assignedSeat(flightCode, f.getDestinationCode(), row, column);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void notifySeatChanged(String flightCode, int oldRow, char oldColumn, int newRow, char newColumn, String category, String oldCategory) {
        Flight f;
        List<NotificationEventCallback> toNotify;
        readLock.lock();
        try {
            f = getFlight(flightCode);
            toNotify = subscribers.getOrDefault(flightCode, new ArrayList<>());
        }
        finally {
            readLock.unlock();
        }
        if (toNotify != null) {
            for (NotificationEventCallback callback : toNotify) {
                executor.submit(() -> {
                    try {
                        callback.movedSeat(flightCode, f.getDestinationCode(), category,
                                newRow, newColumn, oldCategory, oldRow, oldColumn );
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void notifyTicketChanged(String flightCode, String newFlightCode) {
        Flight f;
        List<NotificationEventCallback> toNotify;
        readLock.lock();
        try {
            f = getFlight(flightCode);
            toNotify = subscribers.getOrDefault(flightCode, new ArrayList<>());
        }
        finally {
            readLock.unlock();
        }
        if(toNotify != null) {
            for(NotificationEventCallback callback : toNotify) {
                executor.submit(() -> {
                    try {
                        callback.changedTicket(flightCode, f.getDestinationCode(), newFlightCode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void notifySuccessfulRegistration(String flightCode) {
        Flight f = getFlight(flightCode);
        List<NotificationEventCallback> toNotify = subscribers.getOrDefault(flightCode, new ArrayList<>());
        if(toNotify != null) {
            for(NotificationEventCallback callback : toNotify) {
                executor.submit(() -> {
                    try {
                        callback.successfulRegistration(flightCode, f.getDestinationCode());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
