package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class CarritoActivity extends AppCompatActivity implements CartItemAdapter.OnRemoveItemClickListener {

    private List<CartItem> cartItems;
    private TextView cartTotalTextView;
    private CartItemAdapter adapter;

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

        Button checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                // Abre la nueva actividad de dirección
                Intent intent = new Intent(CarritoActivity.this, DireccionActivity.class);
                startActivity(intent);
            }
        });

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
}