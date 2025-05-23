package com.phatlee.food_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phatlee.food_app.Activity.DetailActivity;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.R;

import java.text.DecimalFormat;
import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.viewholder> {
    List<Foods> items;
    Context context;

    public FoodListAdapter(List<Foods> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FoodListAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_food, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.stockQuantityTxt.setText(String.valueOf(items.get(position).getStockQuantity()));
        holder.priceTxt.setText("$" + items.get(position).getPrice());
        DecimalFormat df = new DecimalFormat("#.#");
        holder.rateTxt.setText("" + df.format(items.get(position).getStar()) );

        Foods foodItem = items.get(position);

        // Ép buộc xử lý lại ImagePath trước khi hiển thị
        foodItem.setImagePath(foodItem.getImagePath());

        int imageResId = context.getResources().getIdentifier("food_" + foodItem.getImagePath(), "drawable", context.getPackageName());

        Log.d("=================DEBUG", "ImagePath sau khi xử lý: food_" + foodItem.getImagePath() + ", Resource ID: " + imageResId);

        if (imageResId != 0) {
            holder.pic.setImageResource(imageResId);
        } else {
            holder.pic.setImageResource(R.drawable.logo); // Nếu không tìm thấy ảnh
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, rateTxt, stockQuantityTxt;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
            stockQuantityTxt = itemView.findViewById(R.id.stockQuantityTxt);
            pic = itemView.findViewById(R.id.img);

        }
    }
}