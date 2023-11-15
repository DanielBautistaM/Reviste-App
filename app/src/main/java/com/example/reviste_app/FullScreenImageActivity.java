package com.example.reviste_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {
    private ImageView fullScreenImageView;
    private GestureDetectorCompat gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullScreenImageView = findViewById(R.id.full_screen_image_view);

        // Retrieve the image URL from the intent
        String imageUrl = getIntent().getStringExtra("image_url");

        // Load the image into the ImageView using Picasso or another library
        Picasso.get().load(imageUrl).into(fullScreenImageView);

        // Set up GestureDetector for panning
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());
        fullScreenImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        // Set up ScaleGestureDetector for zooming
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            fullScreenImageView.scrollBy((int) distanceX, (int) distanceY);
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

            fullScreenImageView.setScaleX(scaleFactor);
            fullScreenImageView.setScaleY(scaleFactor);

            return true;
        }
    }
}
