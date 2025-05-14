package com.phatlee.food_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phatlee.food_app.Entity.Review;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.UserRepository;
import com.phatlee.food_app.databinding.ViewholderUserReviewBinding;

import java.util.HashMap;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviewList;
    private Context context;
    private HashMap<String, User> userMap = new HashMap<>(); // lưu userId -> User
    private UserRepository userRepository;

    public ReviewAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
        userRepository = new UserRepository();
        loadUserData();
    }

    private void loadUserData() {
        userRepository.getAllUsers(new com.phatlee.food_app.Database.UserDaoFirestore.OnUsersLoadedListener() {
            @Override
            public void onUsersLoaded(List<User> users) {
                userMap.clear();
                for (User user : users) {
                    userMap.put(user.getId(), user);
                }
                ((Activity) context).runOnUiThread(() -> notifyDataSetChanged());
            }

            @Override
            public void onFailure(Exception e) {
                ((Activity) context).runOnUiThread(() -> notifyDataSetChanged());
            }
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
        User user = userMap.get(review.userId);
        if (user != null) {
            holder.binding.userName.setText(user.getName());
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Glide.with(context).load(user.getAvatar()).circleCrop().into(holder.binding.userAvatar);
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