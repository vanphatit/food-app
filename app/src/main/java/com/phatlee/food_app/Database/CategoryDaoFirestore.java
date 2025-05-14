package com.phatlee.food_app.Database;

import com.google.firebase.firestore.FirebaseFirestore;
import com.phatlee.food_app.Entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDaoFirestore {

    private FirebaseFirestore firestore;

    public CategoryDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnCategoriesLoadedListener {
        void onCategoriesLoaded(List<Category> categories);
        void onFailure(Exception e);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void insert(Category category, final OnOperationCompleteListener listener) {
        firestore.collection("categories")
                .document(String.valueOf(category.getId()))
                .set(category)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllCategories(final OnCategoriesLoadedListener listener) {
        firestore.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Category> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> list.add(doc.toObject(Category.class)));
                    listener.onCategoriesLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }
}