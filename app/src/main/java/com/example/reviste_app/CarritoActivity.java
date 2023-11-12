package com.example.reviste_app;

// CarritoActivity.java
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CarritoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Obtener la lista de elementos del carrito desde CartManager
        List<CartItem> cartItems = CartManager.getCartItems();

        // Configurar el RecyclerView y el adaptador
        RecyclerView recyclerView = findViewById(R.id.recycler_view_carrito);
        CartItemAdapter adapter = new CartItemAdapter(cartItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
