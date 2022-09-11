package ar.edu.itba.pod.exceptions;

public class NoTicketException extends RuntimeException {
    public NoTicketException(String passengerName, String flightCode) {
        super("Passenger " + passengerName + " does not have a ticket in flight " + flightCode);
    }
}
