package ar.edu.itba.pod.service;

import ar.edu.itba.pod.model.Category;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SeatMapService extends Remote {
    void getFlightMap(int flightCode) throws RemoteException;
    void getFlightMapByCategory(int flightCode, Category category) throws RemoteException;
    void getFlightMapByRow(int flightCode, int row) throws RemoteException;
}
