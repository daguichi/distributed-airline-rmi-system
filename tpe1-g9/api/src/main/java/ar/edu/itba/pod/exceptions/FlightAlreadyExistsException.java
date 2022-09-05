package ar.edu.itba.pod.exceptions;

public class FlightAlreadyExistsException extends RuntimeException {
    public FlightAlreadyExistsException(String flightCode) {
        super("Flight " + flightCode + " already exists");
    }
}