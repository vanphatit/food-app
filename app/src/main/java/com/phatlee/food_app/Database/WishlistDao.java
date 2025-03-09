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

    @Query("SELECT * FROM wishlist WHERE userId = :userId")
    List<Wishlist> getUserWishlist(int userId);

    @Query("SELECT * FROM wishlist WHERE userId = :userId AND foodId = :foodId LIMIT 1")
    Wishlist getWishlistItem(int userId, int foodId);
}
