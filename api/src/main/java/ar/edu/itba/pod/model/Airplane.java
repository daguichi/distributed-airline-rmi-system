package ar.edu.itba.pod.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Airplane  {

    private String name;
    private List<Section> sections;
    private int totalSeats;

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
}
