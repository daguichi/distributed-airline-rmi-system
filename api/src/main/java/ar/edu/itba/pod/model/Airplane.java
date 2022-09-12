package ar.edu.itba.pod.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Airplane  {

    private final String name;
    private Map<Integer, Map<Integer,Seat>> seats;

    public Airplane(String name, List<Section> sections) {
        this.name = name;
        seats = new HashMap<>();
        int rows = 0;
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

}
