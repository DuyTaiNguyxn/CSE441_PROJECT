package com.duytai.cse441_project.model;

public class Order {
    private int orderId;
    private int userId;
    private long orderDate;
    private String status;
    private double totalPrice;
    private String deliveryAddress;
    private String paymentMethod;
    private String discountCode;

    // Constructor
    public Order(int orderId, int userId, long orderDate, String status, double totalPrice, String deliveryAddress, String paymentMethod, String discountCode) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.discountCode = discountCode;
    }

    // Getter và Setter
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
}
