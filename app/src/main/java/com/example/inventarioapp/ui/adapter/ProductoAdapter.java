package com.example.inventarioapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inventarioapp.R;
import com.example.inventarioapp.domain.model.Producto;
import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    // Interfaz de callback: es la forma en que el adapter le avisa a quien lo
    // esté usando (MainActivity) que el usuario tocó un producto específico.
    // MainActivity implementa este método cuando crea el adapter.
    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    private final List<Producto> productos;
    private final OnProductoClickListener listener;

    public ProductoAdapter(List<Producto> productos, OnProductoClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    // Se llama UNA vez por cada fila nueva que hay que crear desde cero
    // (no cada vez que se dibuja — de eso se encarga onBindViewHolder).
    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_producto, parent, false);
        return new ProductoViewHolder(vista);
    }

    // Se llama cada vez que una fila (ya sea nueva o reciclada) necesita
    // mostrar los datos de una posición específica.
    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.bind(producto, listener);
    }

    // Le dice al RecyclerView cuántos elementos hay en total.
    @Override
    public int getItemCount() {
        return productos.size();
    }

    // El ViewHolder "sostiene" las referencias a las vistas de una fila,
    // para no tener que buscarlas de nuevo con findViewById cada vez que se recicla.
    static class ProductoViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreItem, tvCategoriaItem, tvDetalleItem;

        ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreItem = itemView.findViewById(R.id.tvNombreItem);
            tvCategoriaItem = itemView.findViewById(R.id.tvCategoriaItem);
            tvDetalleItem = itemView.findViewById(R.id.tvDetalleItem);
        }

        void bind(Producto producto, OnProductoClickListener listener) {
            tvNombreItem.setText(producto.nombre);
            tvCategoriaItem.setText(producto.categoria);
            tvDetalleItem.setText(String.format(Locale.getDefault(),
                    "Q %.2f -- %d unidades -- Total: Q %.2f",
                    producto.precio, producto.cantidad, producto.getTotal()));

            itemView.setOnClickListener(v -> listener.onProductoClick(producto));
        }
    }
}