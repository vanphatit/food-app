package com.phatlee.food_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;
import com.phatlee.food_app.R;
import com.phatlee.food_app.databinding.ViewholderOrderBinding;
import java.util.List;
import java.util.concurrent.Executors;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> orderList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
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
        holder.binding.orderIdTxt.setText("Order ID: " + order.getId());
        holder.binding.orderDateTxt.setText("Date: " + order.getOrderDate());
        holder.binding.orderTotalPriceTxt.setText("Total: $" + order.getTotalPrice());
        holder.binding.orderStatusTxt.setText("Status: " + order.getStatus());

        // Lấy danh sách món ăn của đơn hàng từ Database
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            List<OrderItem> orderItems = db.orderDao().getOrderItems(order.id);

            ((Activity) context).runOnUiThread(() -> {
                OrderDetailAdapter itemAdapter = new OrderDetailAdapter(orderItems, context);
                holder.binding.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                holder.binding.orderItemsRecyclerView.setAdapter(itemAdapter);
            });
        });

        holder.binding.orderStatusTxt.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                Order order1 = db.orderDao().getOrderById(order.id);
                if (order1 != null) {
                    if (order1.getStatus().equals("Pending")) {
                        order1.setStatus("Done");
                    }
                    db.orderDao().updateOrderStatus(order1.id, order1.getStatus());
                }
                ((Activity) context).runOnUiThread(() -> {
                    holder.binding.orderStatusTxt.setText("Status: " + order1.getStatus());
                    holder.binding.orderStatusTxt.setTextColor(context.getResources().getColor(R.color.blue_grey));
                });
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
}
