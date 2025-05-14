package com.phatlee.food_app.Database;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDaoFirestore {

    private FirebaseFirestore firestore;

    public OrderDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface OnInsertOrderListener {
        void onOrderInserted(String documentId);
        void onFailure(Exception e);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnOrdersLoadedListener {
        void onOrdersLoaded(List<Order> orders);
        void onFailure(Exception e);
    }

    public interface OnOrderItemsLoadedListener {
        void onOrderItemsLoaded(List<OrderItem> orderItems);
        void onFailure(Exception e);
    }

    public interface OnOrderLoadedListener {
        void onOrderLoaded(Order order);
        void onFailure(Exception e);
    }

    // Lấy đơn hàng theo orderId (orderId là String)
    public void getOrderById(String orderId, final OnOrderLoadedListener listener) {
        firestore.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Order order = documentSnapshot.toObject(Order.class);
                        listener.onOrderLoaded(order);
                    } else {
                        listener.onOrderLoaded(null);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    // Insert đơn hàng; sau khi insert, document id được tạo tự động sẽ được trả về
    public void insertOrder(Order order, final OnInsertOrderListener listener) {
        firestore.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    // Lấy document id tự sinh và gán cho order
                    String docId = documentReference.getId();
                    order.setOrderId(docId);
                    // Update lại trường "orderId" trong document trên Firestore
                    documentReference.update("orderId", docId)
                            .addOnSuccessListener(aVoid -> listener.onOrderInserted(docId))
                            .addOnFailureListener(listener::onFailure);
                    listener.onOrderInserted(docId);
                })
                .addOnFailureListener(listener::onFailure);
    }

    // Insert các OrderItem
    public void insertOrderItems(List<OrderItem> orderItems, final OnOperationCompleteListener listener) {
        final int total = orderItems.size();
        final int[] count = {0};
        for (OrderItem item : orderItems) {
            firestore.collection("orderItems")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        count[0]++;
                        if (count[0] == total) {
                            listener.onSuccess();
                        }
                    })
                    .addOnFailureListener(listener::onFailure);
        }
    }

    public String insertOrderSync(Order order) throws Exception {
        DocumentReference docRef = Tasks.await(firestore.collection("orders").add(order));
        docRef.update("orderId", docRef.getId());
        return docRef.getId();
    }

    public void insertOrderItemsSync(List<OrderItem> orderItems) throws Exception {
        for (OrderItem item : orderItems) {
            Tasks.await(firestore.collection("orderItems").add(item));
        }
    }

    // Lấy đơn hàng của user (userId cũng chuyển thành string nếu cần)
    public void getOrdersByUser(String userId, final OnOrdersLoadedListener listener) {
        firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> list.add(doc.toObject(Order.class)));
                    listener.onOrdersLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }

    // Lấy các OrderItem theo orderId
    public void getOrderItems(String orderId, final OnOrderItemsLoadedListener listener) {
        firestore.collection("orderItems")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrderItem> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> list.add(doc.toObject(OrderItem.class)));
                    listener.onOrderItemsLoaded(list);
                })
                .addOnFailureListener(listener::onFailure);
    }

    // Cập nhật trạng thái đơn hàng theo orderId
    public void updateOrderStatus(String orderId, String status, final OnOperationCompleteListener listener) {
        firestore.collection("orders")
                .document(orderId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }
}