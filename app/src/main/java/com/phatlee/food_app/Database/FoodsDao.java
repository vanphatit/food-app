package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.phatlee.food_app.Entity.Foods;

import java.util.List;

@Dao
public interface FoodsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Cập nhật nếu ID đã tồn tại
    void insert(Foods... foods);

    @Query("SELECT * FROM foods")
    List<Foods> getAllFoods();

    @Query("SELECT * FROM foods WHERE id = :id")
    Foods getFoodById(int id);

    @Query("SELECT * FROM foods WHERE categoryId = :categoryId")
    List<Foods> getFoodsByCategory(int categoryId);

    @Query("SELECT * FROM Foods WHERE Title LIKE :searchText")
    List<Foods> getFoodsBySearch(String searchText);

    @Query("SELECT * FROM foods WHERE bestFood = 1")
    List<Foods> getBestFoods();

    @Update
    void update(Foods food);

}
