package com.phatlee.food_app.Repository;

import com.phatlee.food_app.Database.ReviewDaoFirestore;
import com.phatlee.food_app.Entity.Review;
import java.util.List;

public class ReviewRepository {
    private ReviewDaoFirestore dao;

    public ReviewRepository() {
        dao = new ReviewDaoFirestore();
    }

    public void getReviewsByFoodId(int foodId, ReviewDaoFirestore.OnReviewsLoadedListener listener) {
        dao.getReviewsByFoodId(foodId, listener);
    }

    public void insertReview(Review review, ReviewDaoFirestore.OnOperationCompleteListener listener) {
        dao.insert(review, listener);
    }

    public void getAverageRating(int foodId, ReviewDaoFirestore.OnAverageRatingListener listener) {
        dao.getAverageRating(foodId, listener);
    }
}