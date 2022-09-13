package ar.edu.itba.pod.exceptions;

public class EmptySeatMapException extends RuntimeException {
    public EmptySeatMapException() {
        super("Requested seat map is empty");
    }
}
