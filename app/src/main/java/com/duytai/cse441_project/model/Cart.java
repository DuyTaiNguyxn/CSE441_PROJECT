package com.duytai.cse441_project.model;

public class Cart {
    private int cartId;
    private int userId;

    // Constructor mặc định
    public Cart() {}
    // Constructor có tham số
    public Cart(int cartId, int userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    // Getter và Setter
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
