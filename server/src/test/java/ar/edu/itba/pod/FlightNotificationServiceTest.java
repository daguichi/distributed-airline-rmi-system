package ar.edu.itba.pod;

import ar.edu.itba.pod.callbacks.NotificationEventCallback;
import ar.edu.itba.pod.exceptions.FlightAlreadyConfirmedException;
import ar.edu.itba.pod.exceptions.NoSuchFlightException;
import ar.edu.itba.pod.model.Category;
import ar.edu.itba.pod.model.Section;
import ar.edu.itba.pod.model.Ticket;
import ar.edu.itba.pod.server.Servant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class FlightNotificationServiceTest {

    Servant servant = new Servant();

    private final String planeName = "TEST";
    private final String flightCode = "TEST";
    private final String flightCode2 = "TEST-2";
    private final String passengerName1 = "A";
    private final String passengerName2 = "Z";
    private final String destinationCode = "TEST";


    private final Section business = new Section(Category.BUSINESS, 3, 5);
    private final Section economy = new Section(Category.ECONOMY, 5, 10);
    private final List<Section> sectionList = Arrays.asList(business, economy);

    private final Ticket ticket = new Ticket(passengerName1, Category.BUSINESS);
    private final Ticket ticket2 = new Ticket(passengerName2, Category.ECONOMY);
    private final List<Ticket> tickets = Arrays.asList(ticket, ticket2);
    private final NotificationEventCallback callback = new NotificationEventCallback() {
        @Override
        public void successfulRegistration(String flightCode, String destinationCode) throws RemoteException {

        }

        @Override
        public void confirmedFlight(String flightCode, String destinationCode, int row, char column, String category) throws RemoteException {

        }

        @Override
        public void confirmedFlight(String flightCode, String destinationCode, String category) throws RemoteException {

        }

        @Override
        public void cancelledFlight(String flightCode, String destinationCode, int row, char column, String category) throws RemoteException {

        }

        @Override
        public void cancelledFlight(String flightCode, String destinationCode, String category) throws RemoteException {

        }

        @Override
        public void assignedSeat(String flightCode, String destinationCode, int row, char column, String category) throws RemoteException {

        }

        @Override
        public void movedSeat(String flightCode, String destinationCode, String category, int row, char column, String oldCategory, int oldRow, char oldColumn) throws RemoteException {

        }

        @Override
        public void changedTicket(String flightCode, String destinationCode, String newFlightCode) throws RemoteException {

        }

        @Override
        public void unsubscribe() throws RemoteException {

        }
    };

    @Test
    public void registerPassenger() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);

        servant.registerPassenger(flightCode, passengerName1, callback);

        Assertions.assertTrue(servant.getAirport().getSubscribers().containsKey(flightCode));
        Assertions.assertTrue(servant.getAirport().getSubscribers().get(flightCode).containsKey(passengerName1));

        Assertions.assertFalse(servant.getAirport().getSubscribers().containsKey(flightCode2));
        Assertions.assertFalse(servant.getAirport().getSubscribers().get(flightCode).containsKey(passengerName2));
    }

    @Test
    public void registerPassengerInvalidFlight()  {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.registerPassenger(flightCode, passengerName1, callback),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void registerPassengerConfirmedFlight() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, tickets);
        servant.confirmFlight(flightCode);

        FlightAlreadyConfirmedException exception = Assertions.assertThrows(FlightAlreadyConfirmedException.class,
                () -> servant.registerPassenger(flightCode, passengerName1, callback),
                "Should have thrown FlightAlreadyConfirmedException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }
}
