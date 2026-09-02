package com.example.inventarioapp.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.inventarioapp.domain.model.Producto;
import com.example.inventarioapp.domain.repository.ProductoRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {

    private static final String PREFS_NOMBRE = "InventarioPrefs";
    private static final String CLAVE_PRODUCTOS = "productos_json";

    private final Context context;

    public ProductoRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public void guardar(List<Producto> productos) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(productos);
        prefs.edit().putString(CLAVE_PRODUCTOS, json).apply();
    }

    @Override
    public List<Producto> cargar() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(CLAVE_PRODUCTOS, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Type tipoLista = new TypeToken<ArrayList<Producto>>() {
        }.getType();
        List<Producto> productos = gson.fromJson(json, tipoLista);

        return productos != null ? productos : new ArrayList<>();
    }
}