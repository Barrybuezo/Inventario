package com.example.inventarioapp.domain.usecase;

import com.example.inventarioapp.domain.repository.AuthRepository;

public class CerrarSesionUseCase {

    private final AuthRepository authRepository;

    public CerrarSesionUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void ejecutar() {
        authRepository.guardarSesionActiva(false);
    }
}