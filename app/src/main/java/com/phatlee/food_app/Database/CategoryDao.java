package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.phatlee.food_app.Entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Cập nhật nếu ID đã tồn tại
    void insert(Category... categories);

    @Query("SELECT * FROM category")
    List<Category> getAllCategories();
}
