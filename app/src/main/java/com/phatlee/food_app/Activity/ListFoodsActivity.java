package com.phatlee.food_app.Activity;

import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.phatlee.food_app.Adapter.FoodListAdapter;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Repository.FoodsRepository;
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
    private FoodsRepository foodsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        foodsRepository = new FoodsRepository();
        getIntentExtra();
        initList();
        setVariable();
    }

    private void setVariable() {
        // Nếu có các xử lý khác, thêm vào đây
    }

    private void initList() {
        binding.progressBar.setVisibility(View.VISIBLE);
        foodsRepository.getAllFoods(new com.phatlee.food_app.Database.FoodsDaoFirestore.OnFoodsLoadedListener() {
            @Override
            public void onFoodsLoaded(List<Foods> foods) {
                List<Foods> list = new ArrayList<>();
                if (viewAll || (isSearch && (searchText == null || searchText.trim().isEmpty()))) {
                    list = foods;
                } else if (isSearch && searchText != null && !searchText.trim().isEmpty()) {
                    for (Foods food : foods) {
                        if (food.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                            list.add(food);
                        }
                    }
                } else {
                    for (Foods food : foods) {
                        if (food.getCategoryId() == categoryId) {
                            list.add(food);
                        }
                    }
                }
                List<Foods> finalList = list;
                runOnUiThread(() -> {
                    if (!finalList.isEmpty()) {
                        binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodsActivity.this, 2));
                        adapterListFood = new FoodListAdapter(finalList);
                        binding.foodListView.setAdapter(adapterListFood);
                    } else {
                        finish();
                    }
                    binding.progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));
            }
        });
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        viewAll = getIntent().getBooleanExtra("viewAll", false);

        if (categoryName == null) {
            categoryName = "Search Result for " + searchText;
        }
        binding.titleTxt.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}