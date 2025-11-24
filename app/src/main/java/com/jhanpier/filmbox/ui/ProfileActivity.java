package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jhanpier.filmbox.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtNombrePerfil, txtCorreoPerfil;
    private ImageView imgAvatar;
    private Button btnEditarPerfil, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // --- Bottom Navigation ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_video) {
                startActivity(new Intent(this, VideosActivity.class));
                return true;
            } else if (id == R.id.nav_favorite) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else return id == R.id.nav_profile;
        });

        // --- Elementos del perfil ---
        txtNombrePerfil = findViewById(R.id.txtNombrePerfil);
        txtCorreoPerfil = findViewById(R.id.txtCorreoPerfil);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        cargarPerfil();

        btnEditarPerfil.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
        );

        btnCerrarSesion.setOnClickListener(v -> {
            Intent logout = new Intent(ProfileActivity.this, LoginActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
    }

    private void cargarPerfil() {
        SharedPreferences prefs = getSharedPreferences("perfil", MODE_PRIVATE);

        String nombre = prefs.getString("nombre", "Mi Perfil");
        String correo = prefs.getString("correo", "correo@ejemplo.com");
        String avatarUri = prefs.getString("avatar", "");

        txtNombrePerfil.setText(nombre);
        txtCorreoPerfil.setText(correo);

        if (avatarUri.isEmpty()) {
            imgAvatar.setImageResource(R.drawable.default_avatar);

        } else {
            try {
                Uri uri = Uri.parse(avatarUri);
                imgAvatar.setImageURI(uri);

            } catch (Exception e) {
                // Si hay error → coloca imagen por defecto para evitar cierre
                imgAvatar.setImageResource(R.drawable.default_avatar);
            }
        }
    }
}
