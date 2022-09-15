package ar.edu.itba.pod.model;

import java.io.Serializable;

public class Section implements Comparable<Section>, Serializable {

    private Category category;
    private final int rowCount;
    private final int columnCount;

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

    public int getColumnCount() {
        return columnCount;
    }

}
