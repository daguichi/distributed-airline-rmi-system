package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.EmptySeatMapException;
import ar.edu.itba.pod.exceptions.InvalidRowException;
import ar.edu.itba.pod.exceptions.NoSuchFlightException;
import ar.edu.itba.pod.model.*;
import ar.edu.itba.pod.server.Servant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SeatMapServiceTest {

    Servant servant = new Servant();

    private final String planeName = "TEST";
    private final String flightCode = "TEST";
    private final String flightCode2 = "TEST-2";
    private final String passengerName1 = "A";
    private final String passengerName2 = "Z";
    private final String destinationCode = "TEST";
    private final int row = 1;

    private final Section business = new Section(Category.BUSINESS, 1, 1);
    private final Section economy = new Section(Category.ECONOMY, 2, 1);
    private final List<Section> sectionList = Arrays.asList(business, economy);

    private final Row row1 = new Row(Collections.singletonList(new Seat(Category.BUSINESS, 0, 'A')),
            0,Category.BUSINESS);
    private final Row row2 = new Row(Collections.singletonList(new Seat(Category.ECONOMY, 1, 'A')),
            1,Category.ECONOMY);
    private final Row row3 = new Row(Collections.singletonList(new Seat(Category.ECONOMY, 2, 'A')),
            2,Category.ECONOMY);
    private final List<Row> rows = Arrays.asList(row1, row2, row3);

    //TODO NO ANDAN LOS TESTS
    @Test
    public void getFlightMap() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, null);
        Assertions.assertTrue(servant.getFlightMap(flightCode).containsAll(rows));
    }

    @Test
    public void getFlightMapInvalidFlight() throws RemoteException {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.getFlightMap(flightCode),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void getFlightMapByCategory() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, null);
        Assertions.assertEquals(row1, servant.getFlightMapByCategory(flightCode, Category.BUSINESS).get(0));
    }

    @Test
    public void getFlightMapByCategoryInvalidFlight() throws RemoteException {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.getFlightMapByCategory(flightCode, Category.ECONOMY),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }

    @Test
    public void getFlightMapByCategoryInvalid() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, null);
        Assertions.assertThrows(EmptySeatMapException.class,
                () -> servant.getFlightMapByCategory(flightCode, Category.PREMIUM_ECONOMY),
                "Should have thrown EmptySeatMapException");
    }

    @Test
    public void getFlightMapByRow() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, null);
        Assertions.assertEquals(row1, servant.getFlightMapByRow(flightCode, 0));
    }

    @Test
    public void getFlightMapByRowInvalid() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addFlight(planeName, flightCode, destinationCode, null);
        Assertions.assertThrows(InvalidRowException.class,
                () -> servant.getFlightMapByRow(flightCode, 5),
                "Should have thrown InvalidRowException");
    }

    @Test
    public void getFlightMapByRowInvalidFlight() throws RemoteException {
        NoSuchFlightException exception = Assertions.assertThrows(NoSuchFlightException.class,
                () -> servant.getFlightMapByRow(flightCode, row),
                "Should have thrown NoSuchFlightException");
        Assertions.assertTrue(exception.getMessage().contains(flightCode));
    }
}
