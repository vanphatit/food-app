package com.phatlee.food_app.Entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "wishlist",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Foods.class, parentColumns = "id", childColumns = "foodId", onDelete = CASCADE)
        })
public class Wishlist {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int foodId;

    public Wishlist(int userId, int foodId) {
        this.userId = userId;
        this.foodId = foodId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
}
