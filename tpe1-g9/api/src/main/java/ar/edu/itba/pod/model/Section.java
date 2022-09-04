package ar.edu.itba.pod.model;

public class Section implements Comparable<Section> {

    private Category category;
    private int rowCount;
    private int columnCount;

    public Section(Category category, int columnCount, int rowCount) {
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
}
