package ar.edu.itba.pod.service;

import ar.edu.itba.pod.model.AlternativeFlight;
import ar.edu.itba.pod.model.Seat;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SeatAdministrationService extends Remote {

    Seat isAvailable(String flightCode, int row, char column) throws RemoteException;
    void assignSeat(String flightCode, String passengerName, int row, char column) throws RemoteException;
    void changeSeat(String flightCode, String passengerName, int row, char column) throws RemoteException;
    List<AlternativeFlight> getAlternativeFlights(String flightCode, String passengerName) throws RemoteException;
    void changeFlight(String oldFlightCode, String newFlightCode, String passengerName) throws RemoteException, InterruptedException;

}
