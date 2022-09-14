package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Section implements Comparable<Section>, Serializable {

    private Category category;
    private int rowCount;
    private int columnCount;
    private Map<Integer, Map<Integer, Ticket>> seatMap;

    public Section(Category category,  int rowCount, int columnCount) {
        this.category = category;
        this.columnCount = columnCount;
        this.rowCount = rowCount;
    }

    @Override
    public int compareTo(Section o) {
        return this.category.compareTo(o.category);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getTotalSeats() {
        return rowCount * columnCount;
    }

    public Map<Integer, Map<Integer, Ticket>> getSeatMap() {
        return seatMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return getRowCount() == section.getRowCount() && getColumnCount() == section.getColumnCount() && getCategory() == section.getCategory() && Objects.equals(getSeatMap(), section.getSeatMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategory(), getRowCount(), getColumnCount(), getSeatMap());
    }
}
