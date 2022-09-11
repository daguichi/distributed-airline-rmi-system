package ar.edu.itba.pod.exceptions;

public class InvalidSeatException extends RuntimeException {
    public InvalidSeatException(int row, char column) {
        super("Invalid seat, no seat with row " + row +  " and column " + column);
    }
}
