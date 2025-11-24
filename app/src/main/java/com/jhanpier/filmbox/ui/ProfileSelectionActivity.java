package com.jhanpier.filmbox.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jhanpier.filmbox.R;

public class ProfileSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enableEdgeToEdge(); // si usas esa util
        setContentView(R.layout.activity_profile_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {

            androidx.core.graphics.Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);

            return insets;
        });


        ImageView profile1Image = findViewById(R.id.profile1Image);
        LinearLayout addProfile = findViewById(R.id.addProfile);
        Button doneButton = findViewById(R.id.doneButton);
        Button skipButton = findViewById(R.id.skipButton);

        profile1Image.setOnClickListener(v -> Toast.makeText(this, getString(R.string.profile_1) + " selected", Toast.LENGTH_SHORT).show());
        addProfile.setOnClickListener(v -> Toast.makeText(this, getString(R.string.add_profile), Toast.LENGTH_SHORT).show());
        doneButton.setOnClickListener(v -> Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show());
        skipButton.setOnClickListener(v -> Toast.makeText(this, getString(R.string.skip), Toast.LENGTH_SHORT).show());
    }
}
