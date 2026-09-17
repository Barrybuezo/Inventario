package com.example.inventarioapp.domain.model;

import java.io.Serializable;

public class Producto implements Serializable {
    public long id;
    public String nombre;
    public double precio;
    public int cantidad;
    public String fotoUri;
    public String categoria;
    public String descripcion;

    // Constructor para un producto NUEVO que todavía no existe en la base de datos.
    // El id se pone en -1 como "todavía no tiene id real" — SQLite le asignará uno al insertarlo.
    public Producto(String nombre, double precio, int cantidad, String fotoUri,
                    String categoria, String descripcion) {
        this(-1L, nombre, precio, cantidad, fotoUri, categoria, descripcion);
    }

    // Constructor completo, usado para reconstruir un producto QUE YA EXISTE en la base
    // de datos (trae su id real, leído de una fila de SQLite).
    public Producto(long id, String nombre, double precio, int cantidad, String fotoUri,
                    String categoria, String descripcion) {
        this.id = id;
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