package com.example.reviste_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        Button btnConfirmarPago = findViewById(R.id.btnConfirmarPago);
        btnConfirmarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarPago();
            }
        });
    }

    private void confirmarPago() {
        // Obtén referencias a los EditTexts
        EditText etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta);
        EditText etMesVencimiento = findViewById(R.id.etMesVencimiento);
        EditText etAnioVencimiento = findViewById(R.id.etAnioVencimiento);
        EditText etCVV = findViewById(R.id.etCVV);

        // Obtén los valores ingresados por el usuario
        String numeroTarjeta = etNumeroTarjeta.getText().toString();
        String mesVencimientoStr = etMesVencimiento.getText().toString();
        String anioVencimientoStr = etAnioVencimiento.getText().toString();
        String cvv = etCVV.getText().toString();

        // Validar la información de pago (puedes agregar más validaciones según sea necesario)

        // Convertir los valores de mes y año a enteros
        int mesVencimiento = Integer.parseInt(mesVencimientoStr);
        int anioVencimiento = Integer.parseInt(anioVencimientoStr);

        // Crear un objeto para la información de pago
        PagoInfo pagoInfo = new PagoInfo(numeroTarjeta, mesVencimiento, anioVencimiento, cvv);

        // Guardar la información en Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pagos").add(pagoInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Éxito al guardar en Firebase
                        Toast.makeText(FormularioPagoActivity.this, "Información de pago guardada", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad después de un pago exitoso
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al guardar en Firebase
                        Toast.makeText(FormularioPagoActivity.this, "Error al guardar la información de pago", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
