package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.phatlee.food_app.Adapter.ReviewAdapter;
import com.phatlee.food_app.Database.CartDaoFirestore;
import com.phatlee.food_app.Database.ReviewDaoFirestore;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Database.WishlistDaoFirestore;
import com.phatlee.food_app.Entity.Cart;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Review;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.Entity.Wishlist;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.CartRepository;
import com.phatlee.food_app.Repository.FoodsRepository;
import com.phatlee.food_app.Repository.ReviewRepository;
import com.phatlee.food_app.Repository.UserRepository;
import com.phatlee.food_app.Repository.WishlistRepository;
import com.phatlee.food_app.databinding.ActivityDetailBinding;
import com.phatlee.food_app.Database.FoodsDaoFirestore;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private String currentUserId;
    private Foods object;
    private int num = 1;
    private boolean isInWishlist;

    // Khởi tạo các repository Firestore
    private FoodsRepository foodsRepository;
    private CartRepository cartRepository;
    private ReviewRepository reviewRepository;
    private WishlistRepository wishlistRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        // Khởi tạo các repository
        foodsRepository = new FoodsRepository();
        cartRepository = new CartRepository();
        reviewRepository = new ReviewRepository();
        wishlistRepository = new WishlistRepository();

        mAuth = FirebaseAuth.getInstance();
        // Khởi tạo userRepository trước khi sử dụng
        userRepository = new UserRepository();

        // Lấy thông tin user dựa trên email hiện tại
        Executors.newSingleThreadExecutor().execute(() -> {
        try {
            // Gọi phương thức đồng bộ để lấy user theo email
            String email = mAuth.getCurrentUser().getEmail();
            User user = userRepository.getUserByEmail(email);
            if (user != null) {
                currentUserId = user.getId();
                checkWishlistStatus();
                setupReviewSection();
                loadReviews();
            } else {
                currentUserId = null;
                runOnUiThread(() -> {
                    Toast.makeText(DetailActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        } catch (Exception e) {
            currentUserId = null;
            Log.e("DetailActivity", "Error getting user: " + e.getMessage());
            runOnUiThread(() -> {
                Toast.makeText(DetailActivity.this, "Error loading user", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
        });

        Log.d("DetailActivity", "==============✌️👉 Current user ID: " + currentUserId);

        getIntentExtra();
        setVariable();
        saveViewedFood(object);
    }

    private void setVariable() {
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
        binding.stockQuantityTxt.setText(String.valueOf(object.getStockQuantity()));
        DecimalFormat df = new DecimalFormat("#.#");
        binding.rateTxt.setText(df.format(object.getStar()) + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText("$" + (num * object.getPrice()));

        binding.plusBtn.setOnClickListener(v -> {
            int availableStock = object.getStockQuantity();
            if (num < availableStock) {
                num++;
                binding.numTxt.setText(String.valueOf(num));
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            } else {
                Toast.makeText(DetailActivity.this, "Out of stock", Toast.LENGTH_SHORT).show();
            }
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num--;
                binding.numTxt.setText(String.valueOf(num));
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            if(currentUserId == null) {
                Toast.makeText(DetailActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Sử dụng FoodsRepository để lấy thông tin mới nhất của Food từ Firestore
            foodsRepository.getFoodById(object.getId(), new FoodsDaoFirestore.OnFoodLoadedListener() {
                @Override
                public void onFoodLoaded(Foods currentFood) {
                    if (currentFood == null) return;
                    int availableStock = currentFood.getStockQuantity();
                    if (num > availableStock) {
                        runOnUiThread(() ->
                                Toast.makeText(DetailActivity.this, "Not enough stock", Toast.LENGTH_SHORT).show());
                        return;
                    }
                    int newStock = availableStock - num;
                    // Cập nhật stockQuantity xuống Firestore
                    foodsRepository.updateStockQuantity(object.getId(), newStock, new FoodsDaoFirestore.OnOperationCompleteListener() {
                        @Override
                        public void onSuccess() {
                            object.setStockQuantity(newStock);
                            // Xử lý giỏ hàng qua CartRepository
                            cartRepository.getCartItem(currentUserId, object.getId(), new CartDaoFirestore.OnCartLoadedListener() {
                                @Override
                                public void onCartLoaded(Cart cart) {
                                    if (cart != null) {
                                        Log.d("DetailActivity", "==============✌️Cart ID: " + cart.cartId);
                                        cart.quantity += num;
                                        cartRepository.updateCart(cart, new CartDaoFirestore.OnOperationCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                runOnUiThread(() -> {
                                                    Toast.makeText(DetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                                    binding.stockQuantityTxt.setText(String.valueOf(newStock));
                                                });
                                            }
                                            @Override
                                            public void onFailure(Exception e) {
                                                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Failed to update cart", Toast.LENGTH_SHORT).show());
                                            }
                                        });
                                    } else {
                                        Log.d("DetailActivity", "==============✌️Cart is null");
                                        // Nếu chưa có cart item, tạo mới
                                        Cart newCart = new Cart(currentUserId, object.getId(), num);
                                        Log.d("DetailActivity", "==============✌️new Cart: " + newCart);
                                        cartRepository.insertCart(newCart, new CartDaoFirestore.OnOperationCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                runOnUiThread(() -> {
                                                    Toast.makeText(DetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                                    binding.stockQuantityTxt.setText(String.valueOf(newStock));
                                                });
                                            }
                                            @Override
                                            public void onFailure(Exception e) {
                                                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show());
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error accessing cart", Toast.LENGTH_SHORT).show());
                                }
                            });
                        }
                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error updating stock", Toast.LENGTH_SHORT).show());
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error loading food", Toast.LENGTH_SHORT).show());
                }
            });
        });

        binding.favBtn.setOnClickListener(v -> toggleWishlist());
    }

    private void setupReviewSection() {
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean hasBought = checkIfUserHasBoughtFood(currentUserId, object.getId());;
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

    private boolean checkIfUserHasBoughtFood(String userId, int foodId) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            // Truy vấn các đơn hàng của người dùng
            QuerySnapshot ordersSnapshot = Tasks.await(
                    firestore.collection("orders")
                            .whereEqualTo("userId", userId)
                            .get()
            );
            for (DocumentSnapshot orderDoc : ordersSnapshot.getDocuments()) {
                String orderId = orderDoc.getId();
                QuerySnapshot orderItemsSnapshot = Tasks.await(
                        firestore.collection("orderItems")
                                .whereEqualTo("orderId", orderId)
                                .whereEqualTo("foodId", foodId)
                                .get()
                );
                if (!orderItemsSnapshot.isEmpty()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Nếu không có đơn hàng nào chứa món ăn cần tìm thì trả về false
        return false;
    }

    private void submitReview(String userId) {
        float rating = binding.userRatingBar.getRating();
        String comment = binding.commentInput.getText().toString().trim();
        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating!", Toast.LENGTH_SHORT).show();
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            reviewRepository.insertReview(new Review(userId, object.getId(), rating, comment), new ReviewDaoFirestore.OnOperationCompleteListener() {
                @Override
                public void onSuccess() {
                    // Sau đó, cập nhật lại rating trung bình của food (bạn có thể gọi foodsRepository.updateRating)
                    foodsRepository.updateRating(object.getId(), rating, new FoodsDaoFirestore.OnOperationCompleteListener() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                Toast.makeText(DetailActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();
                                binding.commentInput.setText("");
                                binding.userRatingBar.setRating(0);
                                // Sau khi update rating, gọi lại loadReviews để refresh danh sách review
                                loadReviews();
                            });
                        }
                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error updating rating", Toast.LENGTH_SHORT).show());
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error submitting review", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void loadReviews() {
        Executors.newSingleThreadExecutor().execute(() -> {
            reviewRepository.getReviewsByFoodId(object.getId(), new com.phatlee.food_app.Database.ReviewDaoFirestore.OnReviewsLoadedListener() {
                @Override
                public void onReviewsLoaded(List<Review> reviews) {
                    runOnUiThread(() -> {
                        if (reviews == null || reviews.isEmpty()) {
                            binding.reviewsTitle.setVisibility(View.GONE);
                            binding.reviewRecyclerView.setVisibility(View.GONE);
                        } else {
                            binding.reviewsTitle.setVisibility(View.VISIBLE);
                            binding.reviewRecyclerView.setVisibility(View.VISIBLE);
                            binding.reviewRecyclerView.setAdapter(new ReviewAdapter(reviews, DetailActivity.this));
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error loading reviews", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void checkWishlistStatus() {
        // Sử dụng hàm đồng bộ getWishlistItem từ WishlistRepository
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Lấy wishlist theo userId và foodId
                Log.d("DetailActivity", "Checking wishlist for user: " + currentUserId + ", food: " + object.getId());
                Wishlist wishlist = wishlistRepository.getWishlistItem(String.valueOf(currentUserId), object.getId());
                isInWishlist = (wishlist != null);
                if (wishlist != null) {
                    Log.d("DetailActivity", "Wishlist found with ID: " + wishlist.getId());
                } else {
                    Log.e("DetailActivity", "No wishlist item found");
                }
            } catch (Exception e) {
                isInWishlist = false;
                Log.e("DetailActivity", "Error checking wishlist: " + e.getMessage());
            }
            runOnUiThread(() -> updateFavIcon());
        });
    }

    private void updateFavIcon() {
        binding.favBtn.setImageResource(isInWishlist ? R.drawable.favorite_red : R.drawable.favorite_white);
    }

    private void toggleWishlist() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                if (isInWishlist) {
                    // Xóa wishlist (dùng userId dạng String)
                    wishlistRepository.removeFromWishlist(String.valueOf(currentUserId), object.getId());
                    isInWishlist = false;
                    runOnUiThread(() -> {
                        updateFavIcon();
                        Toast.makeText(DetailActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Thêm wishlist mới
                    Wishlist newWishlist = new Wishlist(currentUserId, object.getId());
                    wishlistRepository.addToWishlist(newWishlist);
                    isInWishlist = true;
                    runOnUiThread(() -> {
                        updateFavIcon();
                        Toast.makeText(DetailActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error toggling wishlist: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveViewedFood(Foods food) {
        SharedPreferences prefs = getSharedPreferences("recent_foods", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String key = "food_" + food.getId();

        // Giới hạn tối đa 10 món
        Map<String, ?> all = prefs.getAll();
        if (!all.containsKey(key) && all.size() >= 10) {
            String oldestKey = all.keySet().iterator().next(); // key đầu tiên
            editor.remove(oldestKey);
        }

        // Ghi dưới dạng JSON
        editor.putString(key, new Gson().toJson(food));
        editor.apply();
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}