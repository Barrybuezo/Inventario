package com.example.inventarioapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Clase encargada únicamente de guardar y leer la lista de productos
// convertida a JSON, usando SharedPreferences como "cajita" de almacenamiento.
public class AlmacenProductos {

    private static final String PREFS_NOMBRE = "InventarioPrefs";
    private static final String CLAVE_PRODUCTOS = "productos_json";

    // Convierte la lista de productos a texto JSON y la guarda.
    public static void guardar(Context context, List<MainActivity.Producto> productos) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(productos);
        prefs.edit().putString(CLAVE_PRODUCTOS, json).apply();
    }

    // Lee el texto JSON guardado y lo reconstruye como lista de productos.
    // Si nunca se ha guardado nada (primera vez que se abre la app), regresa una lista vacía.
    public static List<MainActivity.Producto> cargar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(CLAVE_PRODUCTOS, null);

        if (json == null) {
            return new ArrayList<>();
        }

        // TypeToken le dice a Gson exactamente qué tipo de lista reconstruir
        // (List<Producto> en vez de un Object genérico).
        Type tipoLista = new TypeToken<ArrayList<MainActivity.Producto>>() {}.getType();
        List<MainActivity.Producto> productos = gson.fromJson(json, tipoLista);

        return productos != null ? productos : new ArrayList<>();
    }
}