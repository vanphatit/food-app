package com.phatlee.food_app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phatlee.food_app.Adapter.FoodListAdapter;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.databinding.ActivityListFoodsBinding;

import java.util.ArrayList;
import java.util.List;

public class ListFoodsActivity extends BaseActivity {
    ActivityListFoodsBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;
    private boolean viewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
        setVariable();
    }

    private void setVariable() {

    }

    private void initList() {
        binding.progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Foods> list;

            // Nếu "View All" hoặc tìm kiếm không có keyword, lấy toàn bộ món ăn
            if (viewAll || (isSearch && (searchText == null || searchText.trim().isEmpty()))) {
                list = db.foodsDao().getAllFoods();
            }
            // Nếu có tìm kiếm, lọc theo từ khóa
            else if (isSearch && searchText != null && !searchText.trim().isEmpty()) {
                list = db.foodsDao().getFoodsBySearch("%" + searchText + "%");
            }
            // Nếu không phải tìm kiếm, lấy theo category
            else {
                list = db.foodsDao().getFoodsByCategory(categoryId);
            }

            runOnUiThread(() -> {
                if (!list.isEmpty()) {
                    binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodsActivity.this, 2));
                    adapterListFood = new FoodListAdapter(list);
                    binding.foodListView.setAdapter(adapterListFood);
                } else {
                    // return to previous activity
                    finish();
                }
                binding.progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        viewAll = getIntent().getBooleanExtra("viewAll", false);

        if(categoryName == null) {
            categoryName = "Search Result for " + searchText;
        }
        binding.titleTxt.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}