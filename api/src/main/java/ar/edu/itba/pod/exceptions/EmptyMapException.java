package ar.edu.itba.pod.exceptions;

public class EmptyMapException extends RuntimeException {
    public EmptyMapException() {
        super("Requested seat map is empty");
    }
}
