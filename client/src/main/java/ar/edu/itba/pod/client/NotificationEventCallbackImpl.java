package ar.edu.itba.pod.client;

import ar.edu.itba.pod.callbacks.NotificationEventCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class NotificationEventCallbackImpl implements NotificationEventCallback {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventCallback.class);

    @Override
    public void successfulRegistration(String flightCode, String destinationCode ) throws RemoteException {
        logger.info("You are following Flight "+ flightCode +" with destination " + destinationCode);
    }

    @Override
    public void confirmedFlight(String flightCode, String destinationCode) throws RemoteException {
        logger.info("Flight"+ flightCode + "with destination "+ destinationCode + "was confirmed");
    }

    @Override
    public void cancelledFlight(String flightCode, String destinationCode) throws RemoteException {
        logger.info("Flight"+ flightCode + "with destination "+ destinationCode + "was canceled");

    }


    @Override
    public void assignedSeat(String flightCode, String destinationCode, int row, char column) throws RemoteException {
        logger.info("Flight"+ flightCode + "with destination "+ destinationCode + "have assigned a seat with category " + "in"+ row+'|'+column);//TODO: CATEGORIA Y ASIENTOSS?

    }

    @Override
    public void movedSeat(String flightCode, String destinationCode, String category, int row, char column, String oldCategory, int oldRow, char oldColumn) throws RemoteException {
        logger.info("Flight"+ flightCode + "with destination "+ destinationCode + "have changed the seat "+oldCategory+'|'+ oldRow+'|'+oldColumn+" to "+category+'|'+row+'|'+column);

    }

    @Override
    public void changedTicket(String flightCode, String destinationCode,String newFlightCode) throws RemoteException {
       logger.info("Flight"+ flightCode + "with destination "+ destinationCode + "have changed the ticket to flight "+newFlightCode);

    }
}
