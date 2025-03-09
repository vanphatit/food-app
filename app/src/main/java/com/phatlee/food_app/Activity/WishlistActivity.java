package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.phatlee.food_app.Adapter.WishlistAdapter;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Wishlist;
import com.phatlee.food_app.databinding.ActivityWishlistBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class WishlistActivity extends AppCompatActivity {
    private ActivityWishlistBinding binding;
    private AppDatabase db;
    private int userId;
    private List<Foods> wishlistFoods;
    private WishlistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
            List<Wishlist> wishlist = db.wishlistDao().getUserWishlist(userId);
            wishlistFoods = new ArrayList<>();

            for (Wishlist item : wishlist) {
                Foods food = db.foodsDao().getFoodById(item.getFoodId());
                if (food != null) {
                    wishlistFoods.add(food);
                }
            }

            runOnUiThread(() -> {
                if (wishlistFoods.isEmpty()) {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyTxt.setVisibility(View.GONE);
                    adapter = new WishlistAdapter(wishlistFoods, this);
                    binding.cardView.setAdapter(adapter);
                }
            });
        });
    }
}