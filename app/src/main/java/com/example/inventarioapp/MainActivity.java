package com.example.inventarioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static class Producto {
        String nombre;
        double precio;
        int cantidad;

        Producto(String nombre, double precio, int cantidad) {
            this.nombre = nombre;
            this.precio = precio;
            this.cantidad = cantidad;
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

                    Producto nuevo = new Producto(nombre, precio, cantidad);
                    productos.add(nuevo);

                    nombres.add(nuevo.nombre);
                    detalles.add(String.format("Q %.2f -- %d unidades -- Total: Q %.2f",
                            nuevo.precio, nuevo.cantidad, nuevo.getTotal()));

                    adapter.notifyDataSetChanged();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvInventario = findViewById(R.id.lvInventario);
        Button btnAgregar = findViewById(R.id.btnAgregarProducto);

        productos.add(new Producto("Azucar", 5.45, 10));
        productos.add(new Producto("Frijol", 6.75, 15));
        productos.add(new Producto("Harina", 12.25, 6));

        //simple_list_item_2
        for (Producto p : productos) {
            nombres.add(p.nombre);
            detalles.add(String.format("Q %.2f -- %d unidades -- Total: Q %.2f",
                    p.precio, p.cantidad, p.getTotal()));
        }

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

    }
}