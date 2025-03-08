package com.phatlee.food_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.OrderItem;
import com.phatlee.food_app.R;
import com.phatlee.food_app.databinding.ViewholderOrderItemBinding;
import java.util.List;
import java.util.concurrent.Executors;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderItem> orderItemList;
    private Context context;

    public OrderDetailAdapter(List<OrderItem> orderItemList, Context context) {
        this.orderItemList = orderItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderOrderItemBinding binding = ViewholderOrderItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Foods food = db.foodsDao().getFoodById(orderItem.foodId);

            ((Activity) context).runOnUiThread(() -> {
                if (food != null) {
                    holder.binding.titleTxt.setText(food.getTitle());
                    holder.binding.feeEachItem.setText("$" + (orderItem.quantity * food.getPrice()));
                    holder.binding.totalEachItem.setText(orderItem.quantity + " * $" + food.getPrice());

                    int imageResId = context.getResources().getIdentifier("food_" + food.getImagePath(), "drawable", context.getPackageName());
                    holder.binding.pic.setImageResource(imageResId != 0 ? imageResId : com.phatlee.food_app.R.drawable.logo);
                }
            });
        });
//        Foods food = foodList.get(position);
//        holder.binding.foodTitle.setText(food.getTitle());
//        holder.binding.foodPrice.setText("$" + food.getPrice());
//        double totalPrice = Integer.parseInt(holder.binding.foodQuantity.getText().toString()) * food.getPrice();
//        holder.binding.foodTotalPrice.setText("Qty: " + totalPrice);
//
//        int imageResId = context.getResources().getIdentifier("food_" + food.getImagePath(), "drawable", context.getPackageName());
//        holder.binding.foodImage.setImageResource(imageResId != 0 ? imageResId : R.drawable.logo);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderItemBinding binding;

        public ViewHolder(ViewholderOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}