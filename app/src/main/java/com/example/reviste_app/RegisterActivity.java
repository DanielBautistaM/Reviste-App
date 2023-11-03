package com.example.reviste_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, usernameEditText, fullnameEditText;
    TextView buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    ImageButton imageButton;
    DatePicker birthdatePicker;

    @Override
    public void onStart() {
        super.onStart();
        // Check if the user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        usernameEditText = findViewById(R.id.username);
        fullnameEditText = findViewById(R.id.fullname);
        birthdatePicker = findViewById(R.id.birthdatePicker);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBarLog);
        imageButton = findViewById(R.id.btn_back);

        // Obtén el año actual
        Calendar calendar = Calendar.getInstance();
        int yearActual = calendar.get(Calendar.YEAR);

        // Asigna el año actual como el año máximo del DatePicker
        birthdatePicker.setMaxDate(calendar.getTimeInMillis());

        // Configura el año mínimo si es necesario
        // Por ejemplo, para limitar la fecha de nacimiento a hace 100 años
        calendar.add(Calendar.YEAR, -100);
        birthdatePicker.setMinDate(calendar.getTimeInMillis());

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, username, fullname;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                username = usernameEditText.getText().toString();
                fullname = fullnameEditText.getText().toString();

                // Obtén la fecha de nacimiento del DatePicker
                int day = birthdatePicker.getDayOfMonth();
                int month = birthdatePicker.getMonth() + 1; // El mes se indexa desde 0
                int year = birthdatePicker.getYear();

                // Formatea la fecha en el formato deseado (puedes personalizarlo)
                String birthdate = day + "/" + month + "/" + year;

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(fullname) || TextUtils.isEmpty(birthdate)) {
                    Toast.makeText(RegisterActivity.this, "Complete todos los campos", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // Registro exitoso, ahora guardamos los datos adicionales en Firebase Realtime Database.
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();
                                        User userData = new User(username, email, fullname, birthdate);

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                        databaseReference.child(userId).setValue(userData);

                                        Toast.makeText(RegisterActivity.this, "Cuenta creada :D", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Manejar el caso en el que no se pueda obtener el usuario actual.
                                        Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Si la autenticación falla, muestra un mensaje al usuario.
                                    Toast.makeText(RegisterActivity.this, "Fallo Autenticación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
