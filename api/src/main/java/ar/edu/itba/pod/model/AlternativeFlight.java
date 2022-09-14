package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.Objects;

public class AlternativeFlight implements Serializable {

    private final String destinationCode;
    private final String flightCode;
    private final Category category;
    private final long availableSeats;

    public AlternativeFlight(String destinationCode, String flightCode, Category category, long availableSeats) {
        this.destinationCode = destinationCode;
        this.flightCode = flightCode;
        this.category = category;
        this.availableSeats = availableSeats;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public Category getCategory() {
        return category;
    }

    public long getAvailableSeats() {
        return availableSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlternativeFlight that = (AlternativeFlight) o;
        return getAvailableSeats() == that.getAvailableSeats() && Objects.equals(getDestinationCode(), that.getDestinationCode()) && Objects.equals(getFlightCode(), that.getFlightCode()) && getCategory() == that.getCategory();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDestinationCode(), getFlightCode(), getCategory(), getAvailableSeats());
    }
}
