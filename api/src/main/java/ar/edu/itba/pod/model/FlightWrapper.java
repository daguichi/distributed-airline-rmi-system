package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;

public class FlightWrapper implements Serializable {
    private String modelName;
    private String flightCode;
    private String destinationCode;
    private List<Ticket> tickets;
    private boolean valid;

    public FlightWrapper(String modelName, String flightCode, String destinationCode, List<Ticket> tickets, boolean valid) {
        this.modelName = modelName;
        this.flightCode = flightCode;
        this.destinationCode = destinationCode;
        this.tickets = tickets;
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
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
