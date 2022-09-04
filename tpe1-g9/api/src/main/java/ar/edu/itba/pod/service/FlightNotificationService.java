package ar.edu.itba.pod.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightNotificationService extends Remote {

    void registerPassenger(int flightCode, String passengerName) throws RemoteException;

}
