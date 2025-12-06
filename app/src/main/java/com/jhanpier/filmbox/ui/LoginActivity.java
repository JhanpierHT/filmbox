package com.jhanpier.filmbox.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.jhanpier.filmbox.R;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;

    private static final int RC_SIGN_IN = 9001;
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        CheckBox rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        Button loginButton = findViewById(R.id.LoginButton);
        LinearLayout googleButton = findViewById(R.id.googleButton);

        loadCredentials(emailInput, passwordInput, rememberMeCheckBox);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            boolean rememberMe = rememberMeCheckBox.isChecked();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            realizarLogin(email, password, rememberMe);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleButton.setOnClickListener(view -> signInWithGoogle());
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
    }

    private void clearCredentials() {
        getSharedPreferences("prefs", MODE_PRIVATE)
                .edit()
                .remove(KEY_EMAIL)
                .remove(KEY_PASSWORD)
                .putBoolean(KEY_REMEMBER, false)
                .apply();
    }

    private void loadCredentials(EditText emailInput, EditText passwordInput, CheckBox rememberMeCheckBox) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean remember = prefs.getBoolean(KEY_REMEMBER, false);

        if (remember) {
            emailInput.setText(prefs.getString(KEY_EMAIL, ""));
            passwordInput.setText(prefs.getString(KEY_PASSWORD, ""));
            rememberMeCheckBox.setChecked(true);
        }
    }

    // ---------------- GOOGLE LOGIN ------------------

    private void signInWithGoogle() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        if (account == null) return;

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Error iniciando con Google", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        // 1. GUARDAR EL EMAIL QUE USARÁ ProfileManager
                        SharedPreferences prefsUser = getSharedPreferences("login_data", MODE_PRIVATE);
                        prefsUser.edit().putString("email", user.getEmail()).apply();
                    }

                    // 🚨 CORRECCIÓN CLAVE: Borrar el ID de perfil seleccionado del usuario anterior.
                    SharedPreferences prefsActual = getSharedPreferences("perfil_actual", MODE_PRIVATE);
                    prefsActual.edit().remove("id").apply();

                    // 🚀 MEJORA DE NAVEGACIÓN: Asegurar que el Login se cierre permanentemente
                    Intent intent = new Intent(this, ProfileSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
    }

    // ---------------- LOGIN NORMAL ------------------

    private void realizarLogin(String email, String password, boolean rememberMe) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (rememberMe) saveCredentials(email, password);
                    else clearCredentials();

                    // 1. GUARDAR EMAIL PARA ProfileManager
                    SharedPreferences prefsUser = getSharedPreferences("login_data", MODE_PRIVATE);
                    prefsUser.edit().putString("email", email).apply();

                    // 🚨 CORRECCIÓN CLAVE: Borrar el ID de perfil seleccionado del usuario anterior.
                    SharedPreferences prefsActual = getSharedPreferences("perfil_actual", MODE_PRIVATE);
                    prefsActual.edit().remove("id").apply();


                    // 🚀 MEJORA DE NAVEGACIÓN: Asegurar que el Login se cierre permanentemente
                    Intent intent = new Intent(this, ProfileSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
    }
}