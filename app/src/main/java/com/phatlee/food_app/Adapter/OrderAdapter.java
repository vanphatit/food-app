package com.phatlee.food_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phatlee.food_app.Database.OrderDaoFirestore;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.OrderRepository;
import com.phatlee.food_app.databinding.ViewholderOrderBinding;

import java.util.List;
import java.util.concurrent.Executors;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> orderList;
    private Context context;
    private OrderRepository orderRepository;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        orderRepository = new OrderRepository();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderOrderBinding binding = ViewholderOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        // Shorten the order ID for display
        String orderId = order.getOrderId();
        String shortOrderId = (orderId != null && orderId.length() > 4)
                ? orderId.substring(0, 4) + "....."
                : orderId;
        holder.binding.orderIdTxt.setText("Order ID: " + shortOrderId);
        holder.binding.orderDateTxt.setText("Date: " + order.getOrderDate());
        holder.binding.orderTotalPriceTxt.setText("Total: $" + order.getTotalPrice());
        holder.binding.orderStatusTxt.setText("Status: " + order.getStatus());

        // Lấy danh sách OrderItem từ Firestore qua OrderRepository
        orderRepository.getOrderItems(order.getOrderId(), new OrderDaoFirestore.OnOrderItemsLoadedListener() {
            @Override
            public void onOrderItemsLoaded(List<OrderItem> orderItems) {
                ((Activity) context).runOnUiThread(() -> {
                    // Tạo adapter chi tiết đơn hàng
                    OrderDetailAdapter itemAdapter = new OrderDetailAdapter(orderItems, context);
                    holder.binding.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    holder.binding.orderItemsRecyclerView.setAdapter(itemAdapter);
                });
            }
            @Override
            public void onFailure(Exception e) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Error loading order items", Toast.LENGTH_SHORT).show());
            }
        });

        // Cập nhật trạng thái đơn hàng khi bấm vào TextView
        holder.binding.orderStatusTxt.setOnClickListener(v -> {
            orderRepository.getOrderById(order.getOrderId(), new OrderDaoFirestore.OnOrderLoadedListener() {
                @Override
                public void onOrderLoaded(Order orderLoaded) {
                    if (orderLoaded != null) {
                        if ("Pending".equals(orderLoaded.getStatus())) {
                            orderLoaded.setStatus("Done");
                        }
                        orderRepository.updateOrderStatus(orderLoaded.getOrderId(), orderLoaded.getStatus(),
                                new OrderDaoFirestore.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        ((Activity) context).runOnUiThread(() -> {
                                            holder.binding.orderStatusTxt.setText("Status: " + orderLoaded.getStatus());
                                            holder.binding.orderStatusTxt.setTextColor(context.getResources().getColor(R.color.blue_grey));
                                        });
                                    }
                                    @Override
                                    public void onFailure(Exception e) {
                                        ((Activity) context).runOnUiThread(() ->
                                                Toast.makeText(context, "Error updating order status", Toast.LENGTH_SHORT).show());
                                    }
                                });
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Error fetching order", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderBinding binding;

        public ViewHolder(ViewholderOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private String getShortOrderId(String orderId) {
        if (orderId != null && orderId.length() > 4) {
            return orderId.substring(0, 4) + ".....";
        }
        return orderId;
    }
}