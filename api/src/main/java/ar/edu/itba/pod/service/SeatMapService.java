package ar.edu.itba.pod.service;

import ar.edu.itba.pod.model.Category;
import ar.edu.itba.pod.model.Row;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SeatMapService extends Remote {
    List<Row> getFlightMap(String flightCode) throws RemoteException;
    List<Row> getFlightMapByCategory(String flightCode, Category category) throws RemoteException;
    Row getFlightMapByRow(String flightCode, int row) throws RemoteException;
}
