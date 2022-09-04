package ar.edu.itba.pod.model;

import java.util.Map;
import java.util.Set;

public class Flight {

    private Airplane airplane;
    private String flightCode;
    private String destinationCode;
    private Map<String, Ticket> tickets;
    private FlightStatus status;

    public Flight(Airplane airplane, String flightCode, String destinationCode, Map<String, Ticket> tickets, FlightStatus status) {
        this.airplane = airplane;
        this.flightCode = flightCode;
        this.destinationCode = destinationCode;
        this.tickets = tickets;
        this.status = status;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public Map<String, Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Map<String, Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "airplane=" + airplane +
                ", flightCode='" + flightCode + '\'' +
                ", destinationCode='" + destinationCode + '\'' +
                ", tickets=" + tickets +
                '}';
    }
}
