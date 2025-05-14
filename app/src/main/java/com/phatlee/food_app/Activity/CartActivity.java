package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Adapter.CartAdapter;
import com.phatlee.food_app.Database.CartDaoFirestore;
import com.phatlee.food_app.Database.FoodsDaoFirestore;
import com.phatlee.food_app.Database.OrderDaoFirestore;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Entity.Cart;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.CartRepository;
import com.phatlee.food_app.Repository.FoodsRepository;
import com.phatlee.food_app.Repository.OrderRepository;
import com.phatlee.food_app.Repository.UserRepository;
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
    private String currentUserId;
    private boolean isOrdering = false;

    // Repository instances sử dụng Firestore
    private CartRepository cartRepository;
    private FoodsRepository foodsRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    private String selectedAddressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUserId = getIntent().getStringExtra("userid");

        // Khởi tạo các repository Firestore
        cartRepository = new CartRepository();
        foodsRepository = new FoodsRepository();
        orderRepository = new OrderRepository();
        userRepository = new UserRepository();

        setVariable();
        loadCartData();
    }

    private void loadCartData() {
        cartRepository.getCartByUser(currentUserId, new CartDaoFirestore.OnCartListLoadedListener() {
            @Override
            public void onCartListLoaded(List<Cart> carts) {
                cartList = carts;
                for (Cart cart : cartList) {
                    Log.d("======= Cart", cart.getCartId());
                }
                if (cartList == null || cartList.isEmpty()) {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    binding.scrollviewCart.setVisibility(View.GONE);
                } else {
                    binding.emptyTxt.setVisibility(View.GONE);
                    binding.scrollviewCart.setVisibility(View.VISIBLE);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CartActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.cardView.setLayoutManager(linearLayoutManager);
                adapter = new CartAdapter(cartList, CartActivity.this, CartActivity.this::calculateCart);
                binding.cardView.setAdapter(adapter);
                calculateCart();
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(CartActivity.this, "Error loading cart", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void calculateCart() {
        // Tính tổng tiền giỏ hàng bằng cách duyệt qua các cart item và lấy giá Food từ Firestore.
        Executors.newSingleThreadExecutor().execute(() -> {
            if (cartList == null || cartList.isEmpty()) {
                runOnUiThread(() -> {
                    binding.totalFeeTxt.setText("$0");
                    binding.taxTxt.setText("$0");
                    binding.deliveryTxt.setText("$0");
                    binding.totalTxt.setText("$0");
                });
                return;
            }
            final double[] computedTotal = {0};
            final int totalItems = cartList.size();
            final int[] processedCount = {0};
            for (Cart cartItem : cartList) {
                foodsRepository.getFoodById(cartItem.foodId, new FoodsDaoFirestore.OnFoodLoadedListener() {
                    @Override
                    public void onFoodLoaded(Foods food) {
                        if (food != null) {
                            computedTotal[0] += food.getPrice() * cartItem.quantity; // Tính tổng tiền giỏ hàng
                        }
                        synchronized(processedCount) { // Đồng bộ hóa xử lý khi duyệt qua các cart item
                            processedCount[0]++; // Đếm số lượng cart item đã xử lý
                            if (processedCount[0] == totalItems) { // Nếu đã xử lý hết các cart item
                                updateCartTotals(computedTotal[0]); // Cập nhật tổng tiền giỏ hàng
                            }
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        synchronized(processedCount) {
                            processedCount[0]++;
                            if (processedCount[0] == totalItems) {
                                updateCartTotals(computedTotal[0]);
                            }
                        }
                    }
                });
            }
        });
    }

    private void updateCartTotals(double totalFeeValue) {
        double percentTax = 0.02; // 2% tax
        delivery = totalFeeValue <= 0 ? 0 : 10.0;
        tax = Math.round(totalFeeValue * percentTax * 100.0) / 100;
        double total = Math.round((totalFeeValue + tax + delivery) * 100) / 100;
        runOnUiThread(() -> {
            binding.totalFeeTxt.setText("$" + totalFeeValue);
            binding.taxTxt.setText("$" + tax);
            binding.deliveryTxt.setText("$" + delivery);
            binding.totalTxt.setText("$" + total);
        });
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.checkoutBtn.setOnClickListener(v -> {
            // Vô hiệu hóa nút checkout để ngăn người dùng bấm nhiều lần
            binding.checkoutBtn.setEnabled(false);

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    // Lấy danh sách cart của user theo cách đồng bộ
                    List<Cart> carts = cartRepository.getCartByUserSync(currentUserId);
                    if (carts == null || carts.isEmpty()) {
                        runOnUiThread(() -> {
                            Toast.makeText(CartActivity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                            binding.checkoutBtn.setEnabled(true);
                        });
                        return;
                    }

                    // Tính tổng tiền đơn hàng
                    double computedTotal = 0;
                    for (Cart cart : carts) {
                        Foods food = foodsRepository.getFoodByIdSync(cart.foodId);
                        if (food != null) {
                            computedTotal += food.getPrice() * cart.quantity;
                        }
                    }

                    // Tạo đơn hàng mới
                    String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    Order newOrder = new Order(currentUserId, orderDate, computedTotal, "Pending");
                    // insertOrderSync trả về document id (String)
                    String orderId = orderRepository.insertOrderSync(newOrder);

                    // Tạo danh sách OrderItem (với orderId là chuỗi)
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (Cart cart : carts) {
                        orderItems.add(new OrderItem(orderId, cart.foodId, cart.quantity));
                    }

                    // Chèn OrderItems vào Firestore
                    orderRepository.insertOrderItemsSync(orderItems);

                    // Xóa cart của user sau khi đặt hàng thành công
                    cartRepository.clearCartByUserSync(currentUserId);

                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                        loadCartData();
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, "Error creating order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.checkoutBtn.setEnabled(true);
                    });
                }
            });
        });

        binding.shippingAddressTxt.setOnClickListener(v -> {
            Intent i = new Intent(CartActivity.this, ManageAddressActivity.class);
            startActivityForResult(i, 9001);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001 && resultCode == RESULT_OK) {
            String fullAddress = data.getStringExtra("fullAddress");
            selectedAddressId = data.getStringExtra("addressId");
            binding.shippingAddressTxt.setText("📍 " + fullAddress);
        }
    }

    private void createOrder(List<Cart> carts, double totalPrice) {
        String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Order newOrder = new Order(currentUserId, orderDate, totalPrice, "Pending");
        orderRepository.insertOrder(newOrder, new OrderDaoFirestore.OnInsertOrderListener() {
            @Override
            public void onOrderInserted(String documentId) {
                if(!isOrdering) {
                    return;
                }
                List<OrderItem> orderItems = new ArrayList<>();
                for (Cart cart : carts) {
                    Log.d("======= Cart", " ================ Count: " + carts.size());
                    orderItems.add(new OrderItem(documentId, cart.foodId, cart.quantity));
                    Log.d("======= Cart", " ================ Adding order item: " + cart.foodId + " - " + cart.quantity);
                }
                orderRepository.insertOrderItems(orderItems, new OrderDaoFirestore.OnOperationCompleteListener() {
                    @Override
                    public void onSuccess() {
                        cartRepository.clearCartByUser(currentUserId, new CartDaoFirestore.OnOperationCompleteListener() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(() -> {
                                    Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                    isOrdering = false;
                                });
                            }
                            @Override
                            public void onFailure(Exception e) {
                                runOnUiThread(() -> Toast.makeText(CartActivity.this, "Error clearing cart", Toast.LENGTH_SHORT).show());
                            }
                        });
                    }
                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> Toast.makeText(CartActivity.this, "Error inserting order items", Toast.LENGTH_SHORT).show());
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(CartActivity.this, "Error creating order", Toast.LENGTH_SHORT).show());
            }
        });
    }
}