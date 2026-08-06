package com.example.inventarioapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    // "productos" es la lista COMPLETA, la fuente real de datos.
    List<Producto> productos = new ArrayList<>();

    // "productosFiltrados" es lo que se está mostrando en pantalla AHORA MISMO.
    // Si no hay texto en el buscador, es una copia exacta de "productos".
    List<Producto> productosFiltrados = new ArrayList<>();

    List<String> nombres = new ArrayList<>();
    List<String> detalles = new ArrayList<>();
    ArrayAdapter<String> adapter;

    String textoBusqueda = "";

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

                    aplicarFiltro();
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
                    } else {
                        Producto actualizado = (Producto) resultado.getData().getSerializableExtra("producto");
                        productos.set(position, actualizado);
                    }

                    aplicarFiltro();
                }
            });

    String construirDetalle(Producto p) {
        return String.format(Locale.getDefault(), "Q %.2f -- %d unidades -- Total: Q %.2f",
                p.precio, p.cantidad, p.getTotal());
    }

    // Reconstruye "productosFiltrados", "nombres" y "detalles" según lo que haya
    // escrito en el buscador, y refresca la ListView.
    void aplicarFiltro() {
        productosFiltrados.clear();
        nombres.clear();
        detalles.clear();

        for (Producto p : productos) {
            if (textoBusqueda.isEmpty() ||
                    p.nombre.toLowerCase(Locale.getDefault())
                            .contains(textoBusqueda.toLowerCase(Locale.getDefault()))) {
                productosFiltrados.add(p);
                nombres.add(p.nombre);
                detalles.add(construirDetalle(p));
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvInventario = findViewById(R.id.lvInventario);
        Button btnAgregar = findViewById(R.id.btnAgregarProducto);
        EditText etBuscar = findViewById(R.id.etBuscar);

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
            // "position" aquí es la fila dentro de la lista FILTRADA (lo que se ve).
            Producto productoSeleccionado = productosFiltrados.get(position);

            // Hay que traducirlo a su posición real dentro de "productos" (la lista completa),
            // porque es esa posición la que DetalleActivity necesita para editar/eliminar bien.
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