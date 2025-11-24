package com.jhanpier.filmbox.ui;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
        float scale = 1 - Math.abs(position);
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setAlpha(0.3f + (1 - Math.abs(position)));
    }
}
