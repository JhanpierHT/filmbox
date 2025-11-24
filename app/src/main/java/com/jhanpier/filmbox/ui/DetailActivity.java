package com.jhanpier.filmbox.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jhanpier.filmbox.R;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String title = getIntent().getStringExtra("title");
        String year = getIntent().getStringExtra("year");
        String poster = getIntent().getStringExtra("poster");

        TextView tvTitle = findViewById(R.id.tvTitleDetail);
        TextView tvYear = findViewById(R.id.tvYearDetail);
        ImageView ivPoster = findViewById(R.id.ivPosterDetail);

        tvTitle.setText(title);
        tvYear.setText(year);

        Glide.with(this).load(poster).placeholder(R.drawable.ic_placeholder).into(ivPoster);
    }
}
