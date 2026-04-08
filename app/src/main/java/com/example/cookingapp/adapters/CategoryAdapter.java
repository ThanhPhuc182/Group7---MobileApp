package com.example.cookingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookingapp.R;
import com.google.android.material.chip.Chip;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories;

    public CategoryAdapter(List<String> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryName = categories.get(position);
        holder.chip.setText(categoryName);

        // Bạn có thể thêm sự kiện Click tại đây nếu muốn lọc món ăn
        holder.chip.setOnClickListener(v -> {
            // TODO: Xử lý lọc dữ liệu theo categoryName
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = (Chip) itemView; // Vì item_category.xml là một thẻ Chip duy nhất
        }
    }
}