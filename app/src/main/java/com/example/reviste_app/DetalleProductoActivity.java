package com.example.reviste_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetalleProductoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // Obtener el ID del producto pasado desde la actividad anterior
        String productID = getIntent().getStringExtra("product_id");

        if (productID != null) {
            // Obtener una referencia a Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Inicializar referencias a elementos de diseño
            ImageView imgProductoDetalle = findViewById(R.id.img_producto_detalle);
            TextView nombreProductoDetalle = findViewById(R.id.nombre_producto_detalle);
            TextView precioProductoDetalle = findViewById(R.id.precio_producto_detalle);
            TextView descripcionProductoDetalle = findViewById(R.id.descripcion_producto_detalle);
            TextView txtNombreVendedor = findViewById(R.id.txt_nombrevendedor);

            // Consultar Firestore para obtener los detalles del producto
            db.collection("Productos")
                    .document(productID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Recuperar datos del producto
                                String productName = document.getString("name");
                                String productPrice = document.getString("price");
                                String productImage = document.getString("image");
                                String productDescription = document.getString("description");
                                String sellerName = document.getString("sellerName");

                                // Establecer los datos recuperados en las vistas correspondientes
                                nombreProductoDetalle.setText(productName);
                                precioProductoDetalle.setText(productPrice);

                                if (productDescription != null) {
                                    descripcionProductoDetalle.setText(productDescription);
                                } else {
                                    descripcionProductoDetalle.setText("Descripción no disponible");
                                }

                                txtNombreVendedor.setText(sellerName);

                                // Cargar la imagen principal usando Glide
                                Glide.with(this)
                                        .load(productImage)
                                        .into(imgProductoDetalle);
                            }
                        }
                    });
        }
    }
}
