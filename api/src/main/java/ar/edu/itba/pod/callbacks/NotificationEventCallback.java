package ar.edu.itba.pod.callbacks;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationEventCallback extends Remote, Serializable {
    void successfullRegistration(String flightCode, String destinationCode ) throws RemoteException;

    void confirmedFlight(String flightCode, String destinationCode) throws RemoteException;

    void cancelledFlight(String flightCode, String destinationCode) throws RemoteException;

    void assignedSeat(String flightCode, String destinationCode, String category, int row, char column ) throws RemoteException;

    void assignedSeat(String flightCode, String destinationCode, int row, char column) throws RemoteException;

    void movedSeat(String flightCode, String destinationCode, String category, int row, char column, String oldCategory, int oldRow, char oldColumn ) throws RemoteException;

    void changedTicket(String flightCode, String destinationCode,String newFlightCode) throws RemoteException;
}
