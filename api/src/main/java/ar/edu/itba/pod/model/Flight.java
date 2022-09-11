package ar.edu.itba.pod.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Flight {

    private Airplane airplane;
    private String flightCode;
    private String destinationCode;
    private List<Ticket> tickets;
    private FlightStatus status;

    public Flight(Airplane airplane, String flightCode, String destinationCode, List<Ticket> tickets, FlightStatus status) {
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

    public String getFlightCode() {
        return flightCode;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public long availableSeats() {
        return airplane.getSeats().values().stream().flatMap(row -> row.values().stream()).filter(Seat::isAvailable).count();
    }
}
