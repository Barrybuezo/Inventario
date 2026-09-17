package com.example.inventarioapp.domain.usecase;

import com.example.inventarioapp.domain.model.Usuario;
import com.example.inventarioapp.domain.repository.AuthRepository;
import com.example.inventarioapp.util.PasswordHasher;

public class LoginUseCase {

    private final AuthRepository authRepository;

    public LoginUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // Regresa true si las credenciales son correctas (y ya deja la sesión activa).
    public boolean ejecutar(String username, String password) {
        Usuario usuario = authRepository.obtenerPorUsername(username.trim());
        if (usuario == null) return false;

        String hashIngresado = PasswordHasher.hashear(password);
        boolean coincide = usuario.passwordHash.equals(hashIngresado);

        if (coincide) {
            authRepository.guardarSesionActiva(true);
        }
        return coincide;
    }
}