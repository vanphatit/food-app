package com.phatlee.food_app.Repository;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phatlee.food_app.Entity.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddressRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getAddresses(String userId, OnAddressesLoaded listener) {
        db.collection("addresses")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Address> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Address a = doc.toObject(Address.class);
                        list.add(a);
                    }
                    listener.onLoaded(list);
                });
    }

    public void saveAddress(Address a, OnSaveCallback cb) {
        String id = a.getId() == null ? UUID.randomUUID().toString() : a.getId();
        a.setId(id);
        db.collection("addresses").document(id).set(a)
                .addOnSuccessListener(unused -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onFail());
    }

    public void deleteAddress(String id, OnDeleteCallback cb) {
        db.collection("addresses").document(id)
                .delete()
                .addOnSuccessListener(unused -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onFail());
    }

    public interface OnAddressesLoaded {
        void onLoaded(List<Address> list);
    }

    public interface OnSaveCallback {
        void onSuccess();
        void onFail();
    }

    public interface OnDeleteCallback {
        void onSuccess();
        void onFail();
    }
}
