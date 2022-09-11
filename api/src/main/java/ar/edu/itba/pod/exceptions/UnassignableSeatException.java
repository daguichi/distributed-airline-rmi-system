package ar.edu.itba.pod.exceptions;

public class UnassignableSeatException extends RuntimeException {
    public UnassignableSeatException() {
        super("The specified seat can not be assigned to the passenger.");
    }
}
