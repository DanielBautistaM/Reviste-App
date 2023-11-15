package com.example.reviste_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Double minPriceFilter = null;
    private Double maxPriceFilter = null;
    private Float ratingFilter = null;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        retrieveDataFromFirestore();
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
                Intent logOutIntent = new Intent(MainActivity.this, CarritoActivity.class);
                startActivity(logOutIntent);
            }
        });
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
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
                if (!product.isPurchased()) {
                    Intent intent = new Intent(MainActivity.this, DetalleProductoActivity.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
    }

    private void retrieveDataFromFirestore() {
        db.collection("Productos")
                .whereEqualTo("isPurchased", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            if (passesFilters(product)) {
                                productList.add(product);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private boolean passesFilters(Product product) {
        boolean pricePassesFilter = (minPriceFilter == null || product.getPrice() >= minPriceFilter) &&
                (maxPriceFilter == null || product.getPrice() <= maxPriceFilter);

        boolean ratingPassesFilter = ratingFilter == null || Math.abs(product.getRatings() - ratingFilter) < 0.001;

        return pricePassesFilter && ratingPassesFilter;
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_price_filter, null);
        builder.setView(view);

        final CheckBox chkEnablePriceFilter = view.findViewById(R.id.chkEnablePriceFilter);
        final EditText etMinPrice = view.findViewById(R.id.etMinPrice);
        final EditText etMaxPrice = view.findViewById(R.id.etMaxPrice);

        final CheckBox chkEnableRatingFilter = view.findViewById(R.id.chkEnableRatingFilter);
        Spinner spinnerRating = view.findViewById(R.id.spinnerRating);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ratings_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(adapter);

        Button btnClearFilter = view.findViewById(R.id.btnClearFilter);

        builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chkEnablePriceFilter.isChecked()) {
                    minPriceFilter = etMinPrice.getText().toString().isEmpty() ? null : Double.parseDouble(etMinPrice.getText().toString());
                    maxPriceFilter = etMaxPrice.getText().toString().isEmpty() ? null : Double.parseDouble(etMaxPrice.getText().toString());
                } else {
                    minPriceFilter = null;
                    maxPriceFilter = null;
                }

                if (chkEnableRatingFilter.isChecked()) {
                    ratingFilter = spinnerRating.getSelectedItem().toString().equals("Sin filtro") ? null : Float.parseFloat(spinnerRating.getSelectedItem().toString());
                } else {
                    ratingFilter = null;
                }

                updateFilteredProducts();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMinPrice.setText("");
                etMaxPrice.setText("");
                spinnerRating.setSelection(0);
                chkEnablePriceFilter.setChecked(false);
                chkEnableRatingFilter.setChecked(false);
                minPriceFilter = null;
                maxPriceFilter = null;
                ratingFilter = null;
                updateFilteredProducts();
            }
        });

        builder.show();
    }

    private void updateFilteredProducts() {
        retrieveDataFromFirestore();
    }
}