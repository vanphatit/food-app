package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Utils.DataSeeder;
import com.phatlee.food_app.databinding.ActivityIntroBinding;

import java.util.concurrent.Executors;

// IntroActivity là màn hình chào mừng của ứng dụng
// Nó sẽ kiểm tra xem người dùng đã đăng nhập hay chưa
// Nếu đã đăng nhập -> Chuyển đến MainActivity
// Nếu chưa -> Hiển thị nút Login & Signup
public class IntroActivity extends BaseActivity {
    ActivityIntroBinding binding; // View Binding để truy cập các thành phần giao diện dễ dàng
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gán layout bằng View Binding
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Khởi tạo Room Database và seed dữ liệu
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Kiểm tra nếu database trống, thì seed dữ liệu từ JSON
            if (db.foodsDao().getAllFoods().isEmpty() &&
                    db.categoryDao().getAllCategories().isEmpty() &&
                    db.locationDao().getAllLocations().isEmpty() &&
                    db.priceDao().getAllPrices().isEmpty() &&
                    db.timeDao().getAllTimes().isEmpty()) {
                sharedPreferences.edit().remove("user_id").apply();
                DataSeeder.seedDatabase(db, this);
            }

            // Sau khi hoàn tất, chuyển sang MainActivity
            runOnUiThread(() -> {
                new Handler().postDelayed(() -> {
                    setVariable();
                    getWindow().setStatusBarColor(Color.parseColor("#FFE4B5"));

                    if(sharedPreferences.getInt("user_id", 0) != 0){
                        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1);
            });
        });


    }

    // Hàm xử lý sự kiện khi nhấn vào nút Login & Signup
    private void setVariable() {
        binding.loginBtn.setOnClickListener(v -> {
            if (sharedPreferences.getInt("user_id", 0) != 0) {
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
        });
        binding.signupBtn.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this, SignupActivity.class)));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}