package com.example.inventarioapp.domain.repository;

import com.example.inventarioapp.domain.model.Producto;
import java.util.List;

// El contrato ahora refleja un CRUD real: Create, Read, Update, Delete,
// cada uno como su propia operación — no "guardar toda la lista de golpe".
public interface ProductoRepository {
    long insertar(Producto producto);       // Create — regresa el id que le asignó la BD
    List<Producto> obtenerTodos();          // Read
    boolean actualizar(Producto producto);  // Update — usa producto.id para saber cuál fila tocar
    boolean eliminar(long id);              // Delete
}