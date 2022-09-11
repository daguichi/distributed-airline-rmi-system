package ar.edu.itba.pod.model;

public enum Category {
    ECONOMY("ECONOMY"),
    PREMIUM_ECONOMY("PREMIUM_ECONOMY"),
    BUSINESS("BUSINESS");

    private final String name;

    public String getName() {
        return name;
    }

    Category(String name) {
        this.name = name;
    }
}
