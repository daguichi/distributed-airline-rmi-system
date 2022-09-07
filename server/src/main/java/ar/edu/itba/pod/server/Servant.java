package ar.edu.itba.pod.server;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.model.*;
import ar.edu.itba.pod.service.FlightAdministrationService;
import ar.edu.itba.pod.service.FlightNotificationService;
import ar.edu.itba.pod.service.SeatAdministrationService;
import ar.edu.itba.pod.service.SeatMapService;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class Servant implements FlightAdministrationService, FlightNotificationService, SeatAdministrationService, SeatMapService {

    private final Map<String, Airplane> airplanes = new HashMap<>();
    private final Map<String, Flight> flights = new HashMap<>();

    private final Object planesLock = new Object();
    private final Object flightsLock = new Object();

    @Override
    public void addPlaneModel(String name, List<Section> sections) throws RemoteException {
        if(name.isEmpty() || sections.isEmpty())
            throw new InvalidAirplaneException();

        for(Section s : sections) {
            if(s.getColumnCount() <= 0 || s.getRowCount() <= 0)
                throw new InvalidSectionException();
        }

        synchronized (planesLock) {
            if(airplanes.containsKey(name))
                throw new AirplaneAlreadyExistsException(name);

            Airplane airplane = new Airplane(name, sections);
            airplanes.put(name, airplane);
        }
    }

    @Override
    public void addFlight(String modelName, String flightCode, String destinationCode, List<Ticket> tickets) throws RemoteException {
        Airplane airplane = airplanes.get(modelName);
        if (airplane == null)
            throw new NoSuchAirplaneException(modelName);

        synchronized (flightsLock) {
            if (flights.containsKey(flightCode))
                throw new FlightAlreadyExistsException(flightCode);

            Flight flight = new Flight(airplane, flightCode, destinationCode, tickets, FlightStatus.PENDING);
            flights.put(flightCode, flight);
        }
    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        Flight flight = getFLight(flightCode);
        return flight.getStatus();
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        Flight flight = getFLight(flightCode);
        flight.setStatus(FlightStatus.CANCELLED);
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        Flight flight = getFLight(flightCode);
        flight.setStatus(FlightStatus.CONFIRMED);
    }


    //TODO que pasa si un flight que te pasan no esta cancelado
    @Override
    public void reprogramFlightsTickets(List<Flight> reprogramFlights) throws RemoteException {

        List<Flight> possibleFlights = flights.values().stream()
                .filter(flight -> flight.getStatus().equals(FlightStatus.PENDING))
                .collect(Collectors.toList());

        reprogramFlights.sort(Comparator.comparing(Flight::getFlightCode));
        for (Flight f : reprogramFlights)
            processFlight(f, possibleFlights);
    }

    private void processFlight(Flight oldFlight, List<Flight> possibleFlights) {
        List<Ticket> tickets = new LinkedList<>(oldFlight.getTickets());
        tickets.sort(Comparator.comparing(Ticket::getPassengerName));

        List<Flight> destinationFlights = possibleFlights.stream()
                .filter(flight -> flight.getDestinationCode().equals(oldFlight.getDestinationCode()))
                .collect(Collectors.toList());

        for(Ticket t : tickets)
            processTicket(t, destinationFlights);
    }

    private void processTicket(Ticket ticket, List<Flight> possibleFlights) {
        Flight newFlight;
        List<Flight> filteredFlights = possibleFlights.stream().filter(flight -> flight.getAirplane().getSections().stream()
                        .anyMatch(section -> section.getCategory().compareTo(ticket.getCategory()) >= 0))
                .collect(Collectors.toList());




    }

    //TODO HAY QUE CEHQUEAR QUE EL ASIENTO QUE ME PASAN ES CORRECTO?
    //TODO CHEQUEAR QUE LA COLUMN ESTE EN MAYUS?
    @Override
    public boolean isAvailable(String flightCode, int row, char column) throws RemoteException {
        return true;
    }


    @Override
    public void registerPassenger(String flightCode, String passengerName) throws RemoteException {

    }


    @Override
    public void assignSeat(String flightCode, String passengerName, int row, char column) throws RemoteException {

    }

    @Override
    public void changeSeat(String flightCode, String passengerName, int row, char column) throws RemoteException {

    }

    @Override
    public void getAlternativeFlights(String flightCode, String passengerName) throws RemoteException {

    }

    @Override
    public void changeFlight(String oldFlightCode, String newFlightCode, String passengerName) throws RemoteException {

    }


    //TODO REVISAR ESTO, FUNCION PARA PASAR COLUMNA INTEGR A CHAR Y NO HACER (CHAR) J+ 65?
    @Override
    public void getFlightMap(String flightCode) throws RemoteException {
        Flight flight = getFLight(flightCode);

        flight.getAirplane().getSections().forEach(section -> {

            for (int i = 0; i < section.getRowCount(); i++) {
                System.out.print("|");
                for (int j = 0; j < section.getColumnCount(); j++) {
                    char passenger = section.getSeatMap().get(i).get(j) == null
                            ? section.getSeatMap().get(i).get(j).getPassengerName().charAt(0) : '*';
                    System.out.printf(" %2d %c %c |", i, (char) (j + 65), passenger);
                }
                System.out.printf("  %s", section.getCategory().name());
                System.out.println();
            }
        });
    }

    @Override
    public void getFlightMapByCategory(String flightCode, Category category) throws RemoteException {

    }

    @Override
    public void getFlightMapByRow(String flightCode, int row) throws RemoteException {

    }

    private Flight getFLight(String flightCode) {
        Flight flight = flights.get(flightCode);
        if (flight == null)
            throw new NoSuchFlightException(flightCode);
        return flight;
    }

}
