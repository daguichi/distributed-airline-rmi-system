package ar.edu.itba.pod.exceptions;

public class FlightAlreadyExists extends RuntimeException {
    public FlightAlreadyExists(String flightCode) {
        super("Flight " + flightCode + " already exists");
    }
}