package com.phatlee.food_app.Repository;

import com.phatlee.food_app.Database.WishlistDaoFirestore;
import com.phatlee.food_app.Entity.Wishlist;
import java.util.List;

public class WishlistRepository {
    private WishlistDaoFirestore dao;

    public WishlistRepository() {
        dao = new WishlistDaoFirestore();
    }

    public void addToWishlist(Wishlist wishlist) throws Exception {
        dao.addToWishlist(wishlist);
    }

    public void removeFromWishlist(String userId, int foodId) throws Exception {
        dao.removeFromWishlist(userId, foodId);
    }

    public List<Wishlist> getUserWishlist(String userId) throws Exception {
        return dao.getUserWishlist(userId);
    }

    public Wishlist getWishlistItem(String userId, int foodId) throws Exception {
        return dao.getWishlistItem(userId, foodId);
    }
}