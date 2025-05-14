package com.phatlee.food_app.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items")
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String orderId; // ID của đơn hàng
    public int foodId;  // ID của món ăn
    public int quantity; // Số lượng món ăn

    public OrderItem(String orderId, int foodId, int quantity) {
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    // Constructor không tham số – cần cho Firestore deserialization
    public OrderItem() {
    }
}
