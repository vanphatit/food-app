package com.phatlee.food_app.Database;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.phatlee.food_app.Entity.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartDaoFirestore {

    private FirebaseFirestore firestore;

    public CartDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnCartLoadedListener {
        void onCartLoaded(Cart cart);
        void onFailure(Exception e);
    }

    public interface OnCartListLoadedListener {
        void onCartListLoaded(List<Cart> cartList);
        void onFailure(Exception e);
    }

    // Sửa hàm insert: sử dụng add() để tạo document với id tự động và lưu vào cartId
    public void insert(final Cart cart, final OnOperationCompleteListener listener) {
        firestore.collection("carts")
                .add(cart)
                .addOnSuccessListener(documentReference -> {
                    // Lấy document id tự sinh và gán cho cart
                    String docId = documentReference.getId();
                    cart.setCartId(docId);
                    // Update lại trường "cartId" trong document trên Firestore
                    documentReference.update("cartId", docId)
                            .addOnSuccessListener(aVoid -> listener.onSuccess())
                            .addOnFailureListener(listener::onFailure);
                    listener.onSuccess();
                })
                .addOnFailureListener(listener::onFailure);
    }

    // Update: sử dụng cart.getCartId()
    public void update(final Cart cart, final OnOperationCompleteListener listener) {
        if (cart.getCartId() == null) {
            listener.onFailure(new Exception("Cart cartId is null"));
            return;
        }
        firestore.collection("carts")
                .document(cart.getCartId())
                .set(cart)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Delete: sử dụng cart.getCartId()
    public void delete(final Cart cart, final OnOperationCompleteListener listener) {
        if (cart.getCartId() == null) {
            listener.onFailure(new Exception("Cart cartId is null"));
            return;
        }
        firestore.collection("carts")
                .document(cart.getCartId())
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void removeFromCart(final String userId, final int foodId, final OnOperationCompleteListener listener) {
        firestore.collection("carts")
                .whereEqualTo("userId", userId)
                .whereEqualTo("foodId", foodId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        firestore.collection("carts").document(doc.getId()).delete();
                    }
                    listener.onSuccess();
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getCartItem(final String userId, final int foodId, final OnCartLoadedListener listener) {
        firestore.collection("carts")
                .whereEqualTo("userId", userId)
                .whereEqualTo("foodId", foodId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    if (!documents.isEmpty()) {
                        Cart cart = documents.get(0).toObject(Cart.class);
                        cart.setCartId(documents.get(0).getId());
                        listener.onCartLoaded(cart);
                    } else {
                        listener.onCartLoaded(null);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getCartByUser(final String userId, final OnCartListLoadedListener listener) {
        firestore.collection("carts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Cart> list = new ArrayList<>();
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        Cart cart = doc.toObject(Cart.class);
                        cart.setCartId(doc.getId());
                        list.add(cart);
                    }
                    listener.onCartListLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public List<Cart> getCartByUserSync(String userId) throws Exception {
        // Sử dụng Tasks.await() để chờ kết quả của Firestore query
        QuerySnapshot snapshot = Tasks.await(firestore.collection("carts")
                .whereEqualTo("userId", userId)
                .get());
        List<Cart> list = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Cart cart = doc.toObject(Cart.class);
            cart.setCartId(doc.getId());
            list.add(cart);
        }
        return list;
    }


    public void clearCartByUser(final String userId, final OnOperationCompleteListener listener) {
        firestore.collection("carts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        firestore.collection("carts").document(doc.getId()).delete();
                    }
                    listener.onSuccess();
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void clearCartByUserSync(String userId) throws Exception {
        QuerySnapshot snapshot = Tasks.await(firestore.collection("carts")
                .whereEqualTo("userId", userId)
                .get());
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Tasks.await(firestore.collection("carts").document(doc.getId()).delete());
        }
    }

    public void increaseQuantity(final String userId, final int foodId, final OnOperationCompleteListener listener) {
        getCartItem(userId, foodId, new OnCartLoadedListener() {
            @Override
            public void onCartLoaded(Cart cart) {
                if (cart != null) {
                    cart.quantity += 1;
                    update(cart, listener);
                } else {
                    listener.onFailure(new Exception("Cart item not found"));
                }
            }
            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void decreaseQuantity(final String userId, final int foodId, final OnOperationCompleteListener listener) {
        getCartItem(userId, foodId, new OnCartLoadedListener() {
            @Override
            public void onCartLoaded(Cart cart) {
                if (cart != null && cart.quantity > 1) {
                    cart.quantity -= 1;
                    update(cart, listener);
                } else {
                    listener.onFailure(new Exception("Cart item not found or quantity too low"));
                }
            }
            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }
}