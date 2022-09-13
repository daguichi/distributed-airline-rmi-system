package ar.edu.itba.pod.exceptions;

public class FlightAlreadyConfirmedException extends RuntimeException {
    public FlightAlreadyConfirmedException(String flightCode) {
        super("Flight " + flightCode + " already confirmed");
    }
}

