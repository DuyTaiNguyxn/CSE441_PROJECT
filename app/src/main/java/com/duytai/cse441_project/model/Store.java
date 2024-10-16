package com.duytai.cse441_project.model;

public class Store {
    private int storeId;
    private String storeName;
    private String location;
    private String locationLink;
    private String phone;
    private String openingHours;

    // Constructor không tham số
    public Store() {
    }

    // Constructor đầy đủ tham số
    public Store(int storeId, String storeName, String location,
                 String locationLink, String phone, String openingHours) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.locationLink = locationLink;
        this.phone = phone;
        this.openingHours = openingHours;
    }

    // Getter và Setter cho các thuộc tính
    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationLink() {
        return locationLink;
    }

    public void setLocationLink(String locationLink) {
        this.locationLink = locationLink;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

}

