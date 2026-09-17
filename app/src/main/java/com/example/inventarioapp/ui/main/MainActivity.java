package com.example.inventarioapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inventarioapp.R;
import com.example.inventarioapp.data.repository.AuthRepositoryImpl;
import com.example.inventarioapp.data.repository.ProductoRepositoryImpl;
import com.example.inventarioapp.domain.model.Producto;
import com.example.inventarioapp.domain.repository.AuthRepository;
import com.example.inventarioapp.domain.repository.ProductoRepository;
import com.example.inventarioapp.domain.usecase.CerrarSesionUseCase;
import com.example.inventarioapp.ui.adapter.ProductoAdapter;
import com.example.inventarioapp.ui.detalle.DetalleActivity;
import com.example.inventarioapp.ui.login.LoginActivity;
import com.example.inventarioapp.ui.registro.RegistroActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.widget.TextView;
import com.example.inventarioapp.data.repository.ExchangeRateRepositoryImpl;
import com.example.inventarioapp.domain.model.TipoCambio;
import com.example.inventarioapp.domain.repository.ExchangeRateRepository;
import com.example.inventarioapp.domain.usecase.ObtenerTipoCambioUseCase;

public class MainActivity extends AppCompatActivity {

    List<Producto> productos = new ArrayList<>();
    List<Producto> productosFiltrados = new ArrayList<>();
    ProductoAdapter adapter;
    String textoBusqueda = "";
    RecyclerView rvInventario;
    ProductoRepository productoRepository;
    TextView tvTotalGeneral;
    ObtenerTipoCambioUseCase obtenerTipoCambioUseCase;
    TipoCambio tipoCambioActual; // se guarda en memoria para no pedirlo de nuevo en cada refresco de la lista

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

                    Producto nuevo = new Producto(nombre, precio, cantidad, fotoUri, categoria, descripcion);
                    productoRepository.insertar(nuevo);

                    cargarProductos();
                    Snackbar.make(rvInventario, "Producto agregado", Snackbar.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<Intent> detalleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                    boolean eliminado = resultado.getData().getBooleanExtra("eliminado", false);

                    if (eliminado) {
                        long idEliminado = resultado.getData().getLongExtra("id", -1);
                        Producto productoEliminado = buscarPorId(idEliminado);

                        productoRepository.eliminar(idEliminado);
                        cargarProductos();

                        Snackbar.make(rvInventario, "Producto eliminado", Snackbar.LENGTH_LONG)
                                .setAction("Deshacer", v -> {
                                    if (productoEliminado != null) {
                                        productoRepository.insertar(new Producto(
                                                productoEliminado.nombre, productoEliminado.precio,
                                                productoEliminado.cantidad, productoEliminado.fotoUri,
                                                productoEliminado.categoria, productoEliminado.descripcion));
                                        cargarProductos();
                                    }
                                })
                                .show();
                    } else {
                        Producto actualizado = (Producto) resultado.getData().getSerializableExtra("producto");
                        productoRepository.actualizar(actualizado);
                        cargarProductos();

                        Snackbar.make(rvInventario, "Producto actualizado", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

    Producto buscarPorId(long id) {
        for (Producto p : productos) {
            if (p.id == id) return p;
        }
        return null;
    }

    void cargarProductos() {
        productos.clear();
        productos.addAll(productoRepository.obtenerTodos());
        aplicarFiltro();
        actualizarTotalGeneral(); // se recalcula cada vez que la lista cambia
    }

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

    void abrirDetalle(Producto producto) {
        Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
        intent.putExtra("producto", producto);
        detalleLauncher.launch(intent);
    }

    // Suma el total de todo el inventario sin importar el filtro de búsqueda
    // y lo muestra en Quetzales + su conversión, si ya se obtuvo el tipo de cambio
    void actualizarTotalGeneral() {
        double totalQuetzales = 0;
        for (Producto p : productos) {
            totalQuetzales += p.getTotal();
        }

        StringBuilder texto = new StringBuilder(
                String.format(Locale.getDefault(), "Total inventario: Q %.2f", totalQuetzales));

        if (tipoCambioActual != null) {
            double totalUsd = totalQuetzales * tipoCambioActual.tasaUsd;
            double totalEur = totalQuetzales * tipoCambioActual.tasaEur;
            texto.append(String.format(Locale.getDefault(),
                    "  (≈ $%.2f / €%.2f)", totalUsd, totalEur));
        }

        tvTotalGeneral.setText(texto.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productoRepository = new ProductoRepositoryImpl(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvInventario = findViewById(R.id.rvInventario);
        Button btnAgregar = findViewById(R.id.btnAgregarProducto);
        TextInputEditText etBuscar = findViewById(R.id.etBuscar);
        tvTotalGeneral = findViewById(R.id.tvTotalGeneral);

        rvInventario.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductoAdapter(productosFiltrados, this::abrirDetalle);
        rvInventario.setAdapter(adapter);

        cargarProductos();

        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            registroLauncher.launch(intent);
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

        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepositoryImpl();
        obtenerTipoCambioUseCase = new ObtenerTipoCambioUseCase(exchangeRateRepository);

        // Se pide UNA vez al abrir la pantalla. Si llega bien, se guarda en memoria
        // y se vuelve a calcular el total (ya con la conversión incluida).
        obtenerTipoCambioUseCase.ejecutar(new ExchangeRateRepository.TipoCambioCallback() {
            @Override
            public void onExito(TipoCambio tipoCambio) {
                tipoCambioActual = tipoCambio;
                actualizarTotalGeneral();
            }

            @Override
            public void onError(String mensajeError) {
                // Si falla (sin internet, etc.), la app sigue funcionando normal,
                // solo que el total se queda mostrado nada más en Quetzales.
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accionCerrarSesion) {
            AuthRepository authRepository = new AuthRepositoryImpl(this);
            new CerrarSesionUseCase(authRepository).ejecutar();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}