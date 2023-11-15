package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CarritoActivity extends AppCompatActivity implements CartItemAdapter.OnRemoveItemClickListener {

    private List<CartItem> cartItems;
    private TextView cartTotalTextView;
    private CartItemAdapter adapter;
    private TextView tvNombreDireccion;
    private TextView tvDepartamento;
    private TextView tvPaymentInfo; // TextView to display payment information

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        cartItems = CartManager.getCartItems();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_carrito);
        adapter = new CartItemAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartTotalTextView = findViewById(R.id.cart_total_text);
        tvNombreDireccion = findViewById(R.id.tvNombreDireccion);
        tvDepartamento = findViewById(R.id.tvDepartamento);
        tvPaymentInfo = findViewById(R.id.tvPaymentInfo); // Assigning the TextView for payment info


        TextView btnCarrito = findViewById(R.id.btnCarrito);

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CarritoActivity", "Botón 'Realizar Pedido' clickeado");
                double cartTotal = CartManager.getCartTotal();
                Toast.makeText(CarritoActivity.this, "Pedido realizado. Total: $" + cartTotal, Toast.LENGTH_SHORT).show();
                cartItems.clear();
                adapter.notifyDataSetChanged();
                updateCartTotalText();
            }
        });

        ImageButton btnAddAddress = findViewById(R.id.btnAddAddress);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, DireccionActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnPagar = findViewById(R.id.btnPagar);
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, FormularioPagoActivity.class);
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Receiving and displaying the payment info


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DOCUMENT_ID")) {
            String documentId = intent.getStringExtra("DOCUMENT_ID");
            fetchPaymentInfo(documentId);
        }

        updateCartTotalText();
    }

    @Override
    public void onRemoveItemClick(int position) {
        final CartItem removedItem = cartItems.remove(position);
        CartManager.removeFromCart(removedItem);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, cartItems.size());
        CartManager.setLastRemovedItem(removedItem);
        Toast.makeText(CarritoActivity.this, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show();
        updateCartTotalText();
    }

    private void updateCartTotalText() {
        double cartTotal = CartManager.getCartTotal();
        cartTotalTextView.setText("$" + cartTotal);
    }

    private void fetchPaymentInfo(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pagos").document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            PagoInfo pagoInfo = documentSnapshot.toObject(PagoInfo.class);
                            displayPaymentInfo(pagoInfo);
                        } else {
                            Toast.makeText(CarritoActivity.this, "No se encontró la información de pago", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CarritoActivity.this, "Error al obtener la información de pago", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayPaymentInfo(PagoInfo pagoInfo) {
        if (pagoInfo != null) {
            String paymentDetails = "Tarjeta: " + pagoInfo.getNumeroTarjeta() +
                    "\nVence: " + pagoInfo.getMesVencimiento() + "/" + pagoInfo.getAnioVencimiento() +
                    "\nCVV: " + pagoInfo.getCvv();
            tvPaymentInfo.setText(paymentDetails);
        }
    }
}