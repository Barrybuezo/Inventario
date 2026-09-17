package com.example.inventarioapp.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.inventarioapp.data.local.AppDbHelper;
import com.example.inventarioapp.data.local.SesionPreferences;
import com.example.inventarioapp.domain.model.Usuario;
import com.example.inventarioapp.domain.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {

    private final AppDbHelper dbHelper;
    private final Context context;

    public AuthRepositoryImpl(Context context) {
        this.context = context;
        this.dbHelper = new AppDbHelper(context);
    }

    @Override
    public boolean existeAlgunUsuario() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(AppDbHelper.TABLE_USUARIOS, null, null, null, null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    @Override
    public boolean existeUsername(String username) {
        return obtenerPorUsername(username) != null;
    }

    @Override
    public long registrarUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(AppDbHelper.COLUMN_USERNAME, usuario.username);
        valores.put(AppDbHelper.COLUMN_PASSWORD_HASH, usuario.passwordHash);

        // insert() regresa -1 si algo falla (por ejemplo, username duplicado por el UNIQUE).
        long id = db.insert(AppDbHelper.TABLE_USUARIOS, null, valores);
        db.close();
        return id;
    }

    @Override
    public Usuario obtenerPorUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(AppDbHelper.TABLE_USUARIOS, null,
                AppDbHelper.COLUMN_USERNAME + " = ?", new String[]{username},
                null, null, null);

        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_USUARIO_ID));
            String user = cursor.getString(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_USERNAME));
            String hash = cursor.getString(cursor.getColumnIndexOrThrow(AppDbHelper.COLUMN_PASSWORD_HASH));
            usuario = new Usuario(id, user, hash);
        }

        cursor.close();
        db.close();
        return usuario;
    }

    @Override
    public boolean haySesionActiva() {
        return SesionPreferences.haySesionActiva(context);
    }

    @Override
    public void guardarSesionActiva(boolean activa) {
        SesionPreferences.guardarSesionActiva(context, activa);
    }
}