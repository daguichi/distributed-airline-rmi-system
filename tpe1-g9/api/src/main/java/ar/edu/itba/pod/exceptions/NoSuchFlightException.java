package ar.edu.itba.pod.exceptions;

public class NoSuchFlightException extends RuntimeException {
    public NoSuchFlightException(String flightCode) {
        super("Flight " + flightCode + " does not exist");
    }
}
