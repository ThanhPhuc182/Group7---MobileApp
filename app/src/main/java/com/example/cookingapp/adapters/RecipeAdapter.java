package com.example.cookingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookingapp.R;
import com.example.cookingapp.models.Recipe;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    // 1. Khai báo interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }
    // Thống nhất tên biến là recipes
    private List<Recipe> recipes;
    private OnItemClickListener listener; // Biến listener
    public RecipeAdapter(List<Recipe> recipes, OnItemClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    // Gộp setData và updateRecipes thành một hàm cho gọn
    public void setData(List<Recipe> list) {
        this.recipes = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // Sử dụng đúng biến recipes đã khai báo ở trên
        Recipe recipe = recipes.get(position);
        if (recipe == null) return;

        // Hiển thị dữ liệu - Đảm bảo tên biến holder khớp với class ViewHolder bên dưới
        holder.tvTitle.setText(recipe.getName());
        holder.tvTime.setText(recipe.getTime() + " phút");

        // Hiển thị Calo (Đảm bảo bạn đã thêm tv_kcal vào item_recipe.xml)
        if (holder.tvKcal != null) {
            holder.tvKcal.setText(recipe.getCalories() + " kcal");
        }

        // Load ảnh từ URL sử dụng Glide
        Glide.with(holder.itemView.getContext())
                .load(recipe.getImage_url()) // Đây là link URL từ Firebase
                .placeholder(R.drawable.logo_cookingapp) // Ảnh hiện tạm khi đang tải
                .error(R.drawable.logo_cookingapp) // Ảnh hiện nếu link chết
                .centerCrop()
                .into(holder.imgRecipe);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(recipe);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recipes == null ? 0 : recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // Đặt tên biến trong Java đồng bộ với XML để dễ quản lý
        TextView tvTitle, tvTime, tvKcal;
        ImageView imgRecipe;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            // Thêm tv_kcal nếu trong layout item_recipe.xml của bạn có hiển thị calo
            tvKcal = itemView.findViewById(R.id.tv_kcal);
            imgRecipe = itemView.findViewById(R.id.img_recipe);
        }
    }
}