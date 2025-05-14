package com.phatlee.food_app.Repository;

import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Entity.User;
import java.util.List;

public class UserRepository {
    private UserDaoFirestore dao;

    public UserRepository() {
        dao = new UserDaoFirestore();
    }

    public void registerUser(User user, UserDaoFirestore.OnOperationCompleteListener listener) {
        dao.registerUser(user, listener);
    }

    public void login(String email, String password, UserDaoFirestore.OnUserLoadedListener listener) {
        dao.login(email, password, listener);
    }

    public void checkEmailExists(String email, UserDaoFirestore.OnEmailCheckListener listener) {
        dao.checkEmailExists(email, listener);
    }

    public void getAllUsers(UserDaoFirestore.OnUsersLoadedListener listener) {
        dao.getAllUsers(listener);
    }

    public void getUserById(String userId, UserDaoFirestore.OnUserLoadedListener listener) {
        dao.getUserById(userId, listener);
    }

    // get user by email
    public User getUserByEmail(String email) throws Exception {
        return dao.getUserByEmail(email);
    }

    public void updateUserProfile(String userId, String name, String phone, String address,
                                  UserDaoFirestore.OnOperationCompleteListener listener) {
        dao.updateUserProfile(userId, name, phone, address, listener);
    }

    public void updateUserAvatar(String userId, String avatar, UserDaoFirestore.OnOperationCompleteListener listener) {
        dao.updateUserAvatar(userId, avatar, listener);
    }
}