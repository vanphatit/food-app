package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.phatlee.food_app.Entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void registerUser(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean checkEmailExists(String email);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("UPDATE users SET name = :name, phone = :phone, address = :address WHERE id = :userId")
    void updateUserProfile(int userId, String name, String phone, String address);

    @Query("UPDATE users SET avatar = :avatar WHERE id = :userId")
    void updateUserAvatar(int userId, String avatar);

    @Query("SELECT avatar FROM users WHERE id = :userId")
    String getUserAvatar(int userId);
}
