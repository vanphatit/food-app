package com.phatlee.food_app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Adapter.OrderAdapter;
import com.phatlee.food_app.Database.OrderDaoFirestore;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.Repository.OrderRepository;
import com.phatlee.food_app.Repository.UserRepository;
import com.phatlee.food_app.databinding.ActivityOrderBinding;

import java.util.List;

public class OrderActivity extends BaseActivity {
    private ActivityOrderBinding binding;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private String currentUserId;
    private OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderRepository = new OrderRepository();

        currentUserId = getIntent().getStringExtra("userid");

        setVariable();
        loadOrderData();
    }

    private void loadOrderData() {
        orderRepository.getOrdersByUser(currentUserId, new OrderDaoFirestore.OnOrdersLoadedListener() {
            @Override
            public void onOrdersLoaded(List<Order> orders) {
                orderList = orders;
                runOnUiThread(() -> {
                    if (orderList == null || orderList.isEmpty()) {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                        binding.cardView.setVisibility(View.GONE);
                    } else {
                        binding.emptyTxt.setVisibility(View.GONE);
                        binding.cardView.setVisibility(View.VISIBLE);
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(OrderActivity.this);
                    binding.cardView.setLayoutManager(layoutManager);
                    adapter = new OrderAdapter(orderList, OrderActivity.this);
                    binding.cardView.setAdapter(adapter);
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(OrderActivity.this, "Error loading orders", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}