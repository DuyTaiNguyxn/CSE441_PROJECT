package com.duytai.cse441_project.model;

public class OrderItem {
    private int orderItemId; // ID của chi tiết đơn hàng
    private int orderId; // ID của đơn hàng
    private int foodId; // ID của món ăn
    private double price; // Giá của món ăn
    private int quantity; // Số lượng món ăn

    // Constructor
    public OrderItem(int orderItemId, int orderId, int foodId, double price, int quantity) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.foodId = foodId;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter và Setter
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
