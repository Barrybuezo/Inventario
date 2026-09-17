package com.example.inventarioapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventarioapp.R;
import com.example.inventarioapp.data.repository.AuthRepositoryImpl;
import com.example.inventarioapp.domain.repository.AuthRepository;
import com.example.inventarioapp.domain.usecase.GetSplashDestinationUseCase;
import com.example.inventarioapp.ui.login.LoginActivity;
import com.example.inventarioapp.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long DURACION_SPLASH_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AuthRepository authRepository = new AuthRepositoryImpl(this);
        GetSplashDestinationUseCase getSplashDestinationUseCase =
                new GetSplashDestinationUseCase(authRepository);

        // Handler.postDelayed espera un tiempo antes de ejecutar el código —
        // es lo que le da al splash su breve tiempo visible en pantalla.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            GetSplashDestinationUseCase.Destino destino = getSplashDestinationUseCase.ejecutar();

            Class<?> claseDestino = destino == GetSplashDestinationUseCase.Destino.MAIN
                    ? MainActivity.class
                    : LoginActivity.class;

            startActivity(new Intent(SplashActivity.this, claseDestino));
            finish(); // se quita del historial de pantallas — el usuario no debe poder "regresar" al splash
        }, DURACION_SPLASH_MS);
    }
}