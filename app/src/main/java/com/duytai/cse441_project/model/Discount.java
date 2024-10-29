package com.duytai.cse441_project.model;

import java.io.Serializable;

public class Discount implements Serializable {
    private int discountId;
    private String discountCode;
    private String discountDescription;
    private double discountPercentage;
    private String expiryDate;

    // Constructor
    public Discount() {
    }

    public Discount(int discountId, String discountCode, String discountDescription, double discountPercentage, String expiryDate) {
        this.discountId = discountId;
        this.discountCode = discountCode;
        this.discountDescription = discountDescription;
        this.discountPercentage = discountPercentage;
        this.expiryDate = expiryDate;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
