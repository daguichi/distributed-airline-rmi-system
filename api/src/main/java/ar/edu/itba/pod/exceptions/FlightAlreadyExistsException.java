package ar.edu.itba.pod.exceptions;

public class FlightAlreadyExistsException extends RuntimeException {
    private final String flightCode;
    public FlightAlreadyExistsException(String flightCode) {
        super("Flight " + flightCode + " already exists");
        this.flightCode = flightCode;
    }

    public String getFlightCode() {
        return flightCode;
    }
}