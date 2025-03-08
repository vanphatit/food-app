package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Wishlist;

import java.util.List;

@Dao
public interface WishlistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addToWishlist(Wishlist wishlist);

    @Query("DELETE FROM wishlist WHERE userId = :userId AND foodId = :foodId")
    void removeFromWishlist(int userId, int foodId);

    @Query("SELECT foodId FROM wishlist WHERE userId = :userId")
    List<Integer> getUserWishlist(int userId);

    @Query("SELECT * FROM foods WHERE id IN (SELECT foodId FROM wishlist WHERE userId = :userId)")
    List<Foods> getFavoriteFoods(int userId);
}
