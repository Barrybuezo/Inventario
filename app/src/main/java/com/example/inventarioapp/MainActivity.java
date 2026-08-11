package com.example.inventarioapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

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
    List<Producto> productosFiltrados = new ArrayList<>();
    ProductoAdapter adapter;
    String textoBusqueda = "";
    ListView lvInventario;

    ActivityResultLauncher<Intent> registroLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                    String nombre = resultado.getData().getStringExtra("nombre");
                    int cantidad = resultado.getData().getIntExtra("cantidad", 0);
                    double precio = resultado.getData().getDoubleExtra("precio", 0);
                    String fotoUri = resultado.getData().getStringExtra("fotoUri");

                    productos.add(new Producto(nombre, precio, cantidad, fotoUri));
                    aplicarFiltro();
                    AlmacenProductos.guardar(this, productos); // guarda en disco

                    Snackbar.make(lvInventario, "Producto agregado", Snackbar.LENGTH_SHORT).show();
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
                        Producto productoEliminado = productos.get(position);
                        productos.remove(position);
                        aplicarFiltro();
                        AlmacenProductos.guardar(this, productos); // guarda en disco

                        Snackbar.make(lvInventario, "Producto eliminado", Snackbar.LENGTH_LONG)
                                .setAction("Deshacer", v -> {
                                    productos.add(position, productoEliminado);
                                    aplicarFiltro();
                                    AlmacenProductos.guardar(this, productos); // guarda en disco otra vez
                                })
                                .show();
                    } else {
                        Producto actualizado = (Producto) resultado.getData().getSerializableExtra("producto");
                        productos.set(position, actualizado);
                        aplicarFiltro();
                        AlmacenProductos.guardar(this, productos); // guarda en disco

                        Snackbar.make(lvInventario, "Producto actualizado", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

    void aplicarFiltro() {
        productosFiltrados.clear();

        for (Producto p : productos) {
            if (textoBusqueda.isEmpty() ||
                    p.nombre.toLowerCase(Locale.getDefault())
                            .contains(textoBusqueda.toLowerCase(Locale.getDefault()))) {
                productosFiltrados.add(p);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvInventario = findViewById(R.id.lvInventario);
        Button btnAgregar = findViewById(R.id.btnAgregarProducto);
        TextInputEditText etBuscar = findViewById(R.id.etBuscar);

        // Carga lo que se haya guardado antes. Si es la primera vez, llega vacío.
        productos.addAll(AlmacenProductos.cargar(this));

        adapter = new ProductoAdapter(this, productosFiltrados);
        lvInventario.setAdapter(adapter);

        aplicarFiltro(); // pinta en pantalla lo que se acaba de cargar

        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            registroLauncher.launch(intent);
        });

        lvInventario.setOnItemClickListener((parent, view, position, id) -> {
            Producto productoSeleccionado = productosFiltrados.get(position);
            int positionReal = productos.indexOf(productoSeleccionado);

            Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
            intent.putExtra("producto", productoSeleccionado);
            intent.putExtra("position", positionReal);
            detalleLauncher.launch(intent);
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusqueda = s.toString();
                aplicarFiltro();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}