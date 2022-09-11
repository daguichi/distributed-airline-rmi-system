package ar.edu.itba.pod.exceptions;

public class InvalidAlternativeFlightException extends RuntimeException {
    public InvalidAlternativeFlightException(String flightCode) {
        super("Flight " + flightCode + " is not a valid alternative flight");
    }
}