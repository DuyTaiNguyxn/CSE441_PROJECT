package com.duytai.cse441_project.model;

public class Reservation {
    private int reservationId, userId, storeId, numOfGuests;
    private String reservationDate, status;

    public Reservation(int reservationId, int userId, int storeId, String reservationDate, int numOfGuests, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.storeId = storeId;
        this.reservationDate = reservationDate;
        this.numOfGuests = numOfGuests;
        this.status = status;
    }

    public int getReservationId() { return reservationId; }
    public int getUserId() { return userId; }
    public int getStoreId() { return storeId; }
    public String getReservationDate() { return reservationDate; }
    public int getNumOfGuests() { return numOfGuests; }
    public String getStatus() { return status; }
}

