package ar.edu.itba.pod.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SeatAdministrationService extends Remote {

    boolean isAvailable(String flightCode, int row, char column) throws RemoteException;
    void assignSeat(String flightCode, String passengerName, int row, char column) throws RemoteException;
    void changeSeat(String flightCode, String passengerName, int row, char column) throws RemoteException;
    void getAlternativeFlights(String flightCode, String passengerName) throws RemoteException;
    void changeFlight(String oldFlightCode, String newFlightCode, String passengerName) throws RemoteException;

}
