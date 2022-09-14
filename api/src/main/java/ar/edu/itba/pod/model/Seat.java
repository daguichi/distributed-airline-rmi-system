package ar.edu.itba.pod.model;

import ar.edu.itba.pod.model.Ticket;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class Seat implements Serializable {

    private Ticket ticket;
    private Category category;
    private int row;
    private char column;

    public Seat(Category category, int row, char column) {
        this.row = row;
        this.column = column;
        this.category = category;
    }

    public Seat(Category category, Ticket ticket, int row, char column) {
        this.category = category;
        this.ticket = ticket;
        this.row = row;
        this.column = column;
    }

    public Optional<Ticket> getTicket() {
        return Optional.ofNullable(ticket);
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public boolean isAvailable() {
        return !getTicket().isPresent();
    }

    public int getRow() {
        return row;
    }
    public char getColumn() {
        return column;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "ticket=" + ticket +
                ", category=" + category +
                ", row=" + row +
                ", column=" + column +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return getRow() == seat.getRow() && getColumn() == seat.getColumn() && Objects.equals(getTicket(), seat.getTicket()) && getCategory() == seat.getCategory();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTicket(), getCategory(), getRow(), getColumn());
    }
}
