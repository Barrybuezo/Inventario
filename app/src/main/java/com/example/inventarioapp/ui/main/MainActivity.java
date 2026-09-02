package com.example.inventarioapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventarioapp.R;
import com.example.inventarioapp.data.repository.ProductoRepositoryImpl;
import com.example.inventarioapp.domain.model.Producto;
import com.example.inventarioapp.domain.repository.ProductoRepository;
import com.example.inventarioapp.ui.adapter.ProductoAdapter;
import com.example.inventarioapp.ui.detalle.DetalleActivity;
import com.example.inventarioapp.ui.registro.RegistroActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    List<Producto> productos = new ArrayList<>();
    List<Producto> productosFiltrados = new ArrayList<>();
    ProductoAdapter adapter;
    String textoBusqueda = "";
    ListView lvInventario;

    ProductoRepository productoRepository;

    ActivityResultLauncher<Intent> registroLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                    String nombre = resultado.getData().getStringExtra("nombre");
                    int cantidad = resultado.getData().getIntExtra("cantidad", 0);
                    double precio = resultado.getData().getDoubleExtra("precio", 0);
                    String fotoUri = resultado.getData().getStringExtra("fotoUri");
                    String categoria = resultado.getData().getStringExtra("categoria");
                    String descripcion = resultado.getData().getStringExtra("descripcion");

                    productos.add(new Producto(nombre, precio, cantidad, fotoUri, categoria, descripcion));
                    aplicarFiltro();
                    productoRepository.guardar(productos);

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
                        productoRepository.guardar(productos);

                        Snackbar.make(lvInventario, "Producto eliminado", Snackbar.LENGTH_LONG)
                                .setAction("Deshacer", v -> {
                                    productos.add(position, productoEliminado);
                                    aplicarFiltro();
                                    productoRepository.guardar(productos);
                                })
                                .show();
                    } else {
                        Producto actualizado = (Producto) resultado.getData().getSerializableExtra("producto");
                        productos.set(position, actualizado);
                        aplicarFiltro();
                        productoRepository.guardar(productos);

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

        productoRepository = new ProductoRepositoryImpl(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvInventario = findViewById(R.id.lvInventario);
        Button btnAgregar = findViewById(R.id.btnAgregarProducto);
        TextInputEditText etBuscar = findViewById(R.id.etBuscar);

        productos.addAll(productoRepository.cargar());

        adapter = new ProductoAdapter(this, productosFiltrados);
        lvInventario.setAdapter(adapter);

        aplicarFiltro();

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusqueda = s.toString();
                aplicarFiltro();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}