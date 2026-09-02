package com.example.inventarioapp.ui.registro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.inventarioapp.R;
import com.example.inventarioapp.domain.model.Producto;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    TextInputEditText etNombre, etCantidad, etPrecio, etDescripcion;
    TextInputLayout tilNombre, tilCantidad, tilPrecio, tilCategoria;
    AutoCompleteTextView actCategoria;
    ImageView ivFoto;
    TextView tvEstadoFoto;
    Uri fotoUri;
    View root;

    ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            exito -> {
                if (exito) {
                    ivFoto.setVisibility(View.VISIBLE);
                    ivFoto.setImageURI(fotoUri);
                    tvEstadoFoto.setText("Foto tomada");
                } else {
                    Snackbar.make(root, "No se tomó la foto", Snackbar.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<String> permisoLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            concedido -> {
                if (concedido) {
                    lanzarCamara();
                } else {
                    Snackbar.make(root, "Se necesita el permiso de cámara para tomar la foto", Snackbar.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<PickVisualMediaRequest> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    copiarImagenGaleria(uri);
                } else {
                    Snackbar.make(root, "No se seleccionó ninguna imagen", Snackbar.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        root = findViewById(R.id.rootRegistro);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        etNombre = findViewById(R.id.etNombre);
        etCantidad = findViewById(R.id.etCantidad);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);

        tilNombre = findViewById(R.id.tilNombre);
        tilCantidad = findViewById(R.id.tilCantidad);
        tilPrecio = findViewById(R.id.tilPrecio);
        tilCategoria = findViewById(R.id.tilCategoria);
        actCategoria = findViewById(R.id.actCategoria);

        // Llena el dropdown con la lista fija que está en res/values/arrays.xml.
        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(
                this, R.array.categorias_producto, android.R.layout.simple_dropdown_item_1line);
        actCategoria.setAdapter(categoriaAdapter);

        ivFoto = findViewById(R.id.ivFoto);
        tvEstadoFoto = findViewById(R.id.tvEstadoFoto);

        MaterialButton btnTomarFoto = findViewById(R.id.btnTomarFoto);
        MaterialButton btnElegirGaleria = findViewById(R.id.btnElegirGaleria);
        MaterialButton btnGuardar = findViewById(R.id.btnGuardar);
        MaterialButton btnCancelar = findViewById(R.id.btnCancelar);

        TextView tvTituloFormulario = findViewById(R.id.tvTituloFormulario);

        Producto productoExistente;
        if (Build.VERSION.SDK_INT >= 33) {
            productoExistente = getIntent().getSerializableExtra("producto", Producto.class);
        } else {
            productoExistente = (Producto) getIntent().getSerializableExtra("producto");
        }

        if (productoExistente != null) {
            toolbar.setTitle("Editar producto");
            tvTituloFormulario.setText("Editar producto");
            btnGuardar.setText("Actualizar producto");

            etNombre.setText(productoExistente.nombre);
            etCantidad.setText(String.valueOf(productoExistente.cantidad));
            etPrecio.setText(String.valueOf(productoExistente.precio));
            etDescripcion.setText(productoExistente.descripcion);
            actCategoria.setText(productoExistente.categoria, false);

            if (productoExistente.fotoUri != null) {
                fotoUri = Uri.parse(productoExistente.fotoUri);
                ivFoto.setVisibility(View.VISIBLE);
                ivFoto.setImageURI(fotoUri);
                tvEstadoFoto.setText("Foto actual");
            }
        } else {
            toolbar.setTitle("Nuevo producto");
        }

        btnTomarFoto.setOnClickListener(v -> abrirCamara());

        btnElegirGaleria.setOnClickListener(v -> galleryLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

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
            Snackbar.make(root, "Error al crear el archivo de la foto", Snackbar.LENGTH_SHORT).show();
        }
    }

    void copiarImagenGaleria(Uri origenUri) {
        try {
            String nombreArchivo = "galeria_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date()) + ".jpg";
            File carpeta = getExternalFilesDir("Pictures");
            File archivoDestino = new File(carpeta, nombreArchivo);

            InputStream entrada = getContentResolver().openInputStream(origenUri);
            OutputStream salida = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[4096];
            int bytesLeidos;
            while ((bytesLeidos = entrada.read(buffer)) != -1) {
                salida.write(buffer, 0, bytesLeidos);
            }

            entrada.close();
            salida.close();

            fotoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.inventarioapp.fileprovider",
                    archivoDestino);

            ivFoto.setVisibility(View.VISIBLE);
            ivFoto.setImageURI(fotoUri);
            tvEstadoFoto.setText("Foto seleccionada de galería");

        } catch (IOException e) {
            Snackbar.make(root, "Error al copiar la imagen", Snackbar.LENGTH_SHORT).show();
        }
    }

    boolean validarCampos() {
        boolean esValido = true;

        tilNombre.setError(null);
        tilCantidad.setError(null);
        tilPrecio.setError(null);
        tilCategoria.setError(null);

        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String cantidadTexto = etCantidad.getText() != null ? etCantidad.getText().toString().trim() : "";
        String precioTexto = etPrecio.getText() != null ? etPrecio.getText().toString().trim() : "";
        String categoria = actCategoria.getText() != null ? actCategoria.getText().toString().trim() : "";

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

        if (categoria.isEmpty()) {
            tilCategoria.setError("Selecciona una categoría");
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
        String categoria = actCategoria.getText().toString().trim();
        String descripcion = etDescripcion.getText() != null ? etDescripcion.getText().toString().trim() : "";

        Intent resultado = new Intent();
        resultado.putExtra("nombre", nombre);
        resultado.putExtra("cantidad", cantidad);
        resultado.putExtra("precio", precio);
        resultado.putExtra("fotoUri", fotoUri != null ? fotoUri.toString() : null);
        resultado.putExtra("categoria", categoria);
        resultado.putExtra("descripcion", descripcion);

        setResult(RESULT_OK, resultado);
        finish();
    }
}