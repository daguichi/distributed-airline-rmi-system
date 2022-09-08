package ar.edu.itba.pod.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightNotificationService extends Remote {

    void registerPassenger(String flightCode, String passengerName) throws RemoteException;

}
