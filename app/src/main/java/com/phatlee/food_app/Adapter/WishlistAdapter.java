package com.phatlee.food_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phatlee.food_app.Activity.DetailActivity;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.databinding.ViewholderWishlistBinding;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    private List<Foods> wishlistFoods;
    private Context context;

    public WishlistAdapter(List<Foods> wishlistFoods, Context context) {
        this.wishlistFoods = wishlistFoods;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderWishlistBinding binding = ViewholderWishlistBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foods food = wishlistFoods.get(position);
        holder.binding.foodName.setText(food.getTitle());
        holder.binding.foodPrice.setText("$" + food.getPrice());

        int imageResId = context.getResources().getIdentifier("food_" + food.getImagePath(), "drawable", context.getPackageName());

        Glide.with(context).load(imageResId != 0 ? imageResId : com.phatlee.food_app.R.drawable.logo).circleCrop().into(holder.binding.foodImg);

        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", food);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wishlistFoods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewholderWishlistBinding binding;

        public ViewHolder(ViewholderWishlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}