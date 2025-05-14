package com.phatlee.food_app.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Foods.class, parentColumns = "id", childColumns = "foodId", onDelete = ForeignKey.CASCADE)
        })
public class Cart {
    public String cartId;
    public String userId;
    public int foodId;
    public int quantity;

    public Cart(String userId, int foodId, int quantity) {
        this.userId = userId;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    // Constructor không tham số – cần cho Firestore deserialization
    public Cart() {
    }

    public String getCartId() {
        return cartId;
    }
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}