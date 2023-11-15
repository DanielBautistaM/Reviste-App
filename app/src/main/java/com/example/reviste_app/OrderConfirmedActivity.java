package com.example.reviste_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OrderConfirmedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);

        TextView btnSaveAddress = findViewById(R.id.btnSaveAddress);

        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch MainActivity when btnSaveAddress is clicked
                Intent intent = new Intent(OrderConfirmedActivity.this, MainActivity.class);
                startActivity(intent);

                // Finish the current activity (OrderConfirmedActivity) if you want
                finish();
            }
        });
    }
}
