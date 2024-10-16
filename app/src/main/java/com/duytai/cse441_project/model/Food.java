package com.duytai.cse441_project.model;

// Food.java
public class Food {
    private String foodId;
    private String foodName;
    private String description;
    private String imgURL;
    private double price;

    // Constructor
    public Food(String foodId, String foodName, String description, String imgURL, double price) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.description = description;
        this.imgURL = imgURL;
        this.price = price;
    }

    // Getters
    public String getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getDescription() {
        return description;
    }

    public String getImgURL() {
        return imgURL;
    }

    public double getPrice() {
        return price;
    }
}
