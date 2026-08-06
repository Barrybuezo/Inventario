package com.example.inventarioapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    TextInputEditText etNombre, etCantidad, etPrecio;
    TextInputLayout tilNombre, tilCantidad, tilPrecio;
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

        tilNombre = findViewById(R.id.tilNombre);
        tilCantidad = findViewById(R.id.tilCantidad);
        tilPrecio = findViewById(R.id.tilPrecio);

        ivFoto = findViewById(R.id.ivFoto);
        tvEstadoFoto = findViewById(R.id.tvEstadoFoto);

        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);


        //
        TextView tvTituloFormulario = findViewById(R.id.tvTituloFormulario);

        MainActivity.Producto productoExistente;
        if (Build.VERSION.SDK_INT >= 33) {
            productoExistente = getIntent().getSerializableExtra("producto", MainActivity.Producto.class);
        } else {
            productoExistente = (MainActivity.Producto) getIntent().getSerializableExtra("producto");
        }

        if (productoExistente != null) {
            tvTituloFormulario.setText("Editar producto");
            btnGuardar.setText("Actualizar producto");

            etNombre.setText(productoExistente.nombre);
            etCantidad.setText(String.valueOf(productoExistente.cantidad));
            etPrecio.setText(String.valueOf(productoExistente.precio));

            if (productoExistente.fotoUri != null) {
                fotoUri = Uri.parse(productoExistente.fotoUri);
                ivFoto.setVisibility(android.view.View.VISIBLE);
                ivFoto.setImageURI(fotoUri);
                tvEstadoFoto.setText("Foto actual");
            }
        }


        btnTomarFoto.setOnClickListener(v -> abrirCamara());

        btnGuardar.setOnClickListener(v -> guardarProducto());

        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    void abrirCamara() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            lanzarCamara();
        } else {
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

    // Revisa los 3 campos y regresa true solo si TODOS están correctos.
    // Si algo falla, marca el error en rojo bajo el campo correspondiente
    // y NO se detiene en el primer error: revisa los 3 para mostrarlos todos de una vez.
    boolean validarCampos() {
        boolean esValido = true;

        tilNombre.setError(null);
        tilCantidad.setError(null);
        tilPrecio.setError(null);

        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String cantidadTexto = etCantidad.getText() != null ? etCantidad.getText().toString().trim() : "";
        String precioTexto = etPrecio.getText() != null ? etPrecio.getText().toString().trim() : "";

        if (nombre.isEmpty()) {
            tilNombre.setError("Ingresa el nombre del producto");
            esValido = false;
        }

        if (cantidadTexto.isEmpty()) {
            tilCantidad.setError("Ingresa la cantidad");
            esValido = false;
        } else if (Integer.parseInt(cantidadTexto) < 0) {
            tilCantidad.setError("La cantidad no puede ser negativa");
            esValido = false;
        }

        if (precioTexto.isEmpty()) {
            tilPrecio.setError("Ingresa el precio");
            esValido = false;
        } else if (Double.parseDouble(precioTexto) <= 0) {
            tilPrecio.setError("El precio debe ser mayor que 0");
            esValido = false;
        }

        return esValido;
    }

    void guardarProducto() {
        if (!validarCampos()) {
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        int cantidad = Integer.parseInt(etCantidad.getText().toString().trim());
        double precio = Double.parseDouble(etPrecio.getText().toString().trim());

        Intent resultado = new Intent();
        resultado.putExtra("nombre", nombre);
        resultado.putExtra("cantidad", cantidad);
        resultado.putExtra("precio", precio);
        resultado.putExtra("fotoUri", fotoUri != null ? fotoUri.toString() : null); //Operador ternario

        setResult(RESULT_OK, resultado);
        finish();
    }
}