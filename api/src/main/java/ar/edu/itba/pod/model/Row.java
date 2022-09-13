package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;

public class Row implements Serializable {

    private List<Seat> seatList;
    private int row;
    private Category category;

    public Row(List<Seat> seatList, int row, Category category) {
        this.seatList = seatList;
        this.row = row;
        this.category = category;
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public int getRow() {
        return row;
    }

    public Category getCategory() {
        return category;
    }
}
