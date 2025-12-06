package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.content.SharedPreferences; // Importación necesaria
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.model.Profile;

import java.util.List;

public class ProfileSelectionActivity extends AppCompatActivity {

    private LinearLayout containerProfiles;
    private int selectedProfileId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_selection);

        containerProfiles = findViewById(R.id.containerProfiles);
        LinearLayout addProfile = findViewById(R.id.addProfile);
        Button doneButton = findViewById(R.id.doneButton);

        LinearLayout editProfilesContainer = findViewById(R.id.editProfilesContainer);
        editProfilesContainer.setOnClickListener(v -> abrirModoEdicion());

        // 🔥 CORRECCIÓN: Cargar el perfil seleccionado directamente de las SharedPreferences
        SharedPreferences prefsActual = getSharedPreferences("perfil_actual", MODE_PRIVATE);
        selectedProfileId = prefsActual.getInt("id", -1); // Lee de "perfil_actual"

        loadProfiles();

        addProfile.setOnClickListener(v -> {
            Intent i = new Intent(this, EditProfileActivity.class);
            i.putExtra("is_new", true);
            startActivity(i);
        });

        doneButton.setOnClickListener(v -> {
            if (selectedProfileId == -1) {
                Toast.makeText(this, "Selecciona un perfil", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 CORRECCIÓN: Guardar el perfil seleccionado directamente en "perfil_actual"
            prefsActual.edit().putInt("id", selectedProfileId).apply();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void abrirModoEdicion() {
        List<Profile> profiles = ProfileManager.getProfiles(this);

        if (profiles.isEmpty()) {
            Toast.makeText(this, "No hay perfiles para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nombres = new String[profiles.size()];
        for (int i = 0; i < profiles.size(); i++) {
            nombres[i] = profiles.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecciona un perfil para editar")
                .setItems(nombres, (dialog, which) -> {
                    Profile p = profiles.get(which);

                    Intent i = new Intent(this, EditProfileActivity.class);
                    i.putExtra("profile_id", p.getId());
                    startActivity(i);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    private void loadProfiles() {
        containerProfiles.removeAllViews();

        List<Profile> profiles = ProfileManager.getProfiles(this);

        if (profiles.isEmpty()) {
            Toast.makeText(this, "No hay perfiles, crea uno nuevo", Toast.LENGTH_SHORT).show();
        }

        for (Profile p : profiles) {
            View item = getLayoutInflater().inflate(R.layout.item_profile, containerProfiles, false);

            ImageView img = item.findViewById(R.id.imgAvatarItem);
            TextView txt = item.findViewById(R.id.txtNameItem);

            img.setImageResource(p.getAvatarResId());
            txt.setText(p.getName());

            item.setOnClickListener(v -> {
                selectedProfileId = p.getId();
                highlightSelected(item);
            });

            item.setOnLongClickListener(v -> {
                Intent i = new Intent(this, EditProfileActivity.class);
                i.putExtra("profile_id", p.getId());
                startActivity(i);
                return true;
            });

            containerProfiles.addView(item);

            if (p.getId() == selectedProfileId) {
                highlightSelected(item);
            }
        }
    }

    private void highlightSelected(View selected) {
        for (int i = 0; i < containerProfiles.getChildCount(); i++) {
            View child = containerProfiles.getChildAt(i);
            child.setBackgroundResource(R.drawable.bg_profile_unselected);
        }

        selected.setBackgroundResource(R.drawable.bg_profile_selected);
    }
}