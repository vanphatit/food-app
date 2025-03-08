package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        setVariable();
    }

    private void setVariable() {
        binding.signupBtn.setOnClickListener(v -> {
            String email = binding.userEdt.getText().toString().trim();
            String password = binding.passEdt.getText().toString().trim();
            String username = "User" + System.currentTimeMillis(); // Tạo username mặc định
            String avatar = "default_avatar.png"; // Ảnh đại diện mặc định

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                if (db.userDao().checkEmailExists(email)) {
                    runOnUiThread(() -> Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show());
                } else {
                    User newUser = new User(email, email, password, "", "", "");
                    db.userDao().registerUser(newUser);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                }
            }).start();
        });
    }
}