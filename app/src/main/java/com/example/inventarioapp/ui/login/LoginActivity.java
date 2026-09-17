package com.example.inventarioapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventarioapp.R;
import com.example.inventarioapp.data.repository.AuthRepositoryImpl;
import com.example.inventarioapp.domain.repository.AuthRepository;
import com.example.inventarioapp.domain.usecase.LoginUseCase;
import com.example.inventarioapp.ui.main.MainActivity;
import com.example.inventarioapp.ui.registrousuario.RegistroUsuarioActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etUsername, etPassword;
    TextInputLayout tilUsername, tilPassword;
    LoginUseCase loginUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AuthRepository authRepository = new AuthRepositoryImpl(this);
        loginUseCase = new LoginUseCase(authRepository);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnLogin.setOnClickListener(v -> intentarLogin());

        tvIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroUsuarioActivity.class)));
    }

    void intentarLogin() {
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (username.isEmpty()) {
            tilUsername.setError("Ingresa tu usuario");
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Ingresa tu contraseña");
            return;
        }

        boolean exito = loginUseCase.ejecutar(username, password);

        if (exito) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // así el botón "atrás" desde Main no regresa al login
        } else {
            Snackbar.make(etUsername, "Usuario o contraseña incorrectos", Snackbar.LENGTH_SHORT).show();
        }
    }
}