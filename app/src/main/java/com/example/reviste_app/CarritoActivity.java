package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView tvPaymentInfo;
    private AddressManager addressManager;
    private PaymentInfoManager paymentInfoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        addressManager = new AddressManager(this);
        paymentInfoManager = new PaymentInfoManager(this);
        cartItems = CartManager.getCartItems();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_carrito);
        adapter = new CartItemAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartTotalTextView = findViewById(R.id.cart_total_text);
        tvNombreDireccion = findViewById(R.id.tvNombreDireccion);
        tvDepartamento = findViewById(R.id.tvDepartamento);
        tvPaymentInfo = findViewById(R.id.tvPaymentInfo);
        setupButtons();
        displaySavedData();
        handleIntentExtras();
        updateCartTotalText();
    }

    private void setupButtons() {
        TextView btnCarrito = findViewById(R.id.btnCarrito);
        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddressAndPaymentInfoProvided()) {
                    double cartTotal = CartManager.getCartTotal();
                    Toast.makeText(CarritoActivity.this, "Pedido realizado. Total: $" + cartTotal, Toast.LENGTH_SHORT).show();
                    markProductsAsPurchased();
                    cartItems.clear();
                    adapter.notifyDataSetChanged();
                    updateCartTotalText();
                } else {
                    Toast.makeText(CarritoActivity.this, "Por favor, ingrese la dirección y la información de pago", Toast.LENGTH_SHORT).show();
                }
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
    }

    private boolean isAddressAndPaymentInfoProvided() {
        String addressName = addressManager.getAddressName();
        String paymentDocumentId = paymentInfoManager.getPaymentDocumentId();
        return !addressName.isEmpty() && !paymentDocumentId.isEmpty();
    }

    private void handleIntentExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("nombre") && intent.hasExtra("departamento")) {
                String nombre = intent.getStringExtra("nombre");
                String departamento = intent.getStringExtra("departamento");
                addressManager.saveAddress(nombre, departamento);
                displayAddress();
            }

            if (intent.hasExtra("DOCUMENT_ID")) {
                String documentId = intent.getStringExtra("DOCUMENT_ID");
                paymentInfoManager.savePaymentDocumentId(documentId);
                fetchPaymentInfo(documentId);
            }
        }
    }

    private void displaySavedData() {
        displayAddress();
        String documentId = paymentInfoManager.getPaymentDocumentId();
        if (!documentId.isEmpty()) {
            fetchPaymentInfo(documentId);
        }
    }

    private void displayAddress() {
        String nombre = addressManager.getAddressName();
        String departamento = addressManager.getAddressDepartment();
        if (!nombre.isEmpty() && !departamento.isEmpty()) {
            tvNombreDireccion.setText(nombre);
            tvDepartamento.setText(departamento);
        }
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

    private void markProductsAsPurchased() {
        for (CartItem cartItem : cartItems) {
            String productId = cartItem.getProductId();
            updateProductPurchasedStatusInFirestore(productId);
        }
    }

    private void updateProductPurchasedStatusInFirestore(String productId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Productos").document(productId)
                .update("isPurchased", true);
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
                });
    }

    private void displayPaymentInfo(PagoInfo pagoInfo) {
        if (pagoInfo != null) {
            String paymentDetails = pagoInfo.getNumeroTarjeta() +
                    "\nVence: " + pagoInfo.getMesVencimiento() + "/" + pagoInfo.getAnioVencimiento() +
                    "\nCVV: " + pagoInfo.getCvv();
            tvPaymentInfo.setText(paymentDetails);
        }
    }
}