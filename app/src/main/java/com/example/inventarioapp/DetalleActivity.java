package com.example.inventarioapp;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class DetalleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);

        ImageView ivFotoDetalle = findViewById(R.id.ivFotoDetalle);
        TextView tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        TextView tvCantidadDetalle = findViewById(R.id.tvCantidadDetalle);
        TextView tvPrecioDetalle = findViewById(R.id.tvPrecioDetalle);
        TextView tvTotalDetalle = findViewById(R.id.tvTotalDetalle);

        //Validar la versión de Android del celular
        MainActivity.Producto producto;
        if(Build.VERSION.SDK_INT >=33){
            //Usar la nueva versión del Método
            producto = getIntent().getSerializableExtra("producto", MainActivity.Producto.class);
        }else{
            //Usar el método clásico
            producto = (MainActivity.Producto) getIntent().getSerializableExtra("producto");
            //MainActivity.Producto fuerza a Java a interpretar ese paquete como un Producto
        }

        //En lugar de leer variables inexistentes se cierra la pantalla y se sale de la función
        if(producto == null){
            finish();
            return;
        }

        //Llamar los datos para pintarlos en pantalla
        tvNombreDetalle.setText(producto.nombre);
        tvCantidadDetalle.setText(producto.cantidad + " unidades");
        tvPrecioDetalle.setText(String.format(Locale.getDefault(), "Q %.2f c/u", producto.precio));
        tvTotalDetalle.setText(String.format(Locale.getDefault(), "Total: Q %.2f", producto.getTotal()));

        //Mostrar la foto
        if (producto.fotoUri != null){
            ivFotoDetalle.setVisibility(View.VISIBLE);//Mostrar el contenedor de la foto
            ivFotoDetalle.setImageURI(Uri.parse(producto.fotoUri));//La dirección de la foto en texto pasa a ser Uri otra vez
        }
    }
}