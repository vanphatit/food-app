package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Adapter.BestFoodsAdapter;
import com.phatlee.food_app.Adapter.CategoryAdapter;
import com.phatlee.food_app.Database.FoodsDaoFirestore;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Entity.Category;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.CategoryRepository;
import com.phatlee.food_app.Repository.FoodsRepository;
import com.phatlee.food_app.Repository.UserRepository;
import com.phatlee.food_app.databinding.ActivityMainBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private FoodsRepository foodsRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private FirebaseAuth mAuth;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Gọi phương thức đồng bộ để lấy user theo email
                User user = userRepository.getUserByEmail(mAuth.getCurrentUser().getEmail());
                if (user != null) {
                    currentUser = user;
                } else {
                    currentUser = null;
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                currentUser = null;
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error loading user", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });

        // Khởi tạo các repository Firestore
        foodsRepository = new FoodsRepository();
        categoryRepository = new CategoryRepository();
        userRepository = new UserRepository();

        initBestFood();
        initCategory();
        setVariable();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // Reload best foods từ Firestore
        foodsRepository.getAllFoods(new FoodsDaoFirestore.OnFoodsLoadedListener() {
            @Override
            public void onFoodsLoaded(List<Foods> foods) {
                List<Foods> bestFoods = new ArrayList<>();
                for (Foods food : foods) {
                    if (food.isBestFood()) {
                        bestFoods.add(food);
                    }
                }
                runOnUiThread(() -> {
                    binding.bestFoodView.setLayoutManager(
                            new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    BestFoodsAdapter adapter = new BestFoodsAdapter(bestFoods);
                    binding.bestFoodView.setAdapter(adapter);
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error loading best foods", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setVariable() {

        binding.fabChatGemini.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GeminiChatActivity.class));
        });


        binding.logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        binding.searchBtn.setOnClickListener(v -> {
            String text = binding.searchEdt.getText().toString();
            if (!text.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                intent.putExtra("text", text);
                intent.putExtra("isSearch", true);
                startActivity(intent);
            }
        });

        binding.textView12.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
            intent.putExtra("viewAll", true);
            startActivity(intent);
        });

        binding.cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("userid", currentUser.getId());
            startActivity(intent);
        });
        binding.txvProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyProfileActivity.class);
            intent.putExtra("userid", currentUser.getId());
            startActivity(intent);
        });
        binding.orderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            intent.putExtra("userid", currentUser.getId());
            startActivity(intent);
        });
    }

    private void initBestFood() {
        binding.progressBarBestFood.setVisibility(View.VISIBLE);
        // Lấy toàn bộ Foods từ Firestore và lọc ra bestFood
        foodsRepository.getAllFoods(new FoodsDaoFirestore.OnFoodsLoadedListener() {
            @Override
            public void onFoodsLoaded(List<Foods> foods) {
                List<Foods> bestFoods = new ArrayList<>();
                for (Foods food : foods) {
                    if (food.isBestFood()) {
                        bestFoods.add(food);
                    }
                }
                runOnUiThread(() -> {
                    binding.bestFoodView.setLayoutManager(
                            new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    BestFoodsAdapter adapter = new BestFoodsAdapter(bestFoods);
                    binding.bestFoodView.setAdapter(adapter);
                    binding.progressBarBestFood.setVisibility(View.GONE);
                });
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error loading best foods", Toast.LENGTH_SHORT).show();
                    binding.progressBarBestFood.setVisibility(View.GONE);
                });
            }
        });
    }

    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        categoryRepository.getAllCategories(new com.phatlee.food_app.Database.CategoryDaoFirestore.OnCategoriesLoadedListener() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                runOnUiThread(() -> {
                    binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                    CategoryAdapter adapter = new CategoryAdapter(categories);
                    binding.categoryView.setAdapter(adapter);
                    binding.progressBarCategory.setVisibility(View.GONE);
                });
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error loading categories", Toast.LENGTH_SHORT).show();
                    binding.progressBarCategory.setVisibility(View.GONE);
                });
            }
        });
    }
}