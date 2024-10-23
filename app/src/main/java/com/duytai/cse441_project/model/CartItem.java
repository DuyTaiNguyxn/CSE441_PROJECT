package com.duytai.cse441_project.model;

public class CartItem {
    private int cartId; // ID của giỏ hàng
    private int cartItemId; // ID của mục trong giỏ hàng
    private int foodId; // ID của món ăn
    private int quantity; // Số lượng món ăn

    // Constructor mặc định
    public CartItem() {}
    // Constructor có tham số
    public CartItem(int cartItemId, int cartId, int foodId, int quantity) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    // Getter và Setter
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

