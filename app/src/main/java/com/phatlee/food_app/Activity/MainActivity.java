package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phatlee.food_app.Adapter.BestFoodsAdapter;
import com.phatlee.food_app.Adapter.CartAdapter;
import com.phatlee.food_app.Adapter.CategoryAdapter;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Category;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Location;
import com.phatlee.food_app.Entity.Price;
import com.phatlee.food_app.Entity.Time;
import com.phatlee.food_app.R;
import com.phatlee.food_app.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "=============== 👌✌️onCreate called");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

//        initLocation();
//        initTime();
//        initPrice();
        initBestFood();
        initCategory();
        setVariable();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int user_id = sharedPreferences.getInt("user_id", -1);
            String user_name = db.userDao().getUserById(user_id).getName();

            runOnUiThread(() -> {
                binding.txvProfile.setText(user_name);
            });
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
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int user_id = sharedPreferences.getInt("user_id", -1);
            String user_name = db.userDao().getUserById(user_id).getName();

            runOnUiThread(() -> {
                binding.txvProfile.setText(user_name);
            });
        });

        binding.logoutBtn.setOnClickListener(v -> {

            sharedPreferences.edit().remove("user_id").apply();
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

        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));

        binding.txvProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MyProfileActivity.class)));

        binding.orderBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrderActivity.class)));
    }

    private void initBestFood() {
        binding.progressBarBestFood.setVisibility(View.VISIBLE);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Foods> bestFoodList = db.foodsDao().getBestFoods();  // Lấy từ Room

            runOnUiThread(() -> {
                if (!bestFoodList.isEmpty()) {
                    binding.bestFoodView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    BestFoodsAdapter adapter = new BestFoodsAdapter(bestFoodList);
                    binding.bestFoodView.setAdapter(adapter);
                }
                binding.progressBarBestFood.setVisibility(View.GONE);
            });
        }).start();
    }

    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Category> list = db.categoryDao().getAllCategories();

            runOnUiThread(() -> {
                if (!list.isEmpty()) {
                    binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                    CategoryAdapter adapter = new CategoryAdapter(list);
                    binding.categoryView.setAdapter(adapter);
                    adapter.loadCategories(this);  // Cập nhật dữ liệu từ Room
                }
                binding.progressBarCategory.setVisibility(View.GONE);
            });
        }).start();
    }


//    private void initLocation() {
//        new Thread(() -> {
//            AppDatabase db = AppDatabase.getInstance(this);
//            List<Location> list = db.locationDao().getAllLocations();
//
//            runOnUiThread(() -> {
//                if (!list.isEmpty()) {
//                    ArrayAdapter<Location> adapter
//                            = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
//                    for(Location location : list) {
//                        Log.d("MainActivity", "=============== 👌✌️initLocation: " + location);
//                    }
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    binding.locationSp.setAdapter(adapter);
//                }
//            });
//        }).start();
//    }
//
//    private void initTime() {
//        new Thread(() -> {
//            AppDatabase db = AppDatabase.getInstance(this);
//            List<Time> list = db.timeDao().getAllTimes();
//
//            runOnUiThread(() -> {
//                if (!list.isEmpty()) {
//                    ArrayAdapter<Time> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    binding.timeSp.setAdapter(adapter);
//                }
//            });
//        }).start();
//    }
//
//    private void initPrice() {
//        new Thread(() -> {
//            AppDatabase db = AppDatabase.getInstance(this);
//            List<Price> list = db.priceDao().getAllPrices();
//
//            runOnUiThread(() -> {
//                if (!list.isEmpty()) {
//                    ArrayAdapter<Price> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    binding.priceSp.setAdapter(adapter);
//                }
//            });
//        }).start();
//    }
}