package ar.edu.itba.pod.model;

public class Ticket {
    private String passengerName;
    private Category category;
    private int row;
    private char column;

    public Ticket(String passengerName, Category category, int row, char column) {
        this.passengerName = passengerName;
        this.category = category;
        this.row = row;
        this.column = column;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public Category getCategory() {
        return category;
    }

    public int getRow() {
        return row;
    }

    public char getColumn() {
        return column;
    }
}
