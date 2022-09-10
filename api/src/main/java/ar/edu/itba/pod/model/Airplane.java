package ar.edu.itba.pod.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Airplane  {

    private final String name;
    private List<Section> sections;
    private int totalSeats;
    private int occupiedSeats;
    private HashMap<Integer, HashMap<Integer,Seat>> seats;

    public Airplane(String name, List<Section> sections) {
        this.name = name;
        this.sections = sections.stream().sorted().collect(Collectors.toList());
        this.totalSeats = sections.stream().mapToInt(Section::getTotalSeats).sum();
    }

    public List<Section> getSections() {
        return sections;
    }

    public String getName() {
        return name;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public HashMap<Integer, HashMap<Integer, Seat>> getSeats() {
        return seats;
    }

    public int getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(int occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }
}
