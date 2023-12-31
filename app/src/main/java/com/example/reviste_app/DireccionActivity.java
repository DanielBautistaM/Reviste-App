package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DireccionActivity extends AppCompatActivity {

    private EditText edtNombre, edtDepartamento, edtMunicipio, edtTelefono, edtDireccion, edtCodigoPostal;
    private TextView btnSaveAddress;
    private ImageButton backButton; // Agregado ImageButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion);

        edtNombre = findViewById(R.id.edtNombre);
        edtDepartamento = findViewById(R.id.edtDepartamento);
        edtMunicipio = findViewById(R.id.edtMunicipio);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtDireccion = findViewById(R.id.edtDireccion);
        edtCodigoPostal = findViewById(R.id.edtCodigoPostal);

        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarInformacionDeEnvio();
            }
        });

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a CarritoActivity
                Intent intent = new Intent(DireccionActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void guardarInformacionDeEnvio() {
        String nombre = edtNombre.getText().toString();
        String departamento = edtDepartamento.getText().toString();
        String municipio = edtMunicipio.getText().toString();
        String telefono = edtTelefono.getText().toString();
        String direccion = edtDireccion.getText().toString();
        String codigoPostal = edtCodigoPostal.getText().toString();

        // Crear un objeto de la clase Direccion
        Direccion direccionObj = new Direccion(nombre, departamento, municipio, telefono, direccion, codigoPostal);

        // Obtener la referencia de la colección en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference infoEnvioCollection = db.collection("informacionEnvios");

        // Usar un ID único generado automáticamente por Firestore
        infoEnvioCollection
                .add(direccionObj)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Toast.makeText(DireccionActivity.this, "Información de envío guardada exitosamente", Toast.LENGTH_SHORT).show();
                            // Obtener el ID del documento recién creado
                            String docId = task.getResult().getId();

                            // Enviar la información de dirección de vuelta a CarritoActivity
                            Intent intent = new Intent(DireccionActivity.this, CarritoActivity.class);
                            intent.putExtra("nombre", direccion);
                            intent.putExtra("departamento", departamento);
                            startActivity(intent);
                            finish(); // Cerrar la actividad después de guardar exitosamente
                        } else {
                            Toast.makeText(DireccionActivity.this, "Error al guardar la información de envío: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
