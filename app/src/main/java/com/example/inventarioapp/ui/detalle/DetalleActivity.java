package com.example.inventarioapp.ui.detalle;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.inventarioapp.R;
import com.example.inventarioapp.domain.model.Producto;
import com.example.inventarioapp.ui.registro.RegistroActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Locale;
import com.example.inventarioapp.data.repository.ExchangeRateRepositoryImpl;
import com.example.inventarioapp.domain.model.TipoCambio;
import com.example.inventarioapp.domain.repository.ExchangeRateRepository;
import com.example.inventarioapp.domain.usecase.ObtenerTipoCambioUseCase;

public class DetalleActivity extends AppCompatActivity {

    ImageView ivFotoDetalle;
    TextView tvNombreDetalle, tvCategoriaDetalle, tvCantidadDetalle, tvPrecioDetalle, tvTotalDetalle, tvDescripcionDetalle;
    Producto producto;
    TextView tvTotalUsd, tvTotalEur;
    ObtenerTipoCambioUseCase obtenerTipoCambioUseCase;

    ActivityResultLauncher<Intent> editarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                    String nombre = resultado.getData().getStringExtra("nombre");
                    int cantidad = resultado.getData().getIntExtra("cantidad", 0);
                    double precio = resultado.getData().getDoubleExtra("precio", 0);
                    String fotoUri = resultado.getData().getStringExtra("fotoUri");
                    String categoria = resultado.getData().getStringExtra("categoria");
                    String descripcion = resultado.getData().getStringExtra("descripcion");

                    // Conservamos el mismo id: seguimos editando el MISMO registro,
                    // no se crea uno nuevo en la base de datos.
                    producto = new Producto(producto.id, nombre, precio, cantidad, fotoUri, categoria, descripcion);

                    mostrarDatos();
                    enviarResultadoActualizado();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivFotoDetalle = findViewById(R.id.ivFotoDetalle);
        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvCategoriaDetalle = findViewById(R.id.tvCategoriaDetalle);
        tvCantidadDetalle = findViewById(R.id.tvCantidadDetalle);
        tvPrecioDetalle = findViewById(R.id.tvPrecioDetalle);
        tvTotalDetalle = findViewById(R.id.tvTotalDetalle);
        tvDescripcionDetalle = findViewById(R.id.tvDescripcionDetalle);
        MaterialButton btnEditarDetalle = findViewById(R.id.btnEditarDetalle);
        MaterialButton btnEliminarDetalle = findViewById(R.id.btnEliminarDetalle);
        tvTotalUsd = findViewById(R.id.tvTotalUsd);
        tvTotalEur = findViewById(R.id.tvTotalEur);

        if (Build.VERSION.SDK_INT >= 33) {
            producto = getIntent().getSerializableExtra("producto", Producto.class);
        } else {
            producto = (Producto) getIntent().getSerializableExtra("producto");
        }

        if (producto == null) {
            finish();
            return;
        }

        mostrarDatos();

        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepositoryImpl();
        obtenerTipoCambioUseCase = new ObtenerTipoCambioUseCase(exchangeRateRepository);

        obtenerTipoCambioUseCase.ejecutar(new ExchangeRateRepository.TipoCambioCallback() {
            @Override
            public void onExito(TipoCambio tipoCambio) {
                double totalUsd = producto.getTotal() * tipoCambio.tasaUsd;
                double totalEur = producto.getTotal() * tipoCambio.tasaEur;

                tvTotalUsd.setText(String.format(Locale.getDefault(), "≈ $ %.2f USD", totalUsd));
                tvTotalEur.setText(String.format(Locale.getDefault(), "≈ € %.2f EUR", totalEur));
            }

            @Override
            public void onError(String mensajeError) {
                tvTotalUsd.setText("Tipo de cambio no disponible");
                tvTotalEur.setText("");
            }
        });

        btnEditarDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleActivity.this, RegistroActivity.class);
            intent.putExtra("producto", producto);
            editarLauncher.launch(intent);
        });

        btnEliminarDetalle.setOnClickListener(v -> confirmarEliminacion());
    }


    void mostrarDatos(){
        tvNombreDetalle.setText(producto.nombre);
        tvCategoriaDetalle.setText(producto.categoria);
        tvCantidadDetalle.setText(producto.cantidad + " unidades");
        tvPrecioDetalle.setText(String.format(Locale.getDefault(), "Q %.2f c/u", producto.precio));
        tvTotalDetalle.setText(String.format(Locale.getDefault(), "Total: Q %.2f", producto.getTotal()));
        tvDescripcionDetalle.setText(
                producto.descripcion != null && !producto.descripcion.isEmpty()
                        ? producto.descripcion
                        : "Sin descripción");

        if (producto.fotoUri != null) {
            ivFotoDetalle.setVisibility(View.VISIBLE);
            ivFotoDetalle.setImageURI(Uri.parse(producto.fotoUri));
        }
    }

    void confirmarEliminacion(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro que desea eliminar \"" + producto.nombre + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    Intent resultado = new Intent();
                    resultado.putExtra("id", producto.id);
                    resultado.putExtra("eliminado", true);
                    setResult(RESULT_OK, resultado);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    void enviarResultadoActualizado(){
        Intent resultado = new Intent();
        resultado.putExtra("producto", producto);
        setResult(RESULT_OK, resultado);
    }
}