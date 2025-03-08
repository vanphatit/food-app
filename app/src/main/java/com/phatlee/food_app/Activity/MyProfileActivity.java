package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.databinding.ActivityMyProfileBinding;

public class MyProfileActivity extends AppCompatActivity {
    private ActivityMyProfileBinding binding;
    private AppDatabase db;
    private int userId; // ID của user hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserProfile();

        binding.backHomeBtn.setOnClickListener(v -> finish());

        binding.button3.setOnClickListener(v -> {
            updateUserProfile();
        });
    }

    private void loadUserProfile() {
        new Thread(() -> {
            User user = db.userDao().getUserById(userId);
            runOnUiThread(() -> {
                if (user != null) {
                    binding.textView10.setText(user.name);
                    binding.textView13.setText(user.email);
                    binding.textView26.setText(user.phone);
                    binding.textView27.setText(user.address);
                } else {
                    Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void updateUserProfile() {
        new Thread(() -> {
            String newName = binding.textView10.getText().toString();
            String newPhone = binding.textView26.getText().toString();
            String newAddress = binding.textView27.getText().toString();

            db.userDao().updateUserProfile(userId, newName, newPhone, newAddress);

            runOnUiThread(() -> {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}