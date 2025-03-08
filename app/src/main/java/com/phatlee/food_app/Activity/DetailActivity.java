package com.phatlee.food_app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Cart;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.R;
import com.phatlee.food_app.databinding.ActivityDetailBinding;

import java.util.concurrent.Executors;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("user_id", -1);

        binding.backBtn.setOnClickListener(v -> {
            Intent backMess = new Intent();
            backMess.putExtra("message", "Back from Detail Activity");
            setResult(RESULT_OK, backMess);
            finish();
        });

        int imageResId = getResources().getIdentifier("food_" + object.getImagePath(), "drawable", getPackageName());
        binding.pic.setImageResource(imageResId != 0 ? imageResId : R.drawable.logo);

        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num * object.getPrice()) + "$");

        binding.plusBtn.setOnClickListener(v -> {
            num++;
            binding.numTxt.setText(num + " ");
            binding.totalTxt.setText("$" + (num * object.getPrice()));
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num--;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                Cart existingCart = db.cartDao().getCartItem(currentUserId, object.getId());

                if (existingCart != null) {
                    // Nếu đã có trong giỏ hàng -> tăng số lượng
                    existingCart.quantity += num;
                    db.cartDao().update(existingCart);
                } else {
                    // Nếu chưa có -> thêm mới vào giỏ hàng
                    db.cartDao().insert(new Cart(currentUserId, object.getId(), num));
                }

                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show());
            });
        });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}