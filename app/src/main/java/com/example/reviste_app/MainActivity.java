package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reviste_app.AddProductActivity;
import com.example.reviste_app.DetalleProductoActivity;
import com.example.reviste_app.LogOutActivity;
import com.example.reviste_app.Product;
import com.example.reviste_app.ProductAdapter;
import com.example.reviste_app.R;
import com.example.reviste_app.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        retrieveDataFromFirestore();

        // Configura OnClickListener para el botón de inicio (Home)
        ImageButton btnHome = findViewById(R.id.btnhome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega aquí la acción que deseas para el botón de inicio (Home)
                // Por ejemplo, puedes abrir una actividad principal o realizar alguna otra acción.
                Intent addProductIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(addProductIntent);
            }
        });

        // Configura OnClickListener para el botón de agregar producto (Plus)
        ImageButton btnPlus = findViewById(R.id.btnplus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega aquí la acción que deseas para el botón de agregar producto (Plus)
                // Por ejemplo, puedes abrir AddProductActivity.
                Intent addProductIntent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(addProductIntent);
            }
        });

        // Configura OnClickListener para el botón de carrito
        ImageButton btnCarrito = findViewById(R.id.btncarrito);
        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega aquí la acción que deseas para el botón de carrito
                // Por ejemplo, puedes abrir LogOutActivity.
                Intent logOutIntent = new Intent(MainActivity.this, LogOutActivity.class);
                startActivity(logOutIntent);
            }
        });

        // Handle item click events in the RecyclerView
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Retrieve the clicked product
                Product product = productList.get(position);

                // Create an intent to display the product details in DetalleProductoActivity
                Intent intent = new Intent(MainActivity.this, DetalleProductoActivity.class);

                // Pass the product details as extras in the intent
                intent.putExtra("product", product);

                // Start the DetalleProductoActivity
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // Handle long click events if needed
            }
        }));
    }

    private void retrieveDataFromFirestore() {
        db.collection("Productos")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error retrieving data: " + e.getMessage());
                        // Handle errors
                    }
                });
    }
}
