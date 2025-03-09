package com.phatlee.food_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Review;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.R;
import com.phatlee.food_app.databinding.ViewholderUserReviewBinding;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviewList;
    private Context context;
    private HashMap<Integer, User> userMap = new HashMap<>(); // Lưu userId -> User Object (Name + Avatar)

    public ReviewAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
        loadUserData();
    }

    // ✅ Load dữ liệu user từ database (Tên + Avatar)
    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            List<User> userList = db.userDao().getAllUsers(); // Lấy tất cả user

            for (User user : userList) {
                userMap.put(user.id, user); // Lưu user vào HashMap
            }

            ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderUserReviewBinding binding = ViewholderUserReviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        User user = userMap.get(review.userId); // Lấy User từ HashMap

        if (user != null) {
            holder.binding.userName.setText(user.name);

            // ✅ Load Avatar từ file lưu trong database
            if (user.avatar != null && !user.avatar.isEmpty()) {
                Glide.with(context).load(user.avatar).circleCrop().into(holder.binding.userAvatar);
            } else {
                holder.binding.userAvatar.setImageResource(R.drawable.profile);
            }
        } else {
            holder.binding.userName.setText("Unknown User");
            holder.binding.userAvatar.setImageResource(R.drawable.profile);
        }

        holder.binding.userComment.setText(review.comment);
        holder.binding.userRatingBar.setRating(review.rating);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewholderUserReviewBinding binding;

        public ViewHolder(ViewholderUserReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}