package com.phatlee.food_app.Database;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phatlee.food_app.Entity.Foods;

import java.util.ArrayList;
import java.util.List;

public class FoodsDaoFirestore {

    private FirebaseFirestore firestore;

    public FoodsDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnFoodsLoadedListener {
        void onFoodsLoaded(List<Foods> foods);
        void onFailure(Exception e);
    }

    public interface OnFoodLoadedListener {
        void onFoodLoaded(Foods food);
        void onFailure(Exception e);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void insert(Foods food, final OnOperationCompleteListener listener) {
        firestore.collection("foods")
                .document(String.valueOf(food.getId()))
                .set(food)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllFoods(final OnFoodsLoadedListener listener) {
        firestore.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Foods> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> list.add(doc.toObject(Foods.class)));
                    listener.onFoodsLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getFoodById(int id, final OnFoodLoadedListener listener) {
        firestore.collection("foods")
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Foods food = documentSnapshot.toObject(Foods.class);
                        listener.onFoodLoaded(food);
                    } else {
                        listener.onFoodLoaded(null);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public Foods getFoodByIdSync(int foodId) throws Exception {
        DocumentSnapshot doc = Tasks.await(firestore.collection("foods").document(String.valueOf(foodId)).get());
        if (doc.exists()) {
            Foods food = doc.toObject(Foods.class);
            return food;
        } else {
            return null;
        }
    }

    public void update(Foods food, final OnOperationCompleteListener listener) {
        firestore.collection("foods")
                .document(String.valueOf(food.getId()))
                .set(food)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void updateRating(int foodId, float newRating, final OnOperationCompleteListener listener) {
        firestore.collection("foods")
                .document(String.valueOf(foodId))
                .update("star", newRating)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void updateStockQuantity(int foodId, int newStockQuantity, final OnOperationCompleteListener listener) {
        firestore.collection("foods")
                .document(String.valueOf(foodId))
                .update("stockQuantity", newStockQuantity)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void updateStockQuantitySync(int foodId, int newStock) throws Exception {
        Tasks.await(firestore.collection("foods").document(String.valueOf(foodId)).update("stockQuantity", newStock));
    }

    public void updateRatingSync(int foodId, float newRating) throws Exception {
        Tasks.await(firestore.collection("foods").document(String.valueOf(foodId)).update("star", newRating));
    }
}