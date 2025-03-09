package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.phatlee.food_app.Entity.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE foodId = :foodId")
    List<Review> getReviewsByFoodId(int foodId);

    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId AND id IN (SELECT orderId FROM order_items WHERE foodId = :foodId)")
    int hasUserBoughtFood(int userId, int foodId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Review review);

    @Query("SELECT AVG(rating) FROM reviews WHERE foodId = :foodId")
    float getAverageRating(int foodId);

}
