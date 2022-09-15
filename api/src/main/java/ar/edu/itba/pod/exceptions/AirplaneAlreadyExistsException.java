package ar.edu.itba.pod.exceptions;

public class AirplaneAlreadyExistsException extends RuntimeException {
    private String planeModel;
    public AirplaneAlreadyExistsException(String name) {
        super("Airplane " + name + " already exists");
        this.planeModel = name;
    }

    public String getPlaneModel() {
        return planeModel;
    }
}