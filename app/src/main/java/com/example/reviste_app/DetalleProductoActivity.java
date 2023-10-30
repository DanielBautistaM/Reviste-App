package com.example.reviste_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetalleProductoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // Recupera los datos pasados desde la actividad principal
        String productName = getIntent().getStringExtra("product_name");
        String productPrice = getIntent().getStringExtra("product_price");
        String productImage = getIntent().getStringExtra("product_image");
        String productDescription = getIntent().getStringExtra("product_description");

        // Vincula los elementos del diseño
        ImageView imgProductoDetalle = findViewById(R.id.img_producto_detalle);
        TextView nombreProductoDetalle = findViewById(R.id.nombre_producto_detalle);
        TextView precioProductoDetalle = findViewById(R.id.precio_producto_detalle);
        TextView descripcionProductoDetalle = findViewById(R.id.descripcion_producto_detalle);

        // Configura los elementos con los datos del producto
        nombreProductoDetalle.setText(productName);
        precioProductoDetalle.setText(productPrice);

        // Carga la imagen con Glide
        Glide.with(this)
                .load(productImage)
                .into(imgProductoDetalle);

        descripcionProductoDetalle.setText(productDescription);
    }
}
