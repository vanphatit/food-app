package com.phatlee.food_app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.phatlee.food_app.Adapter.ReviewAdapter;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Cart;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Review;
import com.phatlee.food_app.Entity.Wishlist;
import com.phatlee.food_app.R;
import com.phatlee.food_app.databinding.ActivityDetailBinding;

import java.util.List;
import java.util.concurrent.Executors;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private AppDatabase db;
    private int currentUserId;
    private Foods object;
    private int num = 1;
    SharedPreferences sharedPreferences;
    private boolean isInWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        db = AppDatabase.getInstance(this);

        getIntentExtra();
        setVariable();
        checkWishlistStatus();
        setupReviewSection();
        loadReviews();
    }

    private void setVariable() {
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        binding.backBtn.setOnClickListener(v -> {
            Intent backMess = new Intent();
            backMess.putExtra("message", "Back from Detail Activity");
            setResult(RESULT_OK, backMess);
            finish();
        });

        int imageResId = getResources().getIdentifier("food_" + object.getImagePath(), "drawable", getPackageName());
        binding.pic.setImageResource(imageResId != 0 ? imageResId : R.drawable.logo);

        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num * object.getPrice()) + "$");

        binding.plusBtn.setOnClickListener(v -> {
            num++;
            binding.numTxt.setText(num + " ");
            binding.totalTxt.setText("$" + (num * object.getPrice()));
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num--;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                Cart existingCart = db.cartDao().getCartItem(currentUserId, object.getId());

                if (existingCart != null) {
                    // Nếu đã có trong giỏ hàng -> tăng số lượng
                    existingCart.quantity += num;
                    db.cartDao().update(existingCart);
                } else {
                    // Nếu chưa có -> thêm mới vào giỏ hàng
                    db.cartDao().insert(new Cart(currentUserId, object.getId(), num));
                }

                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show());
            });
        });

        binding.favBtn.setOnClickListener(v -> toggleWishlist());
    }

    private void setupReviewSection() {
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("user_id", -1);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            boolean hasBought = db.reviewDao().hasUserBoughtFood(currentUserId, object.getId()) > 0;

            runOnUiThread(() -> {
                if (hasBought) {
                    binding.reviewTitle.setVisibility(View.VISIBLE);
                    binding.userRatingBar.setVisibility(View.VISIBLE);
                    binding.commentInput.setVisibility(View.VISIBLE);
                    binding.submitReviewBtn.setVisibility(View.VISIBLE);

                    binding.submitReviewBtn.setOnClickListener(v -> submitReview(currentUserId));
                } else {
                    binding.reviewTitle.setVisibility(View.GONE);
                    binding.userRatingBar.setVisibility(View.GONE);
                    binding.commentInput.setVisibility(View.GONE);
                    binding.submitReviewBtn.setVisibility(View.GONE);
                }
            });
        });
    }

    private void submitReview(int userId) {
        float rating = binding.userRatingBar.getRating();
        String comment = binding.commentInput.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating!", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            db.reviewDao().insert(new Review(userId, object.getId(), rating, comment));

            // Cập nhật lại rating của món ăn
            float newAvgRating = db.reviewDao().getAverageRating(object.getId());
            db.foodsDao().updateRating(object.getId(), newAvgRating);

            runOnUiThread(() -> {
                Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
                binding.commentInput.setText("");
                binding.userRatingBar.setRating(0);
                binding.rateTxt.setText(newAvgRating + " Rating");
                binding.ratingBar.setRating(newAvgRating);
                loadReviews(); // Load lại danh sách review sau khi cập nhật
            });
        });
    }

    private void loadReviews() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Review> reviews = db.reviewDao().getReviewsByFoodId(object.getId());

            runOnUiThread(() -> {
                if (reviews.isEmpty()) {
                    binding.reviewsTitle.setVisibility(View.GONE);
                    binding.reviewRecyclerView.setVisibility(View.GONE);
                } else {
                    binding.reviewsTitle.setVisibility(View.VISIBLE);
                    binding.reviewRecyclerView.setVisibility(View.VISIBLE);
                    binding.reviewRecyclerView.setAdapter(new ReviewAdapter(reviews, this));
                }
            });
        });
    }

    private void checkWishlistStatus() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Wishlist wishlistItem = db.wishlistDao().getWishlistItem(currentUserId, object.getId());
            isInWishlist = wishlistItem != null;

            runOnUiThread(() -> updateFavIcon());
        });
    }

    private void updateFavIcon() {
        binding.favBtn.setImageResource(isInWishlist ? R.drawable.favorite_red : R.drawable.favorite_white);
    }

    private void toggleWishlist() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (isInWishlist) {
                db.wishlistDao().removeFromWishlist(currentUserId, object.getId());
                isInWishlist = false;
            } else {
                db.wishlistDao().addToWishlist(new Wishlist(currentUserId, object.getId()));
                isInWishlist = true;
            }

            runOnUiThread(() -> {
                updateFavIcon();
                Toast.makeText(this, isInWishlist ? "Added to Wishlist" : "Removed from Wishlist", Toast.LENGTH_SHORT).show();
            });
        });
    }


    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}