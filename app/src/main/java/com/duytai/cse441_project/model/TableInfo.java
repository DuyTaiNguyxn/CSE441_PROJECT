package com.duytai.cse441_project.model;

import java.io.Serializable;

public class TableInfo implements Serializable {
    private int tableId;
    private int storeId;
    private int seats;
    private String status;

    // Constructor
    public TableInfo() {
    }

    public TableInfo(int tableId, int storeId, int seats, String status) {
        this.tableId = tableId;
        this.storeId = storeId;
        this.seats = seats;
        this.status = status;
    }

    // Getters and Setters
    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}