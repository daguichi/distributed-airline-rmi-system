package ar.edu.itba.pod.model;

import java.util.List;

public class FlightWrapper {
    private String modelName;
    private String flightCode;
    private String destinationCode;
    private List<Ticket> tickets;

    public FlightWrapper(String modelName, String flightCode, String destinationCode, List<Ticket> tickets) {
        this.modelName = modelName;
        this.flightCode = flightCode;
        this.destinationCode = destinationCode;
        this.tickets = tickets;
    }

    public String getModelName() {
        return modelName;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
