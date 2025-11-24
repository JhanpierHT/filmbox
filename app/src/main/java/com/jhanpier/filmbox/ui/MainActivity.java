package com.jhanpier.filmbox.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.jhanpier.filmbox.R;
import androidx.activity.ComponentActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enableEdgeToEdge(); // si usas AndroidX 1.6+, puedes mantener si tienes util
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {

            androidx.core.graphics.Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);

            return insets;
        });


    }
}
