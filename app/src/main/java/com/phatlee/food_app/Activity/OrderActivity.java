package com.phatlee.food_app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.phatlee.food_app.Adapter.OrderAdapter;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.databinding.ActivityOrderBinding;
import java.util.List;
import java.util.concurrent.Executors;

public class OrderActivity extends BaseActivity {
    private ActivityOrderBinding binding;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        setVariable();
        loadOrderData();
    }

    private void loadOrderData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            orderList = db.orderDao().getOrdersByUser(currentUserId);

            runOnUiThread(() -> {
                if (orderList.isEmpty()) {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    binding.cardView.setVisibility(View.GONE);
                } else {
                    binding.emptyTxt.setVisibility(View.GONE);
                    binding.cardView.setVisibility(View.VISIBLE);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                binding.cardView.setLayoutManager(layoutManager);
                adapter = new OrderAdapter(orderList, this);
                binding.cardView.setAdapter(adapter);
            });
        });
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}