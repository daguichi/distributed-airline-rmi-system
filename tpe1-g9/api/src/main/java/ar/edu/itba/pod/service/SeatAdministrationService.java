package ar.edu.itba.pod.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SeatAdministrationService extends Remote {

    void isAvailable(int flightCode, String passengerName, int row, char column) throws RemoteException;
    void assignSeat(int flightCode, String passengerName, int row, char column) throws RemoteException;
    void changeSeat(int flightCode, String passengerName, int row, char column) throws RemoteException;
    void getAlternativeFlights(int flightCode, String passengerName) throws RemoteException;
    void changeFlight(int oldFlightCode, int newFlightCode, String passengerName) throws RemoteException;

}
