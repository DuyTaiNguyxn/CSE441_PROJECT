package com.duytai.cse441_project.model;

public class Food {
    private int foodId;
    private int categoryId; // Thêm thuộc tính categoryId
    private String foodName;
    private String description;
    private String imgURL;
    private double price;
    private int quantitySold; // Thêm thuộc tính để tính số lượng bán

    public Food() {
        // Constructor mặc định
    }

    public Food(int foodId, int categoryId, String foodName, String description, String imgURL, double price) {
        this.foodId = foodId;
        this.categoryId = categoryId; // Gán giá trị cho categoryId
        this.foodName = foodName;
        this.description = description;
        this.imgURL = imgURL;
        this.price = price;
        this.quantitySold = 0; // Ban đầu số lượng bán là 0
    }

    // Getter và Setter cho các thuộc tính

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId; // Thêm phương thức setCategoryId()
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

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }
}
