package ar.edu.itba.pod.model;

import java.util.Objects;

public class Ticket {
    private String passengerName;
    private Category category;

    public Ticket(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(getPassengerName(), ticket.getPassengerName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassengerName());
    }
}
