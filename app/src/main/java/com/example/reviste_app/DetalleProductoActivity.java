package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;
import java.util.List;

public class DetalleProductoActivity extends AppCompatActivity implements ImageViewPagerAdapter.OnImageClickListener {

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        ImageView productImageView = findViewById(R.id.product_image);
        TextView productNameTextView = findViewById(R.id.product_name);
        TextView productPriceTextView = findViewById(R.id.product_price);
        TextView productDescriptionTextView = findViewById(R.id.product_description);
        TextView productSellerNameTextView = findViewById(R.id.product_seller_name);
        RatingBar productRatingBar = findViewById(R.id.product_rating_bar);
        ViewPager viewPager = findViewById(R.id.view_pager_additional_images);

        product = getIntent().getParcelableExtra("product");

        if (product != null) {
            Picasso.get().load(product.getImage()).into(productImageView);

            productNameTextView.setText(product.getName());
            productPriceTextView.setText(String.valueOf(product.getPrice()));
            productDescriptionTextView.setText(product.getDescription());
            productSellerNameTextView.setText(product.getSellerName());

            if (product.getAdditionalImages() != null && !product.getAdditionalImages().isEmpty()) {
                ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, product.getAdditionalImages(), this);
                viewPager.setAdapter(adapter);
            }

            productRatingBar.setRating(product.getRatings());
            productRatingBar.setIsIndicator(true);

            productImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageFullScreen(product.getImage());
                }
            });

            TextView addToCartButton = findViewById(R.id.btn_comprar);
            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Verificar si el producto ya está en el carrito
                    boolean productInCart = false;
                    for (CartItem item : CartManager.getCartItems()) {
                        if (item.getProductId().equals(product.getId())) {
                            productInCart = true;
                            break;
                        }
                    }

                    if (!productInCart) {
                        // El producto no está en el carrito, agregarlo
                        CartItem cartItem = new CartItem(product.getId(), product.getName(), 1, product.getPrice().doubleValue(), product.getImage());
                        CartManager.addToCart(cartItem);
                        // Mostrar un mensaje o realizar otras acciones después de agregar al carrito
                        Toast.makeText(DetalleProductoActivity.this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
                    } else {
                        // El producto ya está en el carrito, mostrar un mensaje o realizar otras acciones
                        Toast.makeText(DetalleProductoActivity.this, "El producto ya está en el carrito", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ImageButton backButton = findViewById(R.id.imageButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetalleProductoActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    public void onImageClick(String imageUrl) {
        showImageFullScreen(imageUrl);
    }

    public void showImageFullScreen(String imageUrl) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("image_url", imageUrl);
        startActivity(intent);
    }
}
