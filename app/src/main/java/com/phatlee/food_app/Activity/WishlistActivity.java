package com.phatlee.food_app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Adapter.WishlistAdapter;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Database.WishlistDaoFirestore;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.Entity.Wishlist;
import com.phatlee.food_app.Repository.UserRepository;
import com.phatlee.food_app.databinding.ActivityWishlistBinding;
import com.phatlee.food_app.Repository.FoodsRepository;
import com.phatlee.food_app.Repository.WishlistRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class WishlistActivity extends AppCompatActivity {
    private ActivityWishlistBinding binding;
    private String userId;
    private List<Foods> wishlistFoods;
    private WishlistAdapter adapter;
    private WishlistRepository wishlistRepository;
    private FoodsRepository foodsRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();

        userId = getIntent().getStringExtra("userid");

        wishlistRepository = new WishlistRepository();
        foodsRepository = new FoodsRepository();

        setupRecyclerView();
        loadWishlist();

        binding.backBtn.setOnClickListener(v -> finish());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadWishlist();
    }

    private void setupRecyclerView() {
        binding.cardView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadWishlist() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Sử dụng hàm đồng bộ getUserWishlistSync() từ WishlistRepository để lấy danh sách wishlist của user
                List<Wishlist> wishlists = wishlistRepository.getUserWishlist(userId);
                wishlistFoods = new ArrayList<>();
                if (wishlists != null && !wishlists.isEmpty()) {
                    // Duyệt qua từng wishlist item để lấy thông tin Food bằng hàm đồng bộ getFoodByIdSync()
                    for (Wishlist item : wishlists) {
                        Foods food = foodsRepository.getFoodByIdSync(item.getFoodId());
                        if (food != null) {
                            wishlistFoods.add(food);
                        }
                    }
                }
                // Cập nhật giao diện trên UI thread
                runOnUiThread(() -> {
                    if (wishlistFoods.isEmpty()) {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                    } else {
                        binding.emptyTxt.setVisibility(View.GONE);
                        adapter = new WishlistAdapter(wishlistFoods, WishlistActivity.this);
                        binding.cardView.setAdapter(adapter);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(WishlistActivity.this, "Error loading wishlist", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}