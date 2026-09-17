package com.example.inventarioapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionPreferences {

    private static final String PREFS_NOMBRE = "SesionPrefs";
    private static final String CLAVE_SESION_ACTIVA = "sesion_activa";

    public static boolean haySesionActiva(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        return prefs.getBoolean(CLAVE_SESION_ACTIVA, false);
    }

    public static void guardarSesionActiva(Context context, boolean activa) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(CLAVE_SESION_ACTIVA, activa).apply();
    }
}