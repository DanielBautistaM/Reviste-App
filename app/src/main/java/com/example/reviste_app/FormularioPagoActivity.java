package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FormularioPagoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_pago);

        TextView btnTarjeta = findViewById(R.id.btn_tarjeta);
        btnTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarPago();
            }
        });
    }

    private void confirmarPago() {
        EditText etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta);
        EditText etMesVencimiento = findViewById(R.id.etMesVencimiento);
        EditText etAnioVencimiento = findViewById(R.id.etAnioVencimiento);
        EditText etCVV = findViewById(R.id.etCVV);

        String numeroTarjeta = etNumeroTarjeta.getText().toString();
        String mesVencimientoStr = etMesVencimiento.getText().toString();
        String anioVencimientoStr = etAnioVencimiento.getText().toString();
        String cvv = etCVV.getText().toString();

        if (!validatePaymentInfo(numeroTarjeta, mesVencimientoStr, anioVencimientoStr, cvv)) {
            return; // Early return if validation fails
        }

        int mesVencimiento = Integer.parseInt(mesVencimientoStr);
        int anioVencimiento = Integer.parseInt(anioVencimientoStr);

        PagoInfo pagoInfo = new PagoInfo(numeroTarjeta, mesVencimiento, anioVencimiento, cvv);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pagos").add(pagoInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(FormularioPagoActivity.this, "Información de pago guardada", Toast.LENGTH_SHORT).show();
                        String documentId = documentReference.getId();
                        navigateToCarritoActivity(documentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FormularioPagoActivity.this, "Error al guardar la información de pago", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validatePaymentInfo(String numeroTarjeta, String mesVencimiento, String anioVencimiento, String cvv) {
        if (numeroTarjeta.length() != 16) {
            EditText etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta);
            etNumeroTarjeta.setError("La tarjeta debe tener 16 caracteres");
            return false;
        }

        if (mesVencimiento.length() != 2) {
            EditText etMesVencimiento = findViewById(R.id.etMesVencimiento);
            etMesVencimiento.setError("El mes debe tener 2 caracteres");
            return false;
        }

        int mesInt = Integer.parseInt(mesVencimiento);
        if (mesInt < 1 || mesInt > 12) {
            EditText etMesVencimiento = findViewById(R.id.etMesVencimiento);
            etMesVencimiento.setError("El mes debe estar entre 1 y 12");
            return false;
        }

        if (anioVencimiento.length() != 4) {
            EditText etAnioVencimiento = findViewById(R.id.etAnioVencimiento);
            etAnioVencimiento.setError("El año debe tener 4 caracteres");
            return false;
        }

        int anioInt = Integer.parseInt(anioVencimiento);
        if (anioInt <= 2023) {
            EditText etAnioVencimiento = findViewById(R.id.etAnioVencimiento);
            etAnioVencimiento.setError("El año debe ser mayor a 2023");
            return false;
        }

        if (cvv.length() != 3) {
            EditText etCVV = findViewById(R.id.etCVV);
            etCVV.setError("El CVV debe tener 3 caracteres");
            return false;
        }

        return true;
    }


    private void navigateToCarritoActivity(String documentId) {
        Intent intent = new Intent(FormularioPagoActivity.this, CarritoActivity.class);
        intent.putExtra("DOCUMENT_ID", documentId);
        startActivity(intent);
        finish(); // Optionally close this activity
    }
}