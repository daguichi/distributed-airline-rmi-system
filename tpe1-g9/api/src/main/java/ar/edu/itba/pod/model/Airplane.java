package ar.edu.itba.pod.model;

import java.util.Collections;
import java.util.List;

public class Airplane {

    private String name;
    private List<Section> sections;

    public Airplane(String name, List<Section> sections) {
        this.name = name;
        this.sections = sections;
    }

    public Airplane(String name, Section section) {
        this.name = name;
        this.sections = Collections.singletonList(section);
    }
}
