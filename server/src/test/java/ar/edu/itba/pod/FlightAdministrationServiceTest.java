package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.NoSuchFlightException;
import ar.edu.itba.pod.exceptions.NotPendingFlightException;
import ar.edu.itba.pod.model.*;
import ar.edu.itba.pod.server.Airport;
import ar.edu.itba.pod.server.Servant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlightAdministrationServiceTest {

    Servant servant = new Servant();
    Airport airport = Airport.getInstance();

    private final String planeName = "TEST";
    private final String planeName2 = "TEST-2";
    private final String flightCode = "TEST";
    private final String flightCode2 = "TEST2";
    private final String flightCode3 = "TEST3";
    private final String destinationCode = "TEST";
    private final String passengerName1 = "A";
    private final String passengerName2 = "Z";

    private final Section business = new Section(Category.BUSINESS, 3, 5);
    private final Section economy = new Section(Category.ECONOMY, 5, 10);
    private final Section smallBusiness = new Section(Category.BUSINESS, 1, 1);
    private final Section invalidSection = new Section(Category.ECONOMY, -1, 10);
    private final List<Section> sectionList = Arrays.asList(business, economy);
    private final List<Section> sectionListNoBusiness = Collections.singletonList(economy);

    private final Ticket ticket = new Ticket(passengerName1, Category.BUSINESS);
    private final Ticket ticket2 = new Ticket(passengerName2, Category.ECONOMY);
    private final Ticket ticket3 = new Ticket(passengerName2, Category.ECONOMY);
    private final List<Ticket> tickets = Arrays.asList(ticket, ticket2);

    @Test
    public void addValidAirplane() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        Assertions.assertTrue(airport.getAirplanes().containsKey(planeName));
    }

    @Test
    public void addRepeatedAirplane() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addPlaneModel(planeName, sectionListNoBusiness);
        Assertions.assertTrue(airport.getAirplanes().containsKey(planeName));
        Assertions.assertEquals(Category.BUSINESS ,airport.getAirplanes().get(planeName)
                .getSeats().get(0).get(0).getCategory());
    }

    @Test
    public void addInvalidSectionAirplane() throws RemoteException {
        servant.addPlaneModel(planeName, Collections.singletonList(invalidSection));
        Assertions.assertFalse(airport.getAirplanes().containsKey(planeName));
    }

    @Test
    public void addValidFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        Assertions.assertTrue(airport.getFlights().containsKey(flightCode));
    }

    @Test
    public void addInvalidAirplaneFlight() throws RemoteException {
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        Assertions.assertFalse(airport.getFlights().containsKey(flightCode));
    }

    @Test
    public void addRepeatedFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.addFlight(planeName, flightCode2, destinationCode, tickets);
        Assertions.assertTrue(airport.getFlights().containsKey(flightCode));
        Assertions.assertNotEquals(flightCode2, airport.getFlights().get(flightCode).getFlightCode());
    }

    @Test
    public void getFlightStatus() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        Assertions.assertEquals(FlightStatus.PENDING, airport.getFlights().get(flightCode).getStatus());
    }

    @Test
    public void getFlightStatusInvalidFlight() throws RemoteException {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.getFlightStatus(flightCode),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void cancelFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.cancelFlight(flightCode);
        Assertions.assertEquals(FlightStatus.CANCELLED, airport.getFlights().get(flightCode).getStatus());
    }

    @Test
    public void cancelNotPendingFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.cancelFlight(flightCode);

        NotPendingFlightException exception = Assertions.assertThrows(NotPendingFlightException.class,
                () -> servant.cancelFlight(flightCode),
                "Should have thrown NotPendingFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void confirmFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.confirmFlight(flightCode);
        Assertions.assertEquals(FlightStatus.CONFIRMED, airport.getFlights().get(flightCode).getStatus());
    }

    @Test
    public void cancelNotConfirmedFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.cancelFlight(flightCode);

        NotPendingFlightException exception = Assertions.assertThrows(NotPendingFlightException.class,
                () -> servant.confirmFlight(flightCode),
                "Should have thrown NotPendingFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void reprogramFlightsTickets() throws  RemoteException {
        List<Ticket> ticketsList = new ArrayList<>();
        ticketsList.add(ticket);
        ticketsList.add(ticket2);
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, ticketsList);
        servant.addFlight(planeName, flightCode2, destinationCode, new ArrayList<>());
        servant.cancelFlight(flightCode);
        servant.reprogramFlightsTickets();
        for(Ticket t : ticketsList) {
            Assertions.assertFalse(airport.getFlights()
                    .get(flightCode).getTickets().contains(t));
            Assertions.assertTrue(airport.getFlights()
                    .get(flightCode2).getTickets().contains(t));
        }
    }

    @Test
    public void reprogramMultipleFlightsTickets() throws  RemoteException {
        List<Ticket> ticketsList = new ArrayList<>();
        ticketsList.add(ticket);
        ticketsList.add(ticket2);
        List<Ticket> ticketsList2 = new ArrayList<>();
        ticketsList.add(ticket3);
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, ticketsList);
        servant.addFlight(planeName, flightCode2, destinationCode, ticketsList2);
        servant.addFlight(planeName, flightCode3, destinationCode, new ArrayList<>());
        servant.cancelFlight(flightCode);
        servant.cancelFlight(flightCode2);

        servant.reprogramFlightsTickets();

        for(Ticket t : ticketsList) {
            Assertions.assertTrue(airport.getFlights()
                    .get(flightCode3).getTickets().contains(t));
        }
        for(Ticket t : ticketsList2) {
            Assertions.assertTrue(airport.getFlights()
                    .get(flightCode3).getTickets().contains(t));
        }
    }

    //TODO no esta andando orden alfabetico
    @Test
    public void reprogramFlightsTicketsNotEnoughRoom() throws  RemoteException {
        List<Ticket> ticketsList = new ArrayList<>();
        ticketsList.add(ticket);
        ticketsList.add(ticket2);

        servant.addPlaneModel(planeName, sectionList);
        servant.addPlaneModel(planeName2, Collections.singletonList(smallBusiness));

        servant.addFlight(planeName, flightCode, destinationCode, ticketsList);
        servant.addFlight(planeName2, flightCode2, destinationCode, new ArrayList<>());

        servant.cancelFlight(flightCode);
        servant.reprogramFlightsTickets();

        Assertions.assertFalse(airport.getFlights()
                .get(flightCode).getTickets().contains(ticket));
        Assertions.assertTrue(airport.getFlights()
                .get(flightCode).getTickets().contains(ticket2));

        Assertions.assertTrue(airport.getFlights()
                .get(flightCode2).getTickets().contains(ticket));
        Assertions.assertFalse(airport.getFlights()
                .get(flightCode2).getTickets().contains(ticket2));

    }
 }
