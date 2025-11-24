package com.jhanpier.filmbox.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.jhanpier.filmbox.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView logo = findViewById(R.id.logoImage);
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(logo, "rotation", 0f, 1800f);
        rotateAnim.setDuration(5000);
        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                finish();
            }
        });
        rotateAnim.start();
    }
}
