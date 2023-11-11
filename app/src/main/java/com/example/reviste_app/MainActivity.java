package com.example.reviste_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reviste_app.Product;
import com.example.reviste_app.ProductAdapter;
import com.example.reviste_app.R;
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

        // Configura OnClickListener para el botón de filtrar
        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
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
                        productList.clear(); // Clear previous data
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

    // Método para mostrar el cuadro de diálogo de filtro
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_price_filter, null);
        builder.setView(view);

        final EditText etMinPrice = view.findViewById(R.id.etMinPrice);
        final EditText etMaxPrice = view.findViewById(R.id.etMaxPrice);

        builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtén los valores ingresados por el usuario
                double minPrice = Double.parseDouble(etMinPrice.getText().toString());
                double maxPrice = Double.parseDouble(etMaxPrice.getText().toString());

                // Aplica el filtro
                retrieveDataFromFirestore(minPrice, maxPrice);
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }

    // Overloaded method for retrieving filtered data
    private void retrieveDataFromFirestore(double minPrice, double maxPrice) {
        db.collection("Productos")
                .whereGreaterThanOrEqualTo("price", minPrice)
                .whereLessThanOrEqualTo("price", maxPrice)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productList.clear(); // Clear previous data
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
                        Log.e("Firestore", "Error retrieving filtered data: " + e.getMessage());
                        // Handle errors
                    }
                });
    }
}
