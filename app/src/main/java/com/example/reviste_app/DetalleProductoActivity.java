package com.example.reviste_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class DetalleProductoActivity extends AppCompatActivity {
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private TextView productSellerNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // Initialize the UI elements
        productImageView = findViewById(R.id.product_image);
        productNameTextView = findViewById(R.id.product_name);
        productPriceTextView = findViewById(R.id.product_price);
        productDescriptionTextView = findViewById(R.id.product_description);
        productSellerNameTextView = findViewById(R.id.product_seller_name);

        // Retrieve the product details from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            String price = extras.getString("price");
            String image = extras.getString("image");
            String description = extras.getString("description");
            String sellerName = extras.getString("sellerName");

            // Set the retrieved data to the UI elements
            productNameTextView.setText(name);
            productPriceTextView.setText(price);
            productDescriptionTextView.setText(description);
            productSellerNameTextView.setText(sellerName);

            // Load the product image using a library like Picasso
            Picasso.get().load(image).into(productImageView);
        }
    }
}
