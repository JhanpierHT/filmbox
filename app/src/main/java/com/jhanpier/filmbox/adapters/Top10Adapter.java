package com.jhanpier.filmbox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jhanpier.filmbox.R;

import java.util.List;

public class Top10Adapter extends RecyclerView.Adapter<Top10Adapter.ViewHolder> {

    private final List<Integer> posters;

    public Top10Adapter(List<Integer> posters) { this.posters = posters; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView rank;

        public ViewHolder(View v) {
            super(v);
            poster = v.findViewById(R.id.imgPoster);
            rank = v.findViewById(R.id.txtRank);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top10_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.poster.setImageResource(posters.get(position));
        holder.rank.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() { return posters.size(); }
}
