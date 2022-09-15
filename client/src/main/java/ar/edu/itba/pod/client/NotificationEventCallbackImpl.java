package ar.edu.itba.pod.client;

import ar.edu.itba.pod.callbacks.NotificationEventCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.rmi.RemoteException;

public class NotificationEventCallbackImpl implements NotificationEventCallback {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventCallback.class);

    @Override
    public void successfulRegistration(String flightCode, String destinationCode ) throws RemoteException {
        logger.info("You are following Flight "+ flightCode +" with destination " + destinationCode + ".");
    }

    @Override
    public void confirmedFlight(String flightCode, String destinationCode, int row, char column, String category) throws RemoteException {
        logger.info("Your Flight "+ flightCode+ " with destination "+ destinationCode+" was confirmed and your seat is "+ category+" "+(row+1)+column+ ".");
    }

    @Override
    public void confirmedFlight(String flightCode, String destinationCode, String category) throws RemoteException {
        logger.info("Your Flight "+ flightCode+ " with destination "+ destinationCode+" was confirmed and your category is " +category +".");
        unsubscribe();
    }

    @Override
    public void cancelledFlight(String flightCode, String destinationCode, int row, char column, String category) throws RemoteException {
        logger.info("Your Flight "+ flightCode+" with destination "+ destinationCode+ " was cancelled and your seat is "+ category+" "+(row+1)+column+ ".");

    }

    @Override
    public void cancelledFlight(String flightCode, String destinationCode, String category) throws RemoteException {
        logger.info("Your Flight "+ flightCode+" with destination "+ destinationCode+ " was cancelled and your category is " +category +".");

    }

    @Override
    public void assignedSeat(String flightCode, String destinationCode, int row, char column, String category) throws RemoteException {
        logger.info("Your seat is "+category+" "+(row+1)+column +" for Flight "+flightCode+" with destination "+ destinationCode+ ".");

    }

    @Override
    public void movedSeat(String flightCode, String destinationCode, String category, int row, char column, String oldCategory, int oldRow, char oldColumn) throws RemoteException {
        logger.info("Your seat changed to "+category+" "+(row+1)+column+" from "+ oldCategory+" "+(oldRow+1)+oldColumn+" for Flight "+ flightCode+ " with destination "+ destinationCode + ".");

    }

    @Override
    public void changedTicket(String flightCode, String destinationCode,String newFlightCode) throws RemoteException {
       logger.info("Your ticket changed to Flight "+ newFlightCode+" with destination "+ destinationCode+ " from Flight "+ flightCode+ " with destination "+ destinationCode+ ".");
    }

    @Override
    public void unsubscribe() throws RemoteException {
        Thread exitThread = new Thread(() -> {
            System.exit(0);
        });
        exitThread.start();
    }
}
