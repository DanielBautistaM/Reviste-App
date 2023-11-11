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
                Intent addProductIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(addProductIntent);
            }
        });

        ImageButton btnPlus = findViewById(R.id.btnplus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProductIntent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(addProductIntent);
            }
        });

        ImageButton btnCarrito = findViewById(R.id.btncarrito);
        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logOutIntent = new Intent(MainActivity.this, LogOutActivity.class);
                startActivity(logOutIntent);
            }
        });

        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(MainActivity.this, DetalleProductoActivity.class);
                intent.putExtra("product", product);
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
                        productList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error retrieving data: " + e.getMessage());
                    }
                });
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_price_filter, null);
        builder.setView(view);

        final EditText etMinPrice = view.findViewById(R.id.etMinPrice);
        final EditText etMaxPrice = view.findViewById(R.id.etMaxPrice);

        builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double minPrice = etMinPrice.getText().toString().isEmpty() ? Double.MIN_VALUE : Double.parseDouble(etMinPrice.getText().toString());
                double maxPrice = etMaxPrice.getText().toString().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(etMaxPrice.getText().toString());

                retrieveDataFromFirestore(minPrice, maxPrice);
            }
        });

        builder.setNegativeButton("Cancelar", null);

        Button btnClearFilter = view.findViewById(R.id.btnClearFilter);
        btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMinPrice.setText("");
                etMaxPrice.setText("");
                retrieveDataFromFirestore();
            }
        });

        builder.show();
    }

    private void retrieveDataFromFirestore(double minPrice, double maxPrice) {
        db.collection("Productos")
                .whereGreaterThanOrEqualTo("price", minPrice)
                .whereLessThanOrEqualTo("price", maxPrice)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error retrieving filtered data: " + e.getMessage());
                    }
                });
    }
}
