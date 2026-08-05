package com.example.inventarioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    static class Producto implements Serializable {
        String nombre;
        double precio;
        int cantidad;
        String fotoUri;

        Producto(String nombre, double precio, int cantidad, String fotoUri) {
            this.nombre = nombre;
            this.precio = precio;
            this.cantidad = cantidad;
            this.fotoUri = fotoUri;
        }

        double getTotal() {
            return precio * cantidad;
        }
    }

    List<Producto> productos = new ArrayList<>();
    List<String> nombres = new ArrayList<>();
    List<String> detalles = new ArrayList<>();
    ArrayAdapter<String> adapter;

    ActivityResultLauncher<Intent> registroLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                    String nombre = resultado.getData().getStringExtra("nombre");
                    int cantidad = resultado.getData().getIntExtra("cantidad", 0);
                    double precio = resultado.getData().getDoubleExtra("precio", 0);
                    String fotoUri = resultado.getData().getStringExtra("fotoUri");

                    Producto nuevo = new Producto(nombre, precio, cantidad, fotoUri);
                    productos.add(nuevo);
                    nombres.add(nuevo.nombre);



                    detalles.add(String.format("Q %.2f -- %d unidades -- Total: Q %.2f",
                            nuevo.precio, nuevo.cantidad, nuevo.getTotal()));

                    adapter.notifyDataSetChanged();
                }
            });

    ActivityResultLauncher<Intent> detalleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                    int position = resultado.getData().getIntExtra("position", -1);
                    boolean eliminado = resultado.getData().getBooleanExtra("eliminado", false);

                    if (position == -1) return;

                    if (eliminado) {
                        productos.remove(position);
                        nombres.remove(position);
                        detalles.remove(position);
                    } else {
                        Producto actualizado = (Producto) resultado.getData().getSerializableExtra("producto");
                        productos.set(position, actualizado);
                        nombres.set(position, actualizado.nombre);
                        detalles.set(position, construirDetalle(actualizado));
                    }

                    adapter.notifyDataSetChanged();
                }
            });

    String construirDetalle(Producto p) {
        return String.format(Locale.getDefault(), "Q %.2f -- %d unidades -- Total: Q %.2f",
                p.precio, p.cantidad, p.getTotal());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvInventario = findViewById(R.id.lvInventario);
        Button btnAgregar = findViewById(R.id.btnAgregarProducto);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                nombres) {
            @Override
            public android.view.View getView(int position, android.view.View convertView,
                                             android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                android.widget.TextView text2 = view.findViewById(android.R.id.text2);
                text2.setText(detalles.get(position));
                return view;
            }
        };

        lvInventario.setAdapter(adapter);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            registroLauncher.launch(intent);
        });

        lvInventario.setOnItemClickListener((parent, view, position, id) -> {
            Producto productoSeleccionado = productos.get(position);
            Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
            intent.putExtra("producto", productoSeleccionado);
            intent.putExtra("position", position);
            detalleLauncher.launch(intent);
        });

    }
}