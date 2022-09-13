package ar.edu.itba.pod.exceptions;

public class FlightAlreadyConfirmedException extends RuntimeException {
    public FlightAlreadyConfirmedException() {
        super("Cannot subscribe to a flight already confirmed");
    }
}
