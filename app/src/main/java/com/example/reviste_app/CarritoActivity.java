package com.example.reviste_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;

public class CarritoActivity extends AppCompatActivity implements CartItemAdapter.OnRemoveItemClickListener {

    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Obtener la lista de elementos del carrito desde CartManager
        cartItems = CartManager.getCartItems();

        // Configurar el RecyclerView y el adaptador
        RecyclerView recyclerView = findViewById(R.id.recycler_view_carrito);
        final CartItemAdapter adapter = new CartItemAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementa aquí la lógica para realizar el pedido
                Toast.makeText(CarritoActivity.this, "Pedido realizado", Toast.LENGTH_SHORT).show();
                // Puedes limpiar el carrito después de realizar el pedido
                cartItems.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRemoveItemClick(int position) {
        // Manejar la eliminación del elemento del carrito en la posición dada
        final CartItem removedItem = cartItems.remove(position);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_carrito);
        recyclerView.getAdapter().notifyItemRemoved(position);
        recyclerView.getAdapter().notifyItemRangeChanged(position, cartItems.size());

        // Puedes almacenar el elemento eliminado temporalmente en CartManager
        CartManager.setLastRemovedItem(removedItem);

        // Muestra un mensaje indicando que el producto ha sido eliminado
        Toast.makeText(CarritoActivity.this, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show();
    }
}
