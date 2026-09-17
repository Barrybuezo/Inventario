package com.example.inventarioapp.ui.registrousuario;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventarioapp.R;
import com.example.inventarioapp.data.repository.AuthRepositoryImpl;
import com.example.inventarioapp.domain.repository.AuthRepository;
import com.example.inventarioapp.domain.usecase.RegistrarUsuarioUseCase;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistroUsuarioActivity extends AppCompatActivity {

    TextInputEditText etUsername, etPassword, etConfirmarPassword;
    TextInputLayout tilUsername, tilPassword, tilConfirmarPassword;
    RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        AuthRepository authRepository = new AuthRepositoryImpl(this);
        registrarUsuarioUseCase = new RegistrarUsuarioUseCase(authRepository);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmarPassword = findViewById(R.id.tilConfirmarPassword);
        MaterialButton btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> intentarRegistro());
    }

    void intentarRegistro() {
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmarPassword.setError(null);

        String username = etUsername.getText() != null ? etUsername.getText().toString() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmar = etConfirmarPassword.getText() != null ? etConfirmarPassword.getText().toString() : "";

        if (!password.equals(confirmar)) {
            tilConfirmarPassword.setError("Las contraseñas no coinciden");
            return;
        }

        String error = registrarUsuarioUseCase.ejecutar(username, password);

        if (error == null) {
            Snackbar.make(etUsername, "Cuenta creada. Ahora inicia sesión", Snackbar.LENGTH_LONG).show();
            finish(); // regresa a LoginActivity
        } else {
            Snackbar.make(etUsername, error, Snackbar.LENGTH_SHORT).show();
        }
    }
}