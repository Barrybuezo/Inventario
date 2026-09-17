package com.example.inventarioapp.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.inventarioapp.data.local.AppDbHelper;
import com.example.inventarioapp.domain.model.Producto;
import com.example.inventarioapp.domain.repository.ProductoRepository;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {

    private final AppDbHelper dbHelper;

    public ProductoRepositoryImpl(Context context) {
        this.dbHelper = new AppDbHelper(context);
    }

    @Override
    public long insertar(Producto producto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = construirValores(producto);

        long idGenerado = db.insert(AppDbHelper.TABLE_PRODUCTOS, null, valores);
        db.close();
        return idGenerado;
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // query(tabla, columnas, where, argsWhere, groupBy, having, orderBy)
        // null en columnas = "trae todas las columnas". Ordenamos por nombre.
        Cursor cursor = db.query(AppDbHelper.TABLE_PRODUCTOS, null,
                null, null, null, null, AppDbHelper.COLUMN_NOMBRE + " ASC");

        while (cursor.moveToNext()) {
            lista.add(construirProductoDesdeCursor(cursor));
        }

        cursor.close();
        db.close();
        return lista;
    }

    @Override
    public boolean actualizar(Producto producto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = construirValores(producto);

        int filasAfectadas = db.update(
                AppDbHelper.TABLE_PRODUCTOS,
                valores,
                AppDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(producto.id)});

        db.close();
        return filasAfectadas > 0;
    }

    @Override
    public boolean eliminar(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int filasAfectadas = db.delete(
                AppDbHelper.TABLE_PRODUCTOS,
                AppDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return filasAfectadas > 0;
    }

    // Convierte un objeto Producto en el formato "columna -> valor" que SQLite espera.
    private ContentValues construirValores(Producto producto) {
        ContentValues valores = new ContentValues();
        valores.put(AppDbHelper.COLUMN_NOMBRE, producto.nombre);
        valores.put(AppDbHelper.COLUMN_PRECIO, producto.precio);
        valores.put(AppDbHelper.COLUMN_CANTIDAD, producto.cantidad);
        valores.put(AppDbHelper.COLUMN_FOTO_URI, producto.fotoUri);
        valores.put(AppDbHelper.COLUMN_CATEGORIA, producto.categoria);
        valores.put(AppDbHelper.COLUMN_DESCRIPCION, producto.descripcion);
        return valores;
    }

    // Lee una fila actual del cursor y la convierte de vuelta a un objeto Producto.
    private Producto construirProductoDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_NOMBRE));
        double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_PRECIO));
        int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_CANTIDAD));
        String fotoUri = cursor.getString(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_FOTO_URI));
        String categoria = cursor.getString(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_CATEGORIA));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_DESCRIPCION));

        return new Producto(id, nombre, precio, cantidad, fotoUri, categoria, descripcion);
    }
}