package com.duytai.cse441_project.model;

public class Order {
    private int orderId;
    private int userId;
    private String Ordername;
    private String Orderphone;
    private String deliveryAddress;
    private String orderDate;
    private String status;
    private double totalPrice;
    private String paymentMethod;
    private String discountCode;

    public Order(int orderId, int userId, String ordername, String orderphone, String deliveryAddress, String orderDate, String status, double totalPrice, String paymentMethod, String discountCode) {
        this.orderId = orderId;
        this.userId = userId;
        Ordername = ordername;
        Orderphone = orderphone;
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

    public String getOrdername() {
        return Ordername;
    }

    public void setOrdername(String ordername) {
        Ordername = ordername;
    }

    public String getOrderphone() {
        return Orderphone;
    }

    public void setOrderphone(String orderphone) {
        Orderphone = orderphone;
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
