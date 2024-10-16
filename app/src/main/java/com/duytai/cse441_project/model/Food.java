package com.duytai.cse441_project.model;

public class Food {
    private int foodId;
    private String foodName;
    private String description;
    private String category;
    private double price; // Đảm bảo kiểu dữ liệu là double
    private String imgURL;

    // Constructor
    public Food() {}

    // Getters và Setters
    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price; // Trả về kiểu double
    }

    public void setPrice(double price) {
        this.price = price; // Thiết lập giá
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
