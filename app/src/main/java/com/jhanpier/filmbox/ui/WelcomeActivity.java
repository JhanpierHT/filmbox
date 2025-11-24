package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.jhanpier.filmbox.R;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    private LinearLayout indicatorLayout;
    private SliderAdapter sliderAdapter;
    private ViewPager2 imageSlider;
    private final List<Integer> imageList = Arrays.asList(
            R.drawable.welcome_image1, R.drawable.welcome_image2, R.drawable.welcome_image3,
            R.drawable.welcome_image4, R.drawable.welcome_image5, R.drawable.welcome_image6,
            R.drawable.welcome_image7, R.drawable.welcome_image8
    );

    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        imageSlider = findViewById(R.id.imageSlider);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        sliderAdapter = new SliderAdapter(imageList);
        imageSlider.setAdapter(sliderAdapter);
        imageSlider.setPageTransformer(new ZoomOutPageTransformer());

        setupIndicators();
        setCurrentIndicator(0);

        imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) { setCurrentIndicator(position); }
        });

        sliderRunnable = () -> {
            int nextItem = (imageSlider.getCurrentItem() + 1) % imageList.size();
            imageSlider.setCurrentItem(nextItem);
        };

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override public void run() { runOnUiThread(() -> { if (sliderRunnable!=null) sliderRunnable.run(); }); }
        }, 3000, 3000);

        Button btnLoginRegister = findViewById(R.id.btnLoginRegister);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLoginRegister.setOnClickListener(v -> {
            btnLoginRegister.setSelected(true);
            btnLogin.setSelected(false);
            startActivity(new Intent(this, LoginChoiceActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            btnLogin.setSelected(true);
            btnLoginRegister.setSelected(false);
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[imageList.size()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(24, 8);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int count = indicatorLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView iv = (ImageView) indicatorLayout.getChildAt(i);
            iv.setImageDrawable(ContextCompat.getDrawable(this, i == index ? R.drawable.indicator_active : R.drawable.indicator_inactive));
        }
    }
}
