package ar.edu.itba.pod.model;

import ar.edu.itba.pod.model.Ticket;

import java.io.Serializable;
import java.util.Optional;

public class Seat implements Serializable {

    private Ticket ticket;
    private Category category;
    private int row;
    private char column;

    public Seat(Category category) {
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
}
