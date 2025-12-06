package com.jhanpier.filmbox.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.jhanpier.filmbox.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView alreadyHaveAccount;
    // Se añade un CheckBox si quieres simular el 'rememberMe' que estaba en la función original
    // Aunque en un registro no es común, si lo necesitas para algo, añádelo en el layout y aquí.
    // Por ahora, lo dejaré fuera ya que el código de registro no lo usa.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);

        alreadyHaveAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor rellene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Si todas las validaciones pasan, mostramos los términos y condiciones.
            // La lógica de registro se moverá a realizarRegistro.
            mostrarTerminosYCondiciones(email, password);
        });
    }

    // --- NUEVAS FUNCIONES ---

    /**
     * Muestra el diálogo de Términos y Condiciones.
     * Si el usuario acepta, llama a realizarRegistro.
     */
    private void mostrarTerminosYCondiciones(String email, String password) {
        new AlertDialog.Builder(this)
                .setTitle("Términos y Condiciones")
                // Cambia el mensaje para que sea más claro que acepta el registro.
                .setMessage("Al presionar 'Aceptar', confirmas que has leído y aceptas los términos y condiciones de MoviFix para crear tu cuenta.")
                // Si acepta, llama a la función que ahora manejará el registro.
                .setPositiveButton("Aceptar", (dialog, which) -> realizarRegistro(email, password))
                // Si cancela, no hace nada y el diálogo se cierra.
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Realiza la creación de la cuenta con Firebase Auth.
     */
    private void realizarRegistro(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                        // Redirige al usuario
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + (task.getException()!=null?task.getException().getMessage():""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

}