package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jhanpier.filmbox.R;
import com.jhanpier.filmbox.model.Profile;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName;
    private Button btnGuardar, btnCambiarAvatar;
    private ImageView imgAvatar;

    private static final int PICK_AVATAR = 200;

    private boolean isNew = false;
    private int editingProfileId = -1;

    private int selectedAvatarResId = R.drawable.default_avatar; // avatar por defecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtName = findViewById(R.id.edtName);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
        btnCambiarAvatar = findViewById(R.id.btnCambiarAvatar);
        imgAvatar = findViewById(R.id.imgAvatarEdit);

        isNew = getIntent().getBooleanExtra("is_new", false);
        editingProfileId = getIntent().getIntExtra("profile_id", -1);

        if (!isNew && editingProfileId != -1) {
            cargarPerfil();
        }

        btnCambiarAvatar.setOnClickListener(v -> abrirSelectorAvatares());
        btnGuardar.setOnClickListener(v -> guardarPerfil());
    }

    private void cargarPerfil() {
        Profile p = ProfileManager.getProfileById(this, editingProfileId);
        if (p == null) return;

        edtName.setText(p.getName());
        selectedAvatarResId = p.getAvatarResId();

        imgAvatar.setImageResource(selectedAvatarResId);
    }

    private void abrirSelectorAvatares() {
        Intent i = new Intent(this, AvatarSelectorActivity.class);
        startActivityForResult(i, PICK_AVATAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AVATAR && resultCode == RESULT_OK && data != null) {
            selectedAvatarResId = data.getIntExtra("avatarResId", R.drawable.default_avatar);
            imgAvatar.setImageResource(selectedAvatarResId);
        }
    }

    private void guardarPerfil() {
        String name = edtName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNew) {
            Profile nuevo = new Profile();
            nuevo.setName(name);
            nuevo.setAvatarResId(selectedAvatarResId);

            ProfileManager.addProfile(this, nuevo);
        } else {
            Profile p = ProfileManager.getProfileById(this, editingProfileId);
            if (p != null) {
                p.setName(name);
                p.setAvatarResId(selectedAvatarResId);

                ProfileManager.updateProfile(this, p);
            }
        }

        Toast.makeText(this, "Perfil guardado", Toast.LENGTH_SHORT).show();
        finish();
    }
}
