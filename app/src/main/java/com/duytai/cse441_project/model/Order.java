package com.duytai.cse441_project.model;

public class Order {
    private int orderId;
    private int userId;
    private String orderName;
    private String orderPhone;

    private String deliveryAddress;
    private String orderDate;
    private String status;
    private double totalPrice;
    private String paymentMethod;
    private String discountCode;

    public Order() {
        // Constructor rỗng
    }


    public Order(int orderId, int userId, String orderName, String orderPhone, String deliveryAddress, String orderDate, String status, double totalPrice, String paymentMethod, String discountCode) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderName = orderName;
        this.orderPhone = orderPhone;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
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

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
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
