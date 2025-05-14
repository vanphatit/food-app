package com.phatlee.food_app.Database;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.phatlee.food_app.Entity.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class WishlistDaoFirestore {

    private FirebaseFirestore firestore;

    public WishlistDaoFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Phương thức đồng bộ thêm một Wishlist
    // Firestore sẽ tự tạo document id, sau đó cập nhật lại field "id" của wishlist với giá trị đó.
    public void addToWishlist(Wishlist wishlist) throws Exception {
        // Thêm wishlist vào collection "wishlists" để Firestore tự sinh document id
        DocumentReference docRef = Tasks.await(firestore.collection("wishlists").add(wishlist));
        String docId = docRef.getId();
        wishlist.setId(docId);
        // Cập nhật lại trường "id" trong document với docId vừa sinh
        Tasks.await(docRef.update("id", docId));
    }

    // Phương thức đồng bộ xóa tất cả wishlist có userId và foodId cho trước
    public void removeFromWishlist(String userId, int foodId) throws Exception {
        QuerySnapshot snapshot = Tasks.await(
                firestore.collection("wishlists")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("foodId", foodId)
                        .get()
        );
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Tasks.await(firestore.collection("wishlists").document(doc.getId()).delete());
        }
    }

    // Phương thức đồng bộ lấy danh sách wishlist của một user
    public List<Wishlist> getUserWishlist(String userId) throws Exception {
        QuerySnapshot snapshot = Tasks.await(
                firestore.collection("wishlists")
                        .whereEqualTo("userId", userId)
                        .get()
        );
        List<Wishlist> list = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Wishlist wishlist = doc.toObject(Wishlist.class);
            list.add(wishlist);
        }
        return list;
    }

    // Phương thức đồng bộ lấy một wishlist item theo userId và foodId (trả về null nếu không tìm thấy)
    public Wishlist getWishlistItem(String userId, int foodId) throws Exception {
        QuerySnapshot snapshot = Tasks.await(
                firestore.collection("wishlists")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("foodId", foodId)
                        .limit(1)
                        .get()
        );
        List<DocumentSnapshot> documents = snapshot.getDocuments();
        if (!documents.isEmpty()) {
            return documents.get(0).toObject(Wishlist.class);
        } else {
            return null;
        }
    }
}