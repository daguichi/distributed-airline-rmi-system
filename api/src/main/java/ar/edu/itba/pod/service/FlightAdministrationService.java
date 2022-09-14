package ar.edu.itba.pod.service;

import ar.edu.itba.pod.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FlightAdministrationService extends Remote {

    AirplaneWrapper addPlaneModel(String name, List<Section> sections) throws RemoteException;
    FlightWrapper addFlight(String modelName, String flightCode, String destinationCode, List<Ticket> tickets) throws RemoteException;
    FlightStatus getFlightStatus(String flightCode) throws RemoteException;
    FlightStatus cancelFlight(String flightCode) throws RemoteException;
    FlightStatus confirmFlight(String flightCode) throws RemoteException;
    ReticketWrapper reprogramFlightsTickets() throws RemoteException;
}
