package com.example.reviste_app;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class splashActivity extends AppCompatActivity {

    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottie = findViewById(R.id.lottie);
        lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Animation started (no action needed)
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Animation has ended, navigate to the next screen or activity
                Intent intent = new Intent(splashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the current splash activity
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Animation was canceled (no action needed)
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Animation repeated (no action needed)
            }
        });
    }
}
