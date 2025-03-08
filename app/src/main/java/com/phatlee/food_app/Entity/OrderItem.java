package com.phatlee.food_app.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items")
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int orderId; // ID của đơn hàng
    public int foodId;  // ID của món ăn
    public int quantity; // Số lượng món ăn

    public OrderItem(int orderId, int foodId, int quantity) {
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
    }
}
