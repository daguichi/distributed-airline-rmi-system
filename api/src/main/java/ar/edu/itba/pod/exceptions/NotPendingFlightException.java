package ar.edu.itba.pod.exceptions;

public class NotPendingFlightException extends RuntimeException {
    public NotPendingFlightException(String flightCode) {
        super("Flight " + flightCode  +" is not pending!");
    }
}
