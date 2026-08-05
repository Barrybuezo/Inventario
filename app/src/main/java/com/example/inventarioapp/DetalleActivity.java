package com.example.inventarioapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class DetalleActivity extends AppCompatActivity {

    ImageView ivFotoDetalle;
    TextView tvNombreDetalle, tvCantidadDetalle, tvPrecioDetalle, tvTotalDetalle;
    MainActivity.Producto producto;
    int position;

    ActivityResultLauncher<Intent> editarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                    String nombre = resultado.getData().getStringExtra("nombre");
                    int cantidad = resultado.getData().getIntExtra("cantidad", 0);
                    double precio = resultado.getData().getDoubleExtra("precio", 0);
                    String fotoUri = resultado.getData().getStringExtra("fotoUri");

                    producto = new MainActivity.Producto(nombre, precio, cantidad, fotoUri);

                    mostrarDatos();
                    enviarResultadoActualizado();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);

        // Ya NO se vuelve a declarar el tipo aquí (ImageView / TextView / etc.),
        // así se asignan a las variables de la CLASE, no a unas nuevas locales.
        ivFotoDetalle = findViewById(R.id.ivFotoDetalle);
        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvCantidadDetalle = findViewById(R.id.tvCantidadDetalle);
        tvPrecioDetalle = findViewById(R.id.tvPrecioDetalle);
        tvTotalDetalle = findViewById(R.id.tvTotalDetalle);
        Button btnEditarDetalle = findViewById(R.id.btnEditarDetalle);
        Button btnEliminarDetalle = findViewById(R.id.btnEliminarDetalle);

        //Validar la versión de Android del celular
        if (Build.VERSION.SDK_INT >= 33) {
            //Usar la nueva versión del Método
            producto = getIntent().getSerializableExtra("producto", MainActivity.Producto.class);
        } else {
            //Usar el método clásico
            producto = (MainActivity.Producto) getIntent().getSerializableExtra("producto");
            //MainActivity.Producto fuerza a Java a interpretar ese paquete como un Producto
        }
        position = getIntent().getIntExtra("position", -1);

        //En lugar de leer variables inexistentes se cierra la pantalla y se sale de la función
        if (producto == null || position == -1) {
            finish();
            return;
        }

        mostrarDatos();

        btnEditarDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleActivity.this, RegistroActivity.class);
            intent.putExtra("producto", producto);
            editarLauncher.launch(intent);
        });

        btnEliminarDetalle.setOnClickListener(v -> confirmarEliminacion());



    }


    void mostrarDatos(){
        //Llamar los datos para pintarlos en pantalla
        tvNombreDetalle.setText(producto.nombre);
        tvCantidadDetalle.setText(producto.cantidad + " unidades");
        tvPrecioDetalle.setText(String.format(Locale.getDefault(), "Q %.2f c/u", producto.precio));
        tvTotalDetalle.setText(String.format(Locale.getDefault(), "Total: Q %.2f", producto.getTotal()));

        //Mostrar la foto
        if (producto.fotoUri != null) {
            ivFotoDetalle.setVisibility(View.VISIBLE);//Mostrar el contenedor de la foto
            ivFotoDetalle.setImageURI(Uri.parse(producto.fotoUri));//La dirección de la foto en texto pasa a ser Uri otra vez
        }
    }

    void confirmarEliminacion(){
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro que desea eliminar \"" + producto.nombre + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    Intent resultado = new Intent();
                    resultado.putExtra("position", position);
                    resultado.putExtra("eliminado", true);
                    setResult(RESULT_OK, resultado);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    void enviarResultadoActualizado(){
        Intent resultado = new Intent();
        resultado.putExtra("position", position);
        resultado.putExtra("producto", producto);
        setResult(RESULT_OK, resultado );
    }
}