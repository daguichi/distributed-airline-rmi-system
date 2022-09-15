package ar.edu.itba.pod.exceptions;

public class InvalidSectionException extends RuntimeException {
    private final String planeModel;
    public InvalidSectionException(String planeModel) {
        super("Invalid section, row and column count must be greater than zero.");
        this.planeModel = planeModel;
    }

    public String getPlaneModel() {
        return planeModel;
    }
}
