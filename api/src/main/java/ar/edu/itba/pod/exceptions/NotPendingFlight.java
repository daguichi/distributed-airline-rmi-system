package ar.edu.itba.pod.exceptions;

public class NotPendingFlight extends RuntimeException {
    public NotPendingFlight(String flightCode) {
        super("Flight " + flightCode  +" is not pending!");
    }
}
