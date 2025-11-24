package com.jhanpier.filmbox.ui;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.model.Movie;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.VH> {

    public interface OnClick { void onClick(Movie m); }

    private final List<Movie> list;
    private final OnClick listener;
    private final Context ctx;

    public MovieAdapter(List<Movie> list, OnClick l) {
        this.list = list;
        this.listener = l;
        this.ctx = null;
    }

    public MovieAdapter(List<Movie> list, Context ctx, OnClick l) {
        this.list = list;
        this.listener = l;
        this.ctx = ctx;
    }

    @NonNull
    @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        Movie m = list.get(position);
        holder.tvTitle.setText(m.getTitle());
        Glide.with(holder.itemView.getContext()).load(m.getImageUrl()).into(holder.iv);
        holder.itemView.setOnClickListener(v -> listener.onClick(m));
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvTitle;
        VH(@NonNull View v) {
            super(v);
            iv = v.findViewById(R.id.imgThumbnail);
            tvTitle = v.findViewById(R.id.tvTitle);
        }
    }
}
