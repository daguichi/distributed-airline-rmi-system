package ar.edu.itba.pod.exceptions;

public class InvalidRowException extends RuntimeException {
    public InvalidRowException(int row, String flightCode) {
        super("Flight " + flightCode + " does not have row " + row);
    }
}
