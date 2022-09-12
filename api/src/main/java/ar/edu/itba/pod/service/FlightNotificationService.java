package ar.edu.itba.pod.service;

import ar.edu.itba.pod.callbacks.NotificationEventCallback;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightNotificationService extends Remote {

    void registerPassenger(String flightCode, String passengerName, NotificationEventCallback callback) throws RemoteException;
}
