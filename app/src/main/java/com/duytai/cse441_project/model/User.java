package com.duytai.cse441_project.model;

public class User {
    private int userId;
    private String phone;
    private String password;
    private String email;
    private String address;
    private String role;
    private String avatar_img_url;
    private int rewardPoints;

    public User() {
        // Constructor mặc định
    }

    public User(int userId, String phone, String password, String address, String email, String role, String avatar_img_url,int rewardPoints) {
        this.userId = userId;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.email = email;
        this.role = role;
        this.avatar_img_url = avatar_img_url;
        this.rewardPoints = rewardPoints;

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar_img_url() {
        return avatar_img_url;
    }

    public void setAvatar_img_url(String avatar_img_url) {
        this.avatar_img_url = avatar_img_url;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
