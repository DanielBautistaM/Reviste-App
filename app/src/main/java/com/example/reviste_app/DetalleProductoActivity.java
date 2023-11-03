package com.example.reviste_app;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.util.List;

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
        List<String> additionalImages = getIntent().getStringArrayListExtra("product_additional_images");
        String sellerName = getIntent().getStringExtra("product_seller_name");

        // Vincula los elementos del diseño
        ImageView imgProductoDetalle = findViewById(R.id.img_producto_detalle);
        TextView nombreProductoDetalle = findViewById(R.id.nombre_producto_detalle);
        TextView precioProductoDetalle = findViewById(R.id.precio_producto_detalle);
        TextView descripcionProductoDetalle = findViewById(R.id.productDescription);
        LinearLayout layoutImagenesAdicionales = findViewById(R.id.layout_imagenes_adicionales);
        TextView txtNombreVendedor = findViewById(R.id.sellerName);

        // Configura los elementos con los datos del producto
        nombreProductoDetalle.setText(productName);
        precioProductoDetalle.setText(productPrice);
        descripcionProductoDetalle.setText(productDescription);
        txtNombreVendedor.setText(sellerName);

        // Carga la imagen principal con Glide
        Glide.with(this)
                .load(productImage)
                .into(imgProductoDetalle);

        // Carga las imágenes adicionales (si las hay) en el layout horizontal
        if (additionalImages != null) {
            for (String imageUrl : additionalImages) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(5, 0, 5, 0); // Margen entre las imágenes
                imageView.setLayoutParams(layoutParams);
                Glide.with(this)
                        .load(imageUrl)
                        .into(imageView);
                layoutImagenesAdicionales.addView(imageView);
            }
        }

        // Vincula el botón de regreso
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acción para volver a MainActivity
                Intent intent = new Intent(DetalleProductoActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual para evitar que quede en la pila hacia atrás
            }
        });
    }
}
