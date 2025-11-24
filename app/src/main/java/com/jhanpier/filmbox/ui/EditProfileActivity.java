package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jhanpier.filmbox.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtEmail;
    private Button btnGuardar, btnCambiarFoto;
    private ImageView imgAvatar;
    private String avatarUri = "";
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        imgAvatar = findViewById(R.id.imgAvatarEdit);

        // Cargar datos guardados
        SharedPreferences prefs = getSharedPreferences("perfil", MODE_PRIVATE);
        edtName.setText(prefs.getString("nombre", ""));
        edtEmail.setText(prefs.getString("correo", ""));
        avatarUri = prefs.getString("avatar", "");

        // Mostrar avatar si aún tienes permisos
        if (!avatarUri.isEmpty()) {
            Uri savedUri = Uri.parse(avatarUri);

            if (tienePermisoUri(savedUri)) {
                imgAvatar.setImageURI(savedUri);
            } else {
                // Si ya no tienes permiso, no cargamos la imagen
                avatarUri = "";
            }
        }

        // Abrir selector de fotos
        btnCambiarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Flags válidos
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            startActivityForResult(intent, PICK_IMAGE);
        });

        btnGuardar.setOnClickListener(v -> guardarPerfil());
    }

    private boolean tienePermisoUri(Uri uri) {
        try {
            getContentResolver().openInputStream(uri).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void guardarPerfil() {
        SharedPreferences prefs = getSharedPreferences("perfil", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("nombre", edtName.getText().toString());
        editor.putString("correo", edtEmail.getText().toString());
        editor.putString("avatar", avatarUri);

        editor.apply();

        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            if (data != null && data.getData() != null) {

                Uri uri = data.getData();
                avatarUri = uri.toString();

                // Guardar permisos persistentes
                try {
                    final int flags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    getContentResolver().takePersistableUriPermission(
                            uri,
                            flags
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imgAvatar.setImageURI(uri);
            }
        }
    }
}
