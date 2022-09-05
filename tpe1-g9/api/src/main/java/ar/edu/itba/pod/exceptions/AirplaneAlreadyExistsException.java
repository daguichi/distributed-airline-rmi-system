package ar.edu.itba.pod.exceptions;

public class AirplaneAlreadyExistsException extends RuntimeException {
    public AirplaneAlreadyExistsException(String name) {
        super("Airplane " + name + " already exists");
    }
}
