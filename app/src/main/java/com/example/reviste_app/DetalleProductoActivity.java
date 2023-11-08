package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.squareup.picasso.Picasso;
import android.widget.ImageButton;


public class DetalleProductoActivity extends AppCompatActivity implements ImageViewPagerAdapter.OnImageClickListener {
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private TextView productSellerNameTextView;
    private ViewPager viewPager;
    private ImageViewPagerAdapter adapter;
    private Product product;

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
        viewPager = findViewById(R.id.view_pager_additional_images);

        // Retrieve the product details from the intent
        product = getIntent().getParcelableExtra("product");

        // Set the retrieved data to the UI elements
        if (product != null) {
            // Load the product image using a library like Picasso
            Picasso.get().load(product.getImage()).into(productImageView);

            productNameTextView.setText(product.getName());
            productPriceTextView.setText(product.getPrice());
            productDescriptionTextView.setText(product.getDescription());
            productSellerNameTextView.setText(product.getSellerName());

            // Initialize and set up the ViewPager for additional images
            adapter = new ImageViewPagerAdapter(this, product.getAdditionalImages(), this);
            viewPager.setAdapter(adapter);

            // Set an OnClickListener for the product image to view it in full screen
            productImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageFullScreen(product.getImage());
                }
            });
        }

        // Initialize the ImageButton
        ImageButton backButton = findViewById(R.id.imageButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity when the ImageButton is clicked
                Intent intent = new Intent(DetalleProductoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onImageClick(String imageUrl) {
        // Handle the click event to show the image in full screen
        showImageFullScreen(imageUrl);
    }

    public void showImageFullScreen(String imageUrl) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("image_url", imageUrl);
        startActivity(intent);
    }
}
