package com.jhanpier.filmbox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jhanpier.filmbox.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClick {
        void onClick(String category);
    }

    private final List<String> categories;
    private final OnCategoryClick listener;

    public CategoryAdapter(List<String> categories, OnCategoryClick listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category;

        public ViewHolder(View v) {
            super(v);
            category = v.findViewById(R.id.txtCategory);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.category.setText(category);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
