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
    @PrimaryKey(autoGenerate = true)
    public int cartId;
    public int userId;
    public int foodId;
    public int quantity;

    public Cart(int userId, int foodId, int quantity) {
        this.userId = userId;
        this.foodId = foodId;
        this.quantity = quantity;
    }
}