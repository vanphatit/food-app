package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.phatlee.food_app.Entity.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void registerUser(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean checkEmailExists(String email);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("UPDATE users SET name = :name, phone = :phone, address = :address WHERE id = :userId")
    void updateUserProfile(int userId, String name, String phone, String address);

    @Query("UPDATE users SET name = :name, email = :email, avatar = :avatar, phone = :phone, address = :address WHERE id = :userId")
    void updateUser(int userId, String name, String email, String avatar, String phone, String address);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUser(int userId);
}
