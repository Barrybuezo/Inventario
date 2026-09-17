package com.example.inventarioapp.domain.usecase;

import com.example.inventarioapp.domain.repository.AuthRepository;

public class GetSplashDestinationUseCase {

    // Un enum es más seguro que usar Strings sueltos ("main"/"login") para
    // representar un conjunto fijo y pequeño de posibles destinos.
    public enum Destino {
        MAIN, LOGIN
    }

    private final AuthRepository authRepository;

    public GetSplashDestinationUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Destino ejecutar() {
        return authRepository.haySesionActiva() ? Destino.MAIN : Destino.LOGIN;
    }
}