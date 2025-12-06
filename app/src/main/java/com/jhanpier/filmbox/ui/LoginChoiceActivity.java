package com.jhanpier.filmbox.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jhanpier.filmbox.R;

public class LoginChoiceActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 9001;
    private static final String PREFS_NAME = "perfil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choice);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        LinearLayout googleButton = findViewById(R.id.googleButton);
        LinearLayout facebookButton = findViewById(R.id.facebookButton);
        Button signInButton = findViewById(R.id.signInButton);
        TextView signupText = findViewById(R.id.signupText);

        googleButton.setOnClickListener(v -> signInWithGoogle());
        facebookButton.setOnClickListener(v -> { /* implementar si necesitas */});
        signInButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        signupText.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void signInWithGoogle() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Toast.makeText(this, "Google sign in failed: account null", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // No hacemos signInWithCredential aquí porque sólo queremos usar FirebaseAuth como referencia:
        // si ya quieres autenticar en Firebase, mantén la lógica de credenciales.
        com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Guardar datos básicos en SharedPreferences para precargar EditProfileActivity
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            prefs.edit()
                                    .putString("nombre", user.getDisplayName() != null ? user.getDisplayName() : "")
                                    .putString("correo", user.getEmail() != null ? user.getEmail() : "")
                                    .putString("avatar", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "")
                                    .apply();
                        }

                        // Abrir EditProfileActivity para que el usuario complete/edite su perfil
                        startActivity(new Intent(this, ProfileSelectionActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
