package ar.edu.itba.pod.service;

import ar.edu.itba.pod.model.Category;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SeatMapService extends Remote {
    void getFlightMap(String flightCode) throws RemoteException;
    void getFlightMapByCategory(String flightCode, Category category) throws RemoteException;
    void getFlightMapByRow(String flightCode, int row) throws RemoteException;
}
