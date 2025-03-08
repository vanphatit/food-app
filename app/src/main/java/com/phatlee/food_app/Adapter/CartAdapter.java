package com.phatlee.food_app.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phatlee.food_app.Activity.CartActivity;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Cart;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Helper.ChangeNumberItemsListener;
import com.phatlee.food_app.R;
import java.util.List;
import java.util.concurrent.Executors;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<Cart> cartList;
    private Context context;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private SharedPreferences sharedPreferences;

    public CartAdapter(List<Cart> cartList, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.cartList = cartList;
        this.context = context;
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart cartItem = cartList.get(position);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Foods food = db.foodsDao().getFoodById(cartItem.foodId);

            ((CartActivity) context).runOnUiThread(() -> {
                if (food != null) {
                    holder.title.setText(food.getTitle());
                    holder.feeEachItem.setText("$" + (cartItem.quantity * food.getPrice()));
                    holder.totalEachItem.setText(cartItem.quantity + " * $" + food.getPrice());
                    holder.num.setText(String.valueOf(cartItem.quantity));

                    int imageResId = context.getResources().getIdentifier("food_" + food.getImagePath(), "drawable", context.getPackageName());
                    holder.pic.setImageResource(imageResId != 0 ? imageResId : R.drawable.logo);

                    holder.plusItem.setOnClickListener(v -> updateCart(cartItem, cartItem.quantity + 1));
                    holder.minusItem.setOnClickListener(v -> {
                        updateCart(cartItem, cartItem.quantity - 1);
                    });
                }
            });
        });
    }

    private void updateCart(Cart cartItem, int newQuantity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            sharedPreferences = context.getSharedPreferences("UserSession", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);

            if (newQuantity <= 0) {
                db.cartDao().removeFromCart(userId, cartItem.foodId);
                cartList.remove(cartItem);
            } else {
                db.cartDao().update(new Cart(userId, cartItem.foodId, newQuantity));
                cartItem.quantity = newQuantity;
            }

            ((CartActivity) context).runOnUiThread(() -> {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            });
        });
    }

    private void removeFromCart(Cart cartItem) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.cartDao().removeFromCart(cartItem.userId, cartItem.foodId);
            cartList.remove(cartItem);

            ((CartActivity) context).runOnUiThread(() -> {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            });
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem, plusItem, minusItem, totalEachItem, num;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            num = itemView.findViewById(R.id.numberItemTxt);
        }
    }
}
