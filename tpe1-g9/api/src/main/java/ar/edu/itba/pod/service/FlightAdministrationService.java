package ar.edu.itba.pod.service;

import ar.edu.itba.pod.model.Flight;
import ar.edu.itba.pod.model.FlightStatus;
import ar.edu.itba.pod.model.Section;
import ar.edu.itba.pod.model.Ticket;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlightAdministrationService extends Remote {

    void addPlaneModel(String name, List<Section> sections) throws RemoteException;
    void addFlight(String modelName, String flightCode, String destinationCode, Map<String, Ticket> tickets) throws RemoteException;
    FlightStatus getFlightStatus(String flightCode) throws RemoteException;
    void cancelFlight(String flightCode) throws RemoteException;
    void confirmFlight(String flightCode) throws RemoteException;
    void reprogramFlightTickets(Flight flight) throws RemoteException;
}
