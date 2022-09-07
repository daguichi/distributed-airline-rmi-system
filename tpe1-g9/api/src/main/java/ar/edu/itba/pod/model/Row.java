package ar.edu.itba.pod.model;

import java.util.List;

public class Row {
    private final int rowNumber;
    private final List<Ticket> tickets;

    private final Category category;

    public Category getCategory() {
        return category;
    }

    public Row(int rowNumber, List<Ticket> tickets, Category category) {
        this.rowNumber = rowNumber;
        this.tickets = tickets;
        this.category = category;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
