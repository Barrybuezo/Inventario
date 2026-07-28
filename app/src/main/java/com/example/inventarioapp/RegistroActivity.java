package com.example.inventarioapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    EditText etNombre, etCantidad, etPrecio;
    ImageView ivFoto;
    TextView tvEstadoFoto;
    Uri fotoUri;

    ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            exito -> {
                if (exito) {
                    ivFoto.setVisibility(android.view.View.VISIBLE);
                    ivFoto.setImageURI(fotoUri);
                    tvEstadoFoto.setText("Foto tomada");
                } else {
                    Toast.makeText(this, "No se tomó la foto", Toast.LENGTH_SHORT).show();
                }
            });

    // Launcher nuevo: pide el permiso de cámara al usuario.
    // Cuando el usuario responde (aceptar o negar), se ejecuta este código.
    ActivityResultLauncher<String> permisoLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            concedido -> {
                if (concedido) {
                    lanzarCamara();
                } else {
                    Toast.makeText(this, "Se necesita el permiso de cámara para tomar la foto", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCantidad = findViewById(R.id.etCantidad);
        etPrecio = findViewById(R.id.etPrecio);
        ivFoto = findViewById(R.id.ivFoto);
        tvEstadoFoto = findViewById(R.id.tvEstadoFoto);

        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        btnTomarFoto.setOnClickListener(v -> abrirCamara());

        btnGuardar.setOnClickListener(v -> guardarProducto());

        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    void abrirCamara() {
        // Primero revisamos si el permiso ya fue concedido antes.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            lanzarCamara();
        } else {
            // Si no, se lo pedimos al usuario. El resultado lo maneja permisoLauncher de arriba.
            permisoLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    void lanzarCamara() {
        try {
            String nombreArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            File carpeta = getExternalFilesDir("Pictures");
            File archivo = File.createTempFile(nombreArchivo, ".jpg", carpeta);

            fotoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.inventarioapp.fileprovider",
                    archivo);

            cameraLauncher.launch(fotoUri);

        } catch (IOException e) {
            Toast.makeText(this, "Error al crear el archivo de la foto", Toast.LENGTH_SHORT).show();
        }
    }

    void guardarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String cantidadTexto = etCantidad.getText().toString().trim();
        String precioTexto = etPrecio.getText().toString().trim();

        if (nombre.isEmpty() || cantidadTexto.isEmpty() || precioTexto.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadTexto);
        double precio = Double.parseDouble(precioTexto);

        Intent resultado = new Intent();
        resultado.putExtra("nombre", nombre);
        resultado.putExtra("cantidad", cantidad);
        resultado.putExtra("precio", precio);

        setResult(RESULT_OK, resultado);
        finish();
    }
}