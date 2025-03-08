package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.phatlee.food_app.Entity.Cart;

import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Cart cart);

    @Update
    void update(Cart cart);

    @Delete
    void delete(Cart cart);

    @Query("DELETE FROM cart WHERE userId = :userId AND foodId = :foodId")
    void removeFromCart(int userId, int foodId);

    @Query("SELECT * FROM cart WHERE userId = :userId AND foodId = :foodId LIMIT 1")
    Cart getCartItem(int userId, int foodId);

    @Query("SELECT * FROM cart WHERE userId = :userId")
    List<Cart> getCartByUser(int userId);

    // clearCartByUser
    @Query("DELETE FROM cart WHERE userId = :userId")
    void clearCartByUser(int userId);

    @Query("UPDATE cart SET quantity = quantity + 1 WHERE userId = :userId AND foodId = :foodId")
    void increaseQuantity(int userId, int foodId);

    @Query("UPDATE cart SET quantity = quantity - 1 WHERE userId = :userId AND foodId = :foodId AND quantity > 1")
    void decreaseQuantity(int userId, int foodId);
}