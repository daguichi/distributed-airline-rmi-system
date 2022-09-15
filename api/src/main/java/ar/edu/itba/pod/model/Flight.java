package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Flight  implements Serializable {

    private final Airplane airplane;
    private final String flightCode;
    private final String destinationCode;
    private final List<Ticket> tickets;
    private FlightStatus status;
    private final ReentrantReadWriteLock lock;

    public Flight(Airplane airplane, String flightCode, String destinationCode, List<Ticket> tickets, FlightStatus status) {
        this.airplane = airplane;
        this.flightCode = flightCode;
        this.destinationCode = destinationCode;
        this.tickets = tickets;
        this.status = status;
        this.lock =  new ReentrantReadWriteLock(true);
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

    public ReentrantReadWriteLock getLock() {
        return lock;
    }
}
