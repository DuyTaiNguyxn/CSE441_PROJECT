package com.duytai.cse441_project.model;

public class Reservation {
    private int reservationId, userId, tableInfoId ;
    private String reservationDate, reservationTime, note;

    public Reservation(int reservationId, int userId, int tableInfoId, String reservationDate, String reservationTime, String note) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.tableInfoId = tableInfoId;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.note = note;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTableInfoId() {
        return tableInfoId;
    }

    public void setTableInfoId(int tableInfoId) {
        this.tableInfoId = tableInfoId;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

