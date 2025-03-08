package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import com.phatlee.food_app.Entity.Time;

@Dao
public interface TimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Cập nhật nếu ID đã tồn tại
    void insert(Time... times);

    @Query("SELECT * FROM time")
    List<Time> getAllTimes();
}
