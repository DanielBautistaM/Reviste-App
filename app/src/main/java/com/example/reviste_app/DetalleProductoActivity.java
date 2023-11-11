package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.squareup.picasso.Picasso;

public class DetalleProductoActivity extends AppCompatActivity implements ImageViewPagerAdapter.OnImageClickListener {
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private TextView productSellerNameTextView;
    private RatingBar productRatingBar;
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
        productRatingBar = findViewById(R.id.product_rating_bar);
        viewPager = findViewById(R.id.view_pager_additional_images);

        // Retrieve the product details from the intent
        product = getIntent().getParcelableExtra("product");

        // Set the retrieved data to the UI elements
        if (product != null) {
            // Load the product image using a library like Picasso
            Picasso.get().load(product.getImage()).into(productImageView);

            productNameTextView.setText(product.getName());
            productPriceTextView.setText(String.valueOf(product.getPrice()));
            productDescriptionTextView.setText(product.getDescription());
            productSellerNameTextView.setText(product.getSellerName());

            // Check if additional images are available
            if (product.getAdditionalImages() != null && !product.getAdditionalImages().isEmpty()) {
                // Initialize and set up the ViewPager for additional images
                adapter = new ImageViewPagerAdapter(this, product.getAdditionalImages(), this);
                viewPager.setAdapter(adapter);
            }

            // Set the product rating from Firestore
            productRatingBar.setRating(product.getRatings());

            // Disable the RatingBar so it cannot be interacted with
            productRatingBar.setIsIndicator(true);

            // Set an OnClickListener for the product image to view it in full screen
            productImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageFullScreen(product.getImage());
                }
            });

            // Get a reference to the ImageButton for going back to MainActivity
            ImageButton backButton = findViewById(R.id.imageButton);

            // Set an OnClickListener for the back button
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to return to the MainActivity
                    Intent intent = new Intent(DetalleProductoActivity.this, MainActivity.class);
                    startActivity(intent);
                    // Ensure the user cannot return to this activity by pressing the "Back" button
                    finish();
                }
            });
        }
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
