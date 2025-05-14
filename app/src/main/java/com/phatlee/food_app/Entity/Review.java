package com.phatlee.food_app.Entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Foods.class, parentColumns = "id", childColumns = "foodId", onDelete = CASCADE)
        },
        indices = {@Index("userId"), @Index("foodId")})
public class Review {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userId;
    public int foodId;
    public float rating;
    public String comment;

    public Review(String userId, int foodId, float rating, String comment) {
        this.userId = userId;
        this.foodId = foodId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review() {
    }
}