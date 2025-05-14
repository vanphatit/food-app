package com.phatlee.food_app.Adapter;

import android.content.Context;
import android.content.Intent;
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

public class BestFoodsAdapter extends RecyclerView.Adapter<BestFoodsAdapter.ViewHolder> {
    private List<Foods> items;  // Thay vì ArrayList<Foods>, dùng List<Foods>
    private Context context;

    public BestFoodsAdapter(List<Foods> items) {  // Nhận List<Foods>
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_best_deal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foods food = items.get(position);
        holder.titleTxt.setText(food.getTitle());
        holder.priceTxt.setText("$" + food.getPrice());
        holder.stockQuantityTxt.setText( String.valueOf(food.getStockQuantity()));
        DecimalFormat df = new DecimalFormat("#.#");
        holder.starTxt.setText("" + df.format(food.getStar()));

        int imageResId = context.getResources().getIdentifier("food_" + food.getImagePath(), "drawable", context.getPackageName());
        holder.pic.setImageResource(imageResId != 0 ? imageResId : R.drawable.logo);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", food);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, starTxt, stockQuantityTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            starTxt = itemView.findViewById(R.id.starTxt);
            stockQuantityTxt = itemView.findViewById(R.id.stockQuantityTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}