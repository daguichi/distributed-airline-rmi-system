package ar.edu.itba.pod.exceptions;

public class InvalidAirplaneException extends RuntimeException {
    public InvalidAirplaneException() {
        super("Invalid airplane, specify a name and at least one section with a single row and column.");
    }
}
