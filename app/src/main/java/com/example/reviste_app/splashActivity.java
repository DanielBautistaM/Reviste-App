        package com.example.reviste_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class splashActivity extends AppCompatActivity {

    TextView Revisteapp;
    LottieAnimationView lottie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appname = findViewById(R.id.Revisteapp);
    }
}