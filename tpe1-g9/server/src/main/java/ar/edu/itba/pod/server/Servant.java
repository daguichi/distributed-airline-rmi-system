package ar.edu.itba.pod.server;

import ar.edu.itba.pod.exceptions.FlightAlreadyExists;
import ar.edu.itba.pod.exceptions.NoSuchAirplaneException;
import ar.edu.itba.pod.exceptions.NoSuchFlightException;
import ar.edu.itba.pod.model.*;
import ar.edu.itba.pod.service.FlightAdministrationService;
import ar.edu.itba.pod.service.FlightNotificationService;
import ar.edu.itba.pod.service.SeatAdministrationService;
import ar.edu.itba.pod.service.SeatMapService;

import java.rmi.RemoteException;
import java.util.*;

public class Servant implements FlightAdministrationService, FlightNotificationService, SeatAdministrationService, SeatMapService {

    HashMap<String, Ticket> tickets = new HashMap<>() {{
        put("1A", new Ticket("Fico", Category.BUSINESS, 1, 'A'));
        put("14B", new Ticket("Dagos", Category.PREMIUM_ECONOMY, 14, 'B'));
        put("23F", new Ticket("DAX", Category.ECONOMY, 23, 'F'));
    }};

    Airplane airplane = new Airplane("Airbus A320", Arrays.asList(
            new Section(Category.ECONOMY, 10, 10),
            new Section(Category.PREMIUM_ECONOMY, 10, 10),
            new Section(Category.BUSINESS, 10, 10)
    ));

    Flight f = new Flight(airplane, "AR123", "EZE", tickets, FlightStatus.PENDING);

    private Map<String, Flight> flights = new HashMap<>();
    private Map<String, Airplane> airplanes = new HashMap<>();

    @Override
    public void addPlaneModel(String name, List<Section> sections) throws RemoteException {
        Airplane airplane = new Airplane(name, sections);
        airplanes.put(name, airplane);
    }

    @Override
    public void addFlight(String modelName, String flightCode, String destinationCode, Map<String, Ticket> tickets) throws RemoteException {
        Airplane airplane = airplanes.get(modelName);
        if (airplane == null) {
            throw new NoSuchAirplaneException(modelName);
        }
        Flight flight = new Flight(airplane, flightCode, destinationCode, tickets, FlightStatus.PENDING);
        if (flights.containsKey(flightCode)) {
            throw new FlightAlreadyExists(flightCode);
        }
        flights.put(flightCode, flight);
    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        Flight flight = flights.get(flightCode);
        if (flight == null) {
            throw new NoSuchFlightException(flightCode);
        }
        return flight.getStatus();
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        Flight flight = flights.get(flightCode);
        if (flight == null) {
            throw new NoSuchFlightException(flightCode);
        }
        flight.setStatus(FlightStatus.CANCELLED);
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        Flight flight = flights.get(flightCode);
        if (flight == null) {
            throw new NoSuchFlightException(flightCode);
        }
        flight.setStatus(FlightStatus.CONFIRMED);
    }

    @Override
    public void reprogramFlightTickets(Flight flight) throws RemoteException {
//        flight.getTickets().forEach(ticket -> ticket.setFlight(
//
//        ));
    }


    @Override
    public void registerPassenger(String flightCode, String passengerName) throws RemoteException {

    }

    @Override
    public void isAvailable(String flightCode, String passengerName, int row, char column) throws RemoteException {

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

    @Override
    public void getFlightMap(String flightCode) throws RemoteException {
        Flight flight = flights.get(flightCode);
        if (flight == null) {
            throw new NoSuchFlightException(flightCode);
        }
        flight.getAirplane().getSections().forEach(section -> {
            System.out.println("|");
            for (int i = 0; i < section.getRowCount(); i++) {
                String seat = null;
                for (int j = 0; j < section.getColumnCount(); j++) {
                    seat = String.valueOf(i) + String.valueOf((char) (j + 65));
                    char passenger = flight.getTickets().containsKey(seat) ? flight.getTickets().get(seat).getPassengerName().charAt(0) : '*';
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

    public static void main(String[] args) throws RemoteException {
        Servant servant = new Servant();
        servant.getFlightMap("AR123");
    }
}
