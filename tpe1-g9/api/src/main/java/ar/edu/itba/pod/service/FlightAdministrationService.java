package ar.edu.itba.pod.service;

import ar.edu.itba.pod.model.Flight;
import ar.edu.itba.pod.model.Section;
import ar.edu.itba.pod.model.Ticket;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FlightAdministrationService extends Remote {

    void addPlaneModel(String name, List<Section> sections) throws RemoteException;
    void addFlight(String modelName, int flightCode, int destinationCode, List<Ticket> tickets) throws RemoteException;
    void getFlightStatus(int flightCode) throws RemoteException;
    void cancelFlight(int flightCode) throws RemoteException;
    void confirmFlight(int flightCode) throws RemoteException;
    void reprogramFlightTickets(Flight flight) throws RemoteException;
}
