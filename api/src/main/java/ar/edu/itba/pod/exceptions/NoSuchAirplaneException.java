package ar.edu.itba.pod.exceptions;

public class NoSuchAirplaneException extends RuntimeException {
    private String flightCode;
    public NoSuchAirplaneException(String modelName, String flightCode) {
        super("Airplane model " + modelName + " does not exist");
        this.flightCode = flightCode;
    }

    public NoSuchAirplaneException(String modelName) {
        super("Airplane model " + modelName + " does not exist");
    }

    public String getFlightCode() {
        return flightCode;
    }
}
