package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.model.Profile;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtNombrePerfil, txtCorreoPerfil;
    private ImageView imgAvatar;
    private Button btnEditarPerfil, btnCerrarSesion, btnEliminarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupBottomNavigation(); // 👈 Llamamos al método corregido aquí.

        // --- Elementos UI ---
        txtNombrePerfil = findViewById(R.id.txtNombrePerfil);
        txtCorreoPerfil = findViewById(R.id.txtCorreoPerfil);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEliminarPerfil = findViewById(R.id.btnEliminarPerfil);

        cargarPerfil();

        btnEditarPerfil.setOnClickListener(v -> {

            SharedPreferences prefsActual = getSharedPreferences("perfil_actual", MODE_PRIVATE);
            int idPerfil = prefsActual.getInt("id", -1);

            if (idPerfil == -1) return;

            Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
            i.putExtra("is_new", false);
            i.putExtra("profile_id", idPerfil);
            startActivity(i);
        });


        btnCerrarSesion.setOnClickListener(v -> {
            Intent logout = new Intent(ProfileActivity.this, LoginActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
        });

        btnEliminarPerfil.setOnClickListener(v -> confirmarEliminacion());
    }

    /**
     * 🛠️ MÉTODO DE NAVEGACIÓN CORREGIDO
     * Utiliza CLEAR_TOP y NEW_TASK para evitar la acumulación de instancias
     * en la pila al cambiar entre las pestañas principales.
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // ... (Selección del ítem actual)
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (id == R.id.nav_video) {
                intent = new Intent(this, VideosActivity.class);
            } else if (id == R.id.nav_favorite) {
                intent = new Intent(this, FavoritesActivity.class);
            } else {
                return id == R.id.nav_profile; // Ya estás aquí
            }

            if (intent != null) {
                // ✅ MANTENEMOS: CLEAR_TOP y NEW_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // ❌ ELIMINAR ESTA LÍNEA en todas las pestañas:
                // finish();

                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
    }

    private void cargarPerfil() {

        // 1. Obtener correo del login
        //SharedPreferences prefsLogin = getSharedPreferences("login_data", MODE_PRIVATE);
        SharedPreferences prefsLogin = getSharedPreferences("login_data", MODE_PRIVATE);
        String emailLogin = prefsLogin.getString("email", "unknown");


        // 2. Cargar ID del perfil actual
        SharedPreferences prefsActual = getSharedPreferences("perfil_actual", MODE_PRIVATE);
        int idPerfil = prefsActual.getInt("id", -1);

        if (idPerfil == -1) {
            txtNombrePerfil.setText("Sin perfil");
            txtCorreoPerfil.setText(emailLogin);
            imgAvatar.setImageResource(R.drawable.default_avatar);
            return;
        }

        // NOTA: ProfileManager debe ser accesible o ser una clase estática para que esto funcione.
        Profile perfil = ProfileManager.getProfileById(this, idPerfil);

        if (perfil == null) {
            txtNombrePerfil.setText("Perfil desconocido");
            txtCorreoPerfil.setText(emailLogin);
            imgAvatar.setImageResource(R.drawable.default_avatar);
            return;
        }

        // 3. Mostrar datos reales del perfil
        txtNombrePerfil.setText(perfil.getName());
        txtCorreoPerfil.setText(emailLogin);

        if (perfil.getAvatarResId() != 0) {
            imgAvatar.setImageResource(perfil.getAvatarResId());
        } else {
            imgAvatar.setImageResource(R.drawable.default_avatar);
        }
    }


    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar perfil")
                .setMessage("¿Seguro que deseas eliminar este perfil?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarPerfil())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarPerfil() {

        SharedPreferences prefsActual = getSharedPreferences("perfil_actual", MODE_PRIVATE);
        int idPerfil = prefsActual.getInt("id", -1);

        if (idPerfil == -1) return;

        // 1. Eliminar perfil de ProfileManager
        // NOTA: ProfileManager debe ser accesible o ser una clase estática para que esto funcione.
        ProfileManager.deleteProfile(this, idPerfil);

        // 2. Borrar selección actual
        prefsActual.edit().clear().apply();

        // 3. Comprobar si quedan perfiles
        if (ProfileManager.getProfiles(this).size() > 0) {
            int nuevoId = ProfileManager.getProfiles(this).get(0).getId();
            prefsActual.edit().putInt("id", nuevoId).apply();
        } else {
            // No quedan perfiles → volver al selector o login
            Intent i = new Intent(this, ProfileSelectionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }

        cargarPerfil();
    }
}