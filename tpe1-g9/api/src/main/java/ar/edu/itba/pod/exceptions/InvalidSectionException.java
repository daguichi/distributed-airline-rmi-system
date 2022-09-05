package ar.edu.itba.pod.exceptions;

public class InvalidSectionException extends RuntimeException {
    public InvalidSectionException() {
        super("Invalid section, row and column count must be greater than zero.");
    }
}
