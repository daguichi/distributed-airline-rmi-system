package ar.edu.itba.pod.model;

public class Section {

    private Category category;
    private int columnCount;
    private int rowCount;

    public Section(Category category, int columnCount, int rowCount) {
        this.category = category;
        this.columnCount = columnCount;
        this.rowCount = rowCount;
    }
}
