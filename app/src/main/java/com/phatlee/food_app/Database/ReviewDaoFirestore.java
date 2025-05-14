package com.phatlee.food_app.Database;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phatlee.food_app.Entity.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewDaoFirestore {

    private FirebaseFirestore firestore;

    public ReviewDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnReviewsLoadedListener {
        void onReviewsLoaded(List<Review> reviews);
        void onFailure(Exception e);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void getReviewsByFoodId(int foodId, final OnReviewsLoadedListener listener) {
        firestore.collection("reviews")
                .whereEqualTo("foodId", foodId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        list.add(doc.toObject(Review.class));
                    }
                    listener.onReviewsLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void insert(Review review, final OnOperationCompleteListener listener) {
        firestore.collection("reviews")
                .add(review)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void getAverageRating(int foodId, final OnAverageRatingListener listener) {
        firestore.collection("reviews")
                .whereEqualTo("foodId", foodId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double total = 0;
                    int count = queryDocumentSnapshots.size();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Review r = doc.toObject(Review.class);
                        total += r.rating;
                    }
                    float average = count > 0 ? (float) (total / count) : 0;
                    listener.onAverageRating(average);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnAverageRatingListener {
        void onAverageRating(float average);
        void onFailure(Exception e);
    }
}