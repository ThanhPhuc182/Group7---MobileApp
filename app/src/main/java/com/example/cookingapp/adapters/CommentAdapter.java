package com.example.cookingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.models.Comment;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.VH> {

    private final List<Comment> items = new ArrayList<>();

    public void setData(List<Comment> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Comment c = items.get(position);
        holder.tvUser.setText(c.getUserName() != null && !c.getUserName().isEmpty() ? c.getUserName() : "Người dùng");
        holder.tvText.setText(c.getText());
        holder.rb.setRating(c.getRating());
        Timestamp t = c.getCreatedAt();
        if (t != null) {
            Date d = t.toDate();
            holder.tvTime.setText(DateFormat.getDateTimeInstance().format(d));
        } else {
            holder.tvTime.setText("");
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvUser, tvText, tvTime;
        RatingBar rb;
        VH(@NonNull View v) {
            super(v);
            tvUser = v.findViewById(R.id.tv_comment_user);
            tvText = v.findViewById(R.id.tv_comment_text);
            tvTime = v.findViewById(R.id.tv_comment_time);
            rb = v.findViewById(R.id.rb_comment_rating);
        }
    }
}

