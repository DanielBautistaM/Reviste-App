package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogOutActivity extends AppCompatActivity {
    private Button button;
    private FirebaseAuth mAuth;
    private AddressManager addressManager;
    private PaymentInfoManager paymentInfoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        mAuth = FirebaseAuth.getInstance();
        addressManager = new AddressManager(this);
        paymentInfoManager = new PaymentInfoManager(this);

        button = findViewById(R.id.button_out);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        // Clear user-specific data
        CartManager.clearCart();
        addressManager.clearData();
        paymentInfoManager.clearData();

        // Sign out from Firebase Auth
        mAuth.signOut();

        // Navigate to Login Screen
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        logout();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        logout();
        super.onPause();
    }
}
