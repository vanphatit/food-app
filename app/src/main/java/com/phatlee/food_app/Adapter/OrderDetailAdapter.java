package com.phatlee.food_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.phatlee.food_app.Database.FoodsDaoFirestore;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.OrderItem;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.FoodsRepository;
import com.phatlee.food_app.databinding.ViewholderOrderItemBinding;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderItem> orderItemList;
    private Context context;
    private FoodsRepository foodsRepository;

    public OrderDetailAdapter(List<OrderItem> orderItemList, Context context) {
        this.orderItemList = orderItemList;
        this.context = context;
        this.foodsRepository = new FoodsRepository();
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
        // Sử dụng FoodsRepository để tải thông tin Food theo orderItem.foodId
        foodsRepository.getFoodById(orderItem.foodId, new FoodsDaoFirestore.OnFoodLoadedListener() {
            @Override
            public void onFoodLoaded(Foods food) {
                if (food != null) {
                    holder.binding.titleTxt.setText(food.getTitle());
                    holder.binding.feeEachItem.setText("$" + (orderItem.quantity * food.getPrice()));
                    holder.binding.totalEachItem.setText(orderItem.quantity + " * $" + food.getPrice());
                    int imageResId = context.getResources().getIdentifier("food_" + food.getImagePath(),
                            "drawable", context.getPackageName());
                    holder.binding.pic.setImageResource(imageResId != 0 ? imageResId : R.drawable.logo);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Có thể hiển thị thông báo lỗi nếu cần
            }
        });
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