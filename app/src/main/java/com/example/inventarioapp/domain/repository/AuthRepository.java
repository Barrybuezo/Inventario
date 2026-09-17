package com.example.inventarioapp.domain.repository;

import com.example.inventarioapp.domain.model.Usuario;

public interface AuthRepository {
    boolean existeAlgunUsuario();
    boolean existeUsername(String username);
    long registrarUsuario(Usuario usuario);
    Usuario obtenerPorUsername(String username);

    boolean haySesionActiva();
    void guardarSesionActiva(boolean activa);
}