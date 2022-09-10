package ar.edu.itba.pod.model;

import ar.edu.itba.pod.model.Ticket;

import java.util.Optional;

public class Seat {

    private Ticket ticket;
    private Category category;

    public Seat(Category category) {
        this.category = category;
    }

    public Seat(Category category, Ticket ticket){
        this.category = category;
        this.ticket = ticket;
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

    public Category getCategory() {
        return category;
    }
}
