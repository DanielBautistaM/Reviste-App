package com.example.reviste_app;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {
    private ImageView fullScreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullScreenImageView = findViewById(R.id.full_screen_image_view);

        // Retrieve the image URL from the intent
        String imageUrl = getIntent().getStringExtra("image_url");

        // Load the image into the ImageView using Picasso or another library
        Picasso.get().load(imageUrl).into(fullScreenImageView);
    }
}
