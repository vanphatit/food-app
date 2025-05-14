package com.phatlee.food_app.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Activity.CartActivity;
import com.phatlee.food_app.Activity.DetailActivity;
import com.phatlee.food_app.Activity.MyProfileActivity;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Entity.Cart;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.Helper.ChangeNumberItemsListener;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Database.FoodsDaoFirestore;
import com.phatlee.food_app.Database.CartDaoFirestore;
import com.phatlee.food_app.Repository.CartRepository;
import com.phatlee.food_app.Repository.FoodsRepository;
import com.phatlee.food_app.Repository.UserRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<Cart> cartList;
    private Context context;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private FirebaseAuth mAuth;
    private UserRepository userRepository;

    String userId;

    public CartAdapter(List<Cart> cartList, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.cartList = cartList;
        this.context = context;
        this.changeNumberItemsListener = changeNumberItemsListener;

        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();

        // Lấy thông tin user dựa trên email hiện tại
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Gọi phương thức đồng bộ để lấy user theo email
                User user = userRepository.getUserByEmail(mAuth.getCurrentUser().getEmail());
                if (user != null) {
                    userId = user.getId();
                } else {
                    userId = null;
                }
            } catch (Exception e) {
                userId = null;
            }
        });
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

        // Sử dụng repository để lấy thông tin Food
        Executors.newSingleThreadExecutor().execute(() -> {
            FoodsRepository foodsRepository = new FoodsRepository();
            foodsRepository.getFoodById(cartItem.foodId, new FoodsDaoFirestore.OnFoodLoadedListener() {
                @Override
                public void onFoodLoaded(Foods food) {
                    ((CartActivity) context).runOnUiThread(() -> {
                        if (food != null) {
                            holder.title.setText(food.getTitle());
                            holder.feeEachItem.setText("$" + (cartItem.quantity * food.getPrice()));
                            holder.totalEachItem.setText(cartItem.quantity + " * $" + food.getPrice());
                            holder.num.setText(String.valueOf(cartItem.quantity));

                            int imageResId = context.getResources().getIdentifier("food_" + food.getImagePath(), "drawable", context.getPackageName());
                            holder.pic.setImageResource(imageResId != 0 ? imageResId : R.drawable.logo);

                            holder.plusItem.setOnClickListener(v -> updateCart(cartItem, cartItem.quantity + 1));
                            holder.minusItem.setOnClickListener(v -> updateCart(cartItem, cartItem.quantity - 1));
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {
                    ((CartActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Error loading food info", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void updateCart(Cart cartItem, int newQuantity) {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Sử dụng FoodsRepository để cập nhật stock của Food
            FoodsRepository foodsRepository = new FoodsRepository();
            foodsRepository.getFoodById(cartItem.foodId, new FoodsDaoFirestore.OnFoodLoadedListener() {
                @Override
                public void onFoodLoaded(Foods food) {
                    if (food == null) return;
                    int diff = newQuantity - cartItem.quantity;
                    // Nếu tăng số lượng: kiểm tra stock có đủ không
                    if (diff > 0) {
                        if (food.getStockQuantity() < diff) {
                            ((CartActivity) context).runOnUiThread(() ->
                                    Toast.makeText(context, "Not enough stock", Toast.LENGTH_SHORT).show());
                            return;
                        } else {
                            int newFoodStock = food.getStockQuantity() - diff;
                            foodsRepository.updateStockQuantity(food.getId(), newFoodStock, new FoodsDaoFirestore.OnOperationCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    food.setStockQuantity(newFoodStock);
                                    proceedUpdate(cartItem, newQuantity, userId);
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    ((CartActivity) context).runOnUiThread(() ->
                                            Toast.makeText(context, "Error updating stock", Toast.LENGTH_SHORT).show());
                                }
                            });
                        }
                    } else if (diff < 0) { // Giảm số lượng: tăng lại stock
                        int newFoodStock = food.getStockQuantity() - diff; // vì diff < 0, (-diff) cộng vào stock
                        foodsRepository.updateStockQuantity(food.getId(), newFoodStock, new FoodsDaoFirestore.OnOperationCompleteListener() {
                            @Override
                            public void onSuccess() {
                                food.setStockQuantity(newFoodStock);
                                if(newQuantity <= 0) {
                                    removeFromCart(cartItem);
                                } else {
                                    proceedUpdate(cartItem, newQuantity, userId);
                                }
                            }
                            @Override
                            public void onFailure(Exception e) { }
                        });
                    } else {
                        // diff == 0, không thay đổi gì
                        proceedUpdate(cartItem, newQuantity, userId);
                    }
                }
                @Override
                public void onFailure(Exception e) { }
            });
        });
    }

    // Hàm helper cập nhật số lượng Cart thông qua CartRepository
    private void proceedUpdate(Cart cartItem, int newQuantity, String userId) {
        CartRepository cartRepository = new CartRepository();
        if (newQuantity <= 0) {
            cartRepository.removeFromCart(userId, cartItem.foodId, new CartDaoFirestore.OnOperationCompleteListener() {
                @Override
                public void onSuccess() {
                    cartList.remove(cartItem);
                    ((CartActivity) context).runOnUiThread(() -> {
                        notifyDataSetChanged();
                        changeNumberItemsListener.change();
                    });
                }
                @Override
                public void onFailure(Exception e) { }
            });
        } else {
            cartItem.quantity = newQuantity;
            cartRepository.updateCart(cartItem, new CartDaoFirestore.OnOperationCompleteListener() {
                @Override
                public void onSuccess() {
                    ((CartActivity) context).runOnUiThread(() -> {
                        notifyDataSetChanged();
                        changeNumberItemsListener.change();
                    });
                }
                @Override
                public void onFailure(Exception e) { }
            });
        }
    }

    private void removeFromCart(Cart cartItem) {
        CartRepository cartRepository = new CartRepository();
        cartRepository.removeFromCart(userId, cartItem.foodId, new CartDaoFirestore.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                cartList.remove(cartItem);
                ((CartActivity) context).runOnUiThread(() -> {
                    notifyDataSetChanged();
                    changeNumberItemsListener.change();
                });
            }
            @Override
            public void onFailure(Exception e) { }
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