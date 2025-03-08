package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import com.phatlee.food_app.Entity.Price;

@Dao
public interface PriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Cập nhật nếu ID đã tồn tại
    void insert(Price... prices);

    @Query("SELECT * FROM price")
    List<Price> getAllPrices();
}
