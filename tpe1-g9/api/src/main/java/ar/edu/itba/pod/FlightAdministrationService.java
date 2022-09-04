package ar.edu.itba.pod;

import ar.edu.itba.pod.model.Flight;
import ar.edu.itba.pod.model.Section;
import ar.edu.itba.pod.model.Ticket;

import java.util.List;

public interface FlightAdministrationService {

    void addPlaneModel(String name, List<Section> sections);
    void addFlight(String modelName, int flightCode, int destinationCode, List<Ticket> tickets);
    void getFlightStatus(int flightCode);
    void cancelFlight(int flightCode);
    void confirmFlight(int flightCode);
    void reprogramFlightTickets(Flight flight);
}
