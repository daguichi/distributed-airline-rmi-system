package ar.edu.itba.pod.model;

public class Ticket {
    private String passengerName;
    private Category category;

    public Ticket(String passengerName, Category category) {
        this.passengerName = passengerName;
        this.category = category;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public Category getCategory() {
        return category;
    }
}
