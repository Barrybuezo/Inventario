package com.example.inventarioapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.inventarioapp.R;
import com.example.inventarioapp.domain.model.Producto;
import java.util.List;
import java.util.Locale;
public class ProductoAdapter extends ArrayAdapter<Producto> {
    public ProductoAdapter(Context context, List<Producto> productos) {
        super(context, 0, productos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_producto, parent, false);
        }

        Producto producto = getItem(position);

        TextView tvNombreItem = convertView.findViewById(R.id.tvNombreItem);
        TextView tvCategoriaItem = convertView.findViewById(R.id.tvCategoriaItem);
        TextView tvDetalleItem = convertView.findViewById(R.id.tvDetalleItem);

        tvNombreItem.setText(producto.nombre);
        tvCategoriaItem.setText(producto.categoria);
        tvDetalleItem.setText(String.format(Locale.getDefault(),
                "Q %.2f -- %d unidades -- Total: Q %.2f",
                producto.precio, producto.cantidad, producto.getTotal()));

        return convertView;
    }
}