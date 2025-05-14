package com.phatlee.food_app.Database;

import com.google.firebase.firestore.FirebaseFirestore;
import com.phatlee.food_app.Entity.Price;

import java.util.ArrayList;
import java.util.List;

public class PriceDaoFirestore {

    private FirebaseFirestore firestore;

    public PriceDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnPricesLoadedListener {
        void onPricesLoaded(List<Price> prices);
        void onFailure(Exception e);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void insert(Price price, final OnOperationCompleteListener listener) {
        firestore.collection("prices")
                .document(String.valueOf(price.getId()))
                .set(price)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllPrices(final OnPricesLoadedListener listener) {
        firestore.collection("prices")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Price> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> list.add(doc.toObject(Price.class)));
                    listener.onPricesLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }
}