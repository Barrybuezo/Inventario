package com.example.inventarioapp.domain.usecase;

import com.example.inventarioapp.domain.model.Usuario;
import com.example.inventarioapp.domain.repository.AuthRepository;
import com.example.inventarioapp.util.PasswordHasher;

public class RegistrarUsuarioUseCase {

    private final AuthRepository authRepository;

    public RegistrarUsuarioUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // Regresa un mensaje de error, o null si el registro fue exitoso.
    public String ejecutar(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "Ingresa un nombre de usuario";
        }
        if (password == null || password.length() < 4) {
            return "La contraseña debe tener al menos 4 caracteres";
        }
        if (authRepository.existeUsername(username.trim())) {
            return "Ese nombre de usuario ya existe";
        }

        String hash = PasswordHasher.hashear(password);
        Usuario nuevo = new Usuario(username.trim(), hash);
        long id = authRepository.registrarUsuario(nuevo);

        return id != -1 ? null : "No se pudo crear el usuario";
    }
}