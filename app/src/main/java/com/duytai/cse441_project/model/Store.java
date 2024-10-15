package com.duytai.cse441_project.model;

public class Store {
    private String name;
    private int table;
    private int imageResId;

    public Store(String name, int table, int imageResId) {
        this.name = name;
        this.table = table;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getTable() {
        return table;
    }

    public int getImageResId() {
        return imageResId;
    }
}
