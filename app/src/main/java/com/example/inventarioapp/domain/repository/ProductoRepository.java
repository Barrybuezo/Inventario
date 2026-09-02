package com.example.inventarioapp.domain.repository;

import com.example.inventarioapp.domain.model.Producto;

import java.util.List;

public interface ProductoRepository {
    void guardar(List<Producto> productos);
    List<Producto> cargar();
}