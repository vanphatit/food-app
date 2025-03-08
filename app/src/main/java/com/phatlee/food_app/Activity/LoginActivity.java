package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.databinding.ActivityLoginBinding;
import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private AppDatabase db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        setVariable();
    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(v -> {
            String email = binding.userEdt.getText().toString().trim();
            String password = binding.passEdt.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                User user = db.userDao().login(email, password);
                runOnUiThread(() -> {
                    if (user != null) {
                        sharedPreferences.edit().putInt("user_id", user.id).apply();
                        String user_name = user.name.replace("@gmail.com", "");
                        sharedPreferences.edit().putString("user_name",user_name).apply();
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        binding.txvSignup.setOnClickListener(
                v -> startActivity(
                        new Intent(LoginActivity.this, SignupActivity.class)));
    }
}