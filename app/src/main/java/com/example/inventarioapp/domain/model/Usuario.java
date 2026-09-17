package com.example.inventarioapp.domain.model;

public class Usuario {
    public long id;
    public String username;
    public String passwordHash;

    public Usuario(String username, String passwordHash) {
        this.id = -1L;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Usuario(long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }
}