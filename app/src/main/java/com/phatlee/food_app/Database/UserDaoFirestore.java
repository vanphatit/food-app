package com.phatlee.food_app.Database;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.phatlee.food_app.Entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserDaoFirestore {

    private FirebaseFirestore firestore;

    public UserDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnUsersLoadedListener {
        void onUsersLoaded(List<User> users);
        void onFailure(Exception e);
    }

    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
        void onFailure(Exception e);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Sửa registerUser: sử dụng add() để Firestore tự tạo document id, sau đó gán vào field id của User
    public void registerUser(User user, final OnOperationCompleteListener listener) {
        firestore.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    // Lấy document id tự sinh và gán cho user
                    String docId = documentReference.getId();
                    user.setId(docId);
                    // Update lại trường "id" trong document trên Firestore
                    documentReference.update("id", docId)
                            .addOnSuccessListener(aVoid -> listener.onSuccess())
                            .addOnFailureListener(listener::onFailure);
                    listener.onSuccess();
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void login(String email, String password, final OnUserLoadedListener listener) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                        listener.onUserLoaded(user);
                    } else {
                        listener.onUserLoaded(null);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void checkEmailExists(String email, final OnEmailCheckListener listener) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listener.onResult(!queryDocumentSnapshots.isEmpty());
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnEmailCheckListener {
        void onResult(boolean exists);
        void onFailure(Exception e);
    }

    public void getAllUsers(final OnUsersLoadedListener listener) {
        firestore.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> list.add(doc.toObject(User.class)));
                    listener.onUsersLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }

    // getUserById: tham số truyền vào là id kiểu String
    public void getUserById(String userId, final OnUserLoadedListener listener) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        listener.onUserLoaded(user);
                    } else {
                        listener.onUserLoaded(null);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    // get user by email
    public User getUserByEmail(String email) throws Exception {
        Log.d("UserDaoFirestore", "Searching for user with email: " + email);
        QuerySnapshot snapshot = Tasks.await(
                firestore.collection("users")
                        .whereEqualTo("email", email)
                        .limit(1)
                        .get()
        );
        Log.d("UserDaoFirestore", "Documents found: " + snapshot.size());
        if (!snapshot.isEmpty()) {
            return snapshot.getDocuments().get(0).toObject(User.class);
        } else {
            return null;
        }
    }



    public void updateUserProfile(String userId, String name, String phone, String address, final OnOperationCompleteListener listener) {
        firestore.collection("users")
                .document(userId)
                .update("name", name, "phone", phone, "address", address)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void updateUserAvatar(String userId, String avatar, final OnOperationCompleteListener listener) {
        // Kiểm tra userId không null
        if (userId == null) {
            listener.onFailure(new IllegalArgumentException("UserId must not be null"));
            return;
        }
        firestore.collection("users")
                .document(userId)
                .update("avatar", avatar)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }
}