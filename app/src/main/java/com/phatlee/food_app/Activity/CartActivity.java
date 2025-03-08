package com.phatlee.food_app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.phatlee.food_app.Adapter.CartAdapter;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Cart;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;
import com.phatlee.food_app.databinding.ActivityCartBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private double tax;
    private double totalFee;
    private double delivery;
    private List<Cart> cartList;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        setVariable();
        loadCartData();
    }

    private void loadCartData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            cartList = db.cartDao().getCartByUser(currentUserId);

            runOnUiThread(() -> {
                if (cartList.isEmpty()) {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    binding.scrollviewCart.setVisibility(View.GONE);
                } else {
                    binding.emptyTxt.setVisibility(View.GONE);
                    binding.scrollviewCart.setVisibility(View.VISIBLE);
                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                binding.cardView.setLayoutManager(linearLayoutManager);
                adapter = new CartAdapter(cartList, this, this::calculateCart);
                binding.cardView.setAdapter(adapter);
                calculateCart();
            });
        });
    }

    private void calculateCart() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            totalFee = 0;

            for (Cart cartItem : cartList) {
                Foods food = db.foodsDao().getFoodById(cartItem.foodId);
                if (food != null) {
                    totalFee += food.getPrice() * cartItem.quantity;
                }
            }

            double percentTax = 0.02; // 2% tax
            delivery = 10.0;
            if(totalFee <= 0) {
                delivery = 0;
            }
            tax = Math.round(totalFee * percentTax * 100.0) / 100;
            double total = Math.round((totalFee + tax + delivery) * 100) / 100;

            runOnUiThread(() -> {
                binding.totalFeeTxt.setText("$" + totalFee);
                binding.taxTxt.setText("$" + tax);
                binding.deliveryTxt.setText("$" + delivery);
                binding.totalTxt.setText("$" + total);
            });
        });
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.checkoutBtn.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                List<Cart> cartList = db.cartDao().getCartByUser(currentUserId);

                if (cartList.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Tính tổng tiền đơn hàng
                double totalPrice = 0;
                for (Cart cart : cartList) {
                    Foods food = db.foodsDao().getFoodById(cart.foodId);
                    if (food != null) {
                        totalPrice += food.getPrice() * cart.quantity;
                    }
                }

                // Tạo đơn hàng mới
                String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Order newOrder = new Order(currentUserId, orderDate, totalPrice, "Pending");

                long orderId = db.orderDao().insertOrder(newOrder); // Lưu vào DB

                // Lưu các món vào OrderItem
                List<OrderItem> orderItems = new ArrayList<>();
                for (Cart cart : cartList) {
                    orderItems.add(new OrderItem((int) orderId, cart.foodId, cart.quantity));
                }
                db.orderDao().insertOrderItems(orderItems);

                // Xóa giỏ hàng sau khi đặt hàng thành công
                db.cartDao().clearCartByUser(currentUserId);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }
}