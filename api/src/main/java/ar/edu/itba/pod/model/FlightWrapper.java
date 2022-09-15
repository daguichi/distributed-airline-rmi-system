package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;

public class FlightWrapper implements Serializable {
    private final String modelName;
    private final String flightCode;
    private final String destinationCode;
    private final List<Ticket> tickets;

    public FlightWrapper(String modelName, String flightCode,
                         String destinationCode, List<Ticket> tickets) {
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
