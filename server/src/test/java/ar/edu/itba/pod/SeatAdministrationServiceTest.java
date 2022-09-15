package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.*;
import ar.edu.itba.pod.model.*;
import ar.edu.itba.pod.server.Servant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeatAdministrationServiceTest {

    Servant servant = new Servant();

    private final String planeName = "TEST";
    private final String flightCode = "TEST";
    private final String flightCode2 = "TEST-2";
    private final String flightCode3 = "TEST-3";
    private final String flightCode4 = "TEST-4";
    private final String flightCode5 = "TEST-5";
    private final String passengerName1 = "A";
    private final String passengerName2 = "Z";
    private final String destinationCode = "TEST";
    private final String destinationCode2 = "TEST-2";


    private final Section business = new Section(Category.BUSINESS, 3, 5);
    private final Section economy = new Section(Category.ECONOMY, 5, 10);
    private final List<Section> sectionList = Arrays.asList(business, economy);

    private final Ticket ticket = new Ticket(passengerName1, Category.BUSINESS);
    private final Ticket ticket2 = new Ticket(passengerName2, Category.ECONOMY);
    private final List<Ticket> tickets = Arrays.asList(ticket, ticket2);




    @Test
    public void isAvailable() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);

        Assertions.assertEquals(new Seat(Category.BUSINESS, 1, 'A'),
                servant.isAvailable(flightCode, 1, 'A'));
    }

    @Test
    public void isAvailableInvalidSeat() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);

        Assertions.assertThrows(InvalidSeatException.class,
                () -> servant.isAvailable(planeName, 20, 'A'),
                "Should have thrown InvalidSeatException");
    }

    @Test
    public void isAvailableInvalidFlight() {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.isAvailable(flightCode, 1, 'A'),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void assignSeat() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName1, 1, 'A');
        Assertions.assertFalse(servant.getAirport().getFlights().get(flightCode)
                .getAirplane().getSeats().get(1).get(0).isAvailable());
    }

    @Test
    public void assignSeatInvalidSeat() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);

        Assertions.assertThrows(InvalidSeatException.class,
                () -> servant.assignSeat(planeName, passengerName1, 1, 'Z'),
                "Should have thrown InvalidSeatException");
    }

    @Test
    public void assignSeatNoTicket() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);

        Assertions.assertThrows(NoTicketException.class,
                () -> servant.assignSeat(planeName, "NO-TICKET", 1, 'A'),
                "Should have thrown NoTicketException");
    }

    @Test
    public void assignSeatInvalidFlight() {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.assignSeat(flightCode, passengerName1, 1, 'A'),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void assignSeatPassengerAlreadySeated() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName1, 1, 'A');
        PassengerAlreadyInFlightException exception = Assertions.assertThrows(PassengerAlreadyInFlightException.class,
                () -> servant.assignSeat(flightCode, passengerName1, 2, 'A'),
                "Should have thrown PassengerAlreadyInFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void assignUnavailableSeat() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName1, 1, 'A');
        Assertions.assertThrows(UnassignableSeatException.class,
                () -> servant.assignSeat(flightCode, passengerName2, 1, 'A'),
                "Should have thrown UnassignableSeatException");}

    @Test
    public void assignSeatBetterCategory() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        Assertions.assertThrows(UnassignableSeatException.class,
                () -> servant.assignSeat(flightCode, passengerName2, 1, 'A'),
                "Should have thrown UnassignableSeatException");
    }

    @Test
    public void assignSeatNotPendingFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.confirmFlight(flightCode);
        Assertions.assertThrows(UnassignableSeatException.class,
                () -> servant.assignSeat(flightCode, passengerName2, 1, 'A'),
                "Should have thrown UnassignableSeatException");
    }

    @Test
    public void changeSeat() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName1, 1, 'A');

        servant.changeSeat(flightCode, passengerName1, 2, 'B');

        Assertions.assertTrue(servant.getAirport().getFlights().get(flightCode)
                .getAirplane().getSeats().get(1).get(0).isAvailable());
        Assertions.assertFalse(servant.getAirport().getFlights().get(flightCode)
                .getAirplane().getSeats().get(2).get(1).isAvailable());
        Assertions.assertEquals(ticket, servant.getAirport().getFlights().get(flightCode)
                .getAirplane().getSeats().get(2).get(1).getTicket().get());
    }

    @Test
    public void changeSeatInvalidFlight() {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.changeSeat(flightCode, passengerName1, 1, 'A'),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void changeSeatInvalidSeat() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName1, 1, 'A');
        Assertions.assertThrows(InvalidSeatException.class,
                () -> servant.changeSeat(planeName, passengerName1, 1, 'Z'),
                "Should have thrown InvalidSeatException");
    }

    @Test
    public void changeSeatNotSeated() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);

        Assertions.assertThrows(PassengerNotInFlightException.class,
                () -> servant.changeSeat(planeName, passengerName1, 1, 'A'),
                "Should have thrown PassengerNotInFlightException");
    }


    @Test
    public void changeUnavailableSeat() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName1, 5, 'A');
        servant.assignSeat(flightCode, passengerName2, 5, 'B');
        Assertions.assertThrows(UnassignableSeatException.class,
                () -> servant.changeSeat(flightCode, passengerName2, 5, 'A'),
                "Should have thrown UnassignableSeatException");}

    @Test
    public void changeSeatBetterCategory() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName2, 5, 'A');
        Assertions.assertThrows(UnassignableSeatException.class,
                () -> servant.changeSeat(flightCode, passengerName2, 1, 'A'),
                "Should have thrown UnassignableSeatException");
    }

    @Test
    public void changeSeatNotPendingFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.assignSeat(flightCode, passengerName1, 1, 'A');
        servant.confirmFlight(flightCode);
        Assertions.assertThrows(UnassignableSeatException.class,
                () -> servant.changeSeat(flightCode, passengerName1, 1, 'A'),
                "Should have thrown UnassignableSeatException");
    }

    @Test
    public void getAlternativeFlights() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.addFlight(planeName, flightCode2, destinationCode, new ArrayList<>());
        servant.addFlight(planeName, flightCode3, destinationCode, tickets);
        servant.addFlight(planeName, flightCode4, destinationCode2, new ArrayList<>());
        servant.addFlight(planeName, flightCode5, destinationCode, new ArrayList<>());
        servant.cancelFlight(flightCode5);

        AlternativeFlight alternativeFlight1 =
                new AlternativeFlight(destinationCode, flightCode2, Category.BUSINESS, 15);
        AlternativeFlight alternativeFlight2 =
                new AlternativeFlight(destinationCode, flightCode2, Category.ECONOMY, 50);

        List<AlternativeFlight> alternativeFlights = servant.getAlternativeFlights(flightCode, passengerName1);
        Assertions.assertEquals(2, alternativeFlights.size());
        Assertions.assertTrue(alternativeFlights.containsAll(Arrays.asList(alternativeFlight1, alternativeFlight2)));
    }

    @Test
    public void getAlternativeFlightsNoTicket() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, new ArrayList<>());

        Assertions.assertThrows(NoTicketException.class,
                () -> servant.getAlternativeFlights(flightCode, passengerName1),
                "Should have thrown NoTicketException");
    }

    @Test
    public void getAlternativeFlightsInvalidFlight() {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.getAlternativeFlights(flightCode, passengerName1),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void changeFlight() throws RemoteException {
        List<Ticket> ticketsList = new ArrayList<>();
        ticketsList.add(ticket);
        ticketsList.add(ticket2);
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, ticketsList);
        servant.addFlight(planeName, flightCode2, destinationCode, new ArrayList<>());

        servant.changeFlight(flightCode, flightCode2, passengerName1);
        Assertions.assertFalse(servant.getAirport().getFlights().get(flightCode).getTickets().contains(ticket));
        Assertions.assertTrue(servant.getAirport().getFlights().get(flightCode2).getTickets().contains(ticket));
    }

    @Test
    public void changeFlightNotAvailable() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.addFlight(planeName, flightCode2, destinationCode, new ArrayList<>());
        servant.cancelFlight(flightCode2);

        Assertions.assertThrows(InvalidAlternativeFlightException.class,
                () -> servant.changeFlight(flightCode, flightCode2, passengerName1),
                "Should have thrown InvalidAlternativeFlightException");
    }

    @Test
    public void changeFlightAlreadyInFLight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.addFlight(planeName, flightCode2, destinationCode, tickets);

        Assertions.assertThrows(InvalidAlternativeFlightException.class,
                () -> servant.changeFlight(flightCode, flightCode2, passengerName1),
                "Should have thrown InvalidAlternativeFlightException");
    }

    @Test
    public void changeFlightsNoTicket() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, new ArrayList<>());

        Assertions.assertThrows(NoTicketException.class,
                () -> servant.changeFlight(flightCode, flightCode2, passengerName1),
                "Should have thrown NoTicketException");
    }

    @Test
    public void changeInvalidOldFlight() {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.changeFlight(flightCode, flightCode2, passengerName1),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void changeInvalidNewFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);

        Assertions.assertThrows(InvalidAlternativeFlightException.class,
                () -> servant.changeFlight(flightCode, flightCode2, passengerName1),
                "Should have thrown InvalidAlternativeFlightException");
    }
}
