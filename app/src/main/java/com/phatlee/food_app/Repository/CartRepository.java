package com.phatlee.food_app.Repository;

import com.phatlee.food_app.Database.CartDaoFirestore;
import com.phatlee.food_app.Entity.Cart;
import java.util.List;

public class CartRepository {
    private CartDaoFirestore dao;

    public CartRepository() {
        dao = new CartDaoFirestore();
    }

    public void insertCart(Cart cart, CartDaoFirestore.OnOperationCompleteListener listener) {
        dao.insert(cart, listener);
    }

    public void updateCart(Cart cart, CartDaoFirestore.OnOperationCompleteListener listener) {
        dao.update(cart, listener);
    }

    public void deleteCart(Cart cart, CartDaoFirestore.OnOperationCompleteListener listener) {
        dao.delete(cart, listener);
    }

    public void removeFromCart(String userId, int foodId, CartDaoFirestore.OnOperationCompleteListener listener) {
        dao.removeFromCart(userId, foodId, listener);
    }

    public void getCartItem(String userId, int foodId, CartDaoFirestore.OnCartLoadedListener listener) {
        dao.getCartItem(userId, foodId, listener);
    }

    public void getCartByUser(String userId, CartDaoFirestore.OnCartListLoadedListener listener) {
        dao.getCartByUser(userId, listener);
    }

    public List<Cart> getCartByUserSync(String userId) throws Exception {
        return dao.getCartByUserSync(userId);
    }


    public void clearCartByUser(String userId, CartDaoFirestore.OnOperationCompleteListener listener) {
        dao.clearCartByUser(userId, listener);
    }

    public void clearCartByUserSync(String userId) throws Exception {
        dao.clearCartByUserSync(userId);
    }

    public void increaseQuantity(String userId, int foodId, CartDaoFirestore.OnOperationCompleteListener listener) {
        dao.increaseQuantity(userId, foodId, listener);
    }

    public void decreaseQuantity(String userId, int foodId, CartDaoFirestore.OnOperationCompleteListener listener) {
        dao.decreaseQuantity(userId, foodId, listener);
    }
}