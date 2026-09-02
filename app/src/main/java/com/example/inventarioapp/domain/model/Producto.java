package com.example.inventarioapp.domain.model;

import java.io.Serializable;

public class Producto implements Serializable {
    public String nombre;
    public double precio;
    public int cantidad;
    public String fotoUri;
    public String categoria;
    public String descripcion;

    public Producto(String nombre, double precio, int cantidad, String fotoUri,
                    String categoria, String descripcion) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.fotoUri = fotoUri;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public double getTotal() {
        return precio * cantidad;
    }
}