package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.databinding.ActivityIntroBinding;

// IntroActivity là màn hình chào mừng của ứng dụng
// Nó sẽ kiểm tra xem người dùng đã đăng nhập hay chưa
// Nếu đã đăng nhập -> Chuyển đến MainActivity
// Nếu chưa -> Hiển thị nút Login & Signup
public class IntroActivity extends BaseActivity {
    ActivityIntroBinding binding; // View Binding để truy cập các thành phần giao diện dễ dàng
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gán layout bằng View Binding
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setVariable();
        getWindow().setStatusBarColor(Color.parseColor("#FFE4B5"));

        if(mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Hàm xử lý sự kiện khi nhấn vào nút Login & Signup
    private void setVariable() {
        binding.loginBtn.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
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