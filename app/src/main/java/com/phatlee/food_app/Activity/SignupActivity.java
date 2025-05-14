package com.phatlee.food_app.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.Repository.UserRepository;
import com.phatlee.food_app.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    FirebaseAuth mAuth;
    // Sử dụng UserRepository để giao tiếp với Firestore
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
        setVariable();
    }

    private void setVariable() {
        binding.signupBtn.setOnClickListener(v -> {
            String email = binding.userEdt.getText().toString().trim();
            String password = binding.passEdt.getText().toString().trim();
            String username = email.replace("@gmail.com", ""); // Tạo username mặc định
            String avatar = ""; // Ảnh đại diện mặc định

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Authentication successful");
                            // Tạo user mới; trường id sẽ được gán tự động qua Firestore
                            User newUser = new User(username, email, password, avatar, "", "");
                            userRepository.registerUser(newUser, new UserDaoFirestore.OnOperationCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    });
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "Error adding user to Firestore", e);
                                    runOnUiThread(() ->
                                            Toast.makeText(SignupActivity.this, "Error adding user to Firestore", Toast.LENGTH_SHORT).show());
                                }
                            });
                        } else {
                            Log.i(TAG, "failure: " + task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}