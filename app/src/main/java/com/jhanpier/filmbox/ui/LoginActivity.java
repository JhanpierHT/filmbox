package com.jhanpier.filmbox.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
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
    private static final String PREFS_NAME = "prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        final EditText emailInput = findViewById(R.id.emailInput);
        final EditText passwordInput = findViewById(R.id.passwordInput);
        final CheckBox rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        Button loginButton = findViewById(R.id.LoginButton);
        LinearLayout googleButton = findViewById(R.id.googleButton);
        LinearLayout facebookButton = findViewById(R.id.facebookButton);

        loadCredentials(emailInput, passwordInput, rememberMeCheckBox);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            boolean rememberMe = rememberMeCheckBox.isChecked();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarTerminosYCondiciones(email, password, rememberMe);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleButton.setOnClickListener(view -> signInWithGoogle());

        facebookButton.setOnClickListener(view ->
                Toast.makeText(this, "Facebook login aún no implementado", Toast.LENGTH_SHORT).show());
    }

    private void saveCredentials(String email, String password) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
    }

    private void clearCredentials() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_EMAIL)
                .remove(KEY_PASSWORD)
                .putBoolean(KEY_REMEMBER, false)
                .apply();
    }

    private void loadCredentials(EditText emailInput, EditText passwordInput, CheckBox rememberMeCheckBox) {
        boolean remember = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_REMEMBER, false);
        if (remember) {
            String savedEmail = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_EMAIL, "");
            String savedPassword = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_PASSWORD, "");
            emailInput.setText(savedEmail);
            passwordInput.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google sign in successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, ProfileSelectionActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarTerminosYCondiciones(String email, String password, boolean rememberMe) {
        new AlertDialog.Builder(this)
                .setTitle("Términos y Condiciones")
                .setMessage("Al continuar, aceptas los siguientes términos de uso de MoviFix:\n\n" +
                        "• Solo usarás la aplicación MoviFix con fines personales, educativos o de entretenimiento...\n\n" +
                        "• ...")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        realizarLogin(email, password, rememberMe);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void realizarLogin(String email, String password, boolean rememberMe) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (rememberMe) saveCredentials(email, password); else clearCredentials();
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + (task.getException() != null ? task.getException().getMessage() : "unknown"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
