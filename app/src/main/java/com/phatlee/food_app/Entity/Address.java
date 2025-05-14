package com.phatlee.food_app.Entity;

import java.io.Serializable;

public class Address implements Serializable {
    private String id;
    private String userId;
    private String name;
    private String phone;
    private String detail; // ví dụ: Số 1 Võ Văn Ngân, Linh Chiểu, Thủ Đức
    private boolean isDefault;

    public Address() {} // Required for Firestore

    public Address(String id, String userId, String name, String phone, String detail, boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.detail = detail;
        this.isDefault = isDefault;
    }

    public String getFullAddress() {
        return detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
