package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.*;

public class Airplane implements Serializable {

    private final String name;
    private Map<Integer, Map<Integer,Seat>> seats;

    public Airplane(String name, List<Section> sections) {
        this.name = name;
        seats = new HashMap<>();
        int rows = 0;
        sections.sort(Comparator.comparing(Section::getCategory).reversed());
        for(Section s : sections) {
            for(int i = rows; i < s.getRowCount() + rows; i++) {
                seats.put(i, new HashMap<>());
                for(int j = 0; j < s.getColumnCount(); j++)
                    seats.get(i).put(j, new Seat(s.getCategory(), null, i, (char) (j + 'A')));
            }
            rows += s.getRowCount();
        }

    }


    public String getName() {
        return name;
    }

    public Map<Integer, Map<Integer, Seat>> getSeats() {
        return seats;
    }

    @Override
    public String toString() {
        return "Airplane{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airplane airplane = (Airplane) o;
        return Objects.equals(getName(), airplane.getName()) && Objects.equals(getSeats(), airplane.getSeats());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSeats());
    }
}
