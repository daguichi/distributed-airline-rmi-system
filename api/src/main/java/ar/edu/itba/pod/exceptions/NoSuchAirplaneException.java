package ar.edu.itba.pod.exceptions;

public class NoSuchAirplaneException extends RuntimeException {
    public NoSuchAirplaneException(String modelName) {
        super("Airplane model " + modelName + " does not exist");
    }
}
