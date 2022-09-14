package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Row row1 = (Row) o;
        return getRow() == row1.getRow() && Objects.equals(getSeatList(), row1.getSeatList()) && getCategory() == row1.getCategory();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeatList(), getRow(), getCategory());
    }
}
