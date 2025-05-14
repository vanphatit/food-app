package com.phatlee.food_app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.phatlee.food_app.Adapter.FoodListAdapter;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.databinding.ActivityListFoodsBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecentlyViewedActivity extends BaseActivity {
    ActivityListFoodsBinding binding;
    private List<Foods> viewedFoods = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.titleTxt.setText("Recently Viewed");
        binding.backBtn.setOnClickListener(v -> finish());

        loadViewedFoods();
    }

    private void loadViewedFoods() {
        SharedPreferences prefs = getSharedPreferences("recent_foods", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        viewedFoods.clear();
        Gson gson = new Gson();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Foods food = gson.fromJson(entry.getValue().toString(), Foods.class);
            viewedFoods.add(food);
        }

        if (viewedFoods.isEmpty()) {
            binding.foodListView.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.foodListView.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new FoodListAdapter(viewedFoods);
            binding.foodListView.setAdapter(adapter);
        }
    }
}
