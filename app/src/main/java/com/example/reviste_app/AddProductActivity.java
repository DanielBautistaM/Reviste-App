package com.example.reviste_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {
    private EditText etProductName, etProductPrice, etProductDescription, etSellerName;
    private ImageView imgProductImage;
    private Button btnUploadImage, btnAddProduct;
    private Button[] btnUploadAdditionalImages = new Button[4];
    private ImageView[] imgPreviewAdditionalImages = new ImageView[4];
    private Spinner spinnerRating;
    private Uri imageUri;
    private Uri[] additionalImageUris = new Uri[4];
    private FirebaseFirestore db;
    private boolean isCreatingProduct = false;
    private float rating = 0.0f;
    private String selectedRating;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductDescription = findViewById(R.id.etProductDescription);
        etSellerName = findViewById(R.id.etSellerName);
        imgProductImage = findViewById(R.id.imgProductImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        db = FirebaseFirestore.getInstance();
        spinnerRating = findViewById(R.id.spinnerRating);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ratings_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(adapter);

        spinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedRating = adapter.getItem(position).toString();
                rating = Float.parseFloat(selectedRating);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else {
                    selectImageFromCamera();
                }
            }
        });

        for (int i = 0; i < 4; i++) {
            int buttonId = getResources().getIdentifier("btnUploadAdditionalImage" + (i + 1), "id", getPackageName());
            btnUploadAdditionalImages[i] = findViewById(buttonId);

            int imgPreviewId = getResources().getIdentifier("imgPreview" + (i + 1), "id", getPackageName());
            imgPreviewAdditionalImages[i] = findViewById(imgPreviewId);

            final int index = i;
            btnUploadAdditionalImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAdditionalImageFromGallery(index);
                }
            });
        }

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCreatingProduct) {
                    isCreatingProduct = true;
                    btnAddProduct.setEnabled(false);

                    String name = etProductName.getText().toString();
                    Double price = Double.parseDouble(etProductPrice.getText().toString());
                    String description = etProductDescription.getText().toString();
                    String sellerName = etSellerName.getText().toString();

                    if (name.isEmpty() || description.isEmpty() || imageUri == null || selectedRating == null) {
                        isCreatingProduct = false;
                        btnAddProduct.setEnabled(true);
                    } else {
                        uploadProductData(name, price, description, rating, sellerName);
                    }
                }
            }
        });
    }

    private void selectImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectAdditionalImageFromGallery(final int index) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, index + 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProductImage.setImageURI(imageUri);
        } else if (requestCode >= 2 && requestCode <= 5 && resultCode == RESULT_OK && data != null) {
            int index = requestCode - 2;
            additionalImageUris[index] = data.getData();
            imgPreviewAdditionalImages[index].setImageURI(additionalImageUris[index]);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProductData(final String name, final Double price, final String description, final float rating, final String sellerName) {
        String imageName = "products/" + System.currentTimeMillis() + ".jpg";

        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imageName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                String imageUrl = downloadUrl.toString();

                                final List<String> additionalImages = new ArrayList<>();
                                uploadAdditionalImages(name, price, description, imageUrl, additionalImages, 0, sellerName);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isCreatingProduct = false;
                        btnAddProduct.setEnabled(true);
                        Toast.makeText(AddProductActivity.this, "Error al cargar la imagen principal", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAdditionalImages(final String name, final Double price, final String description, final String imageUrl, final List<String> additionalImages, final int index, final String sellerName) {
        if (index < 4 && additionalImageUris[index] != null) {
            String additionalImageName = "additional_images/" + System.currentTimeMillis() + ".jpg";
            final StorageReference additionalStorageRef = FirebaseStorage.getInstance().getReference().child(additionalImageName);

            additionalStorageRef.putFile(additionalImageUris[index])
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            additionalStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    String additionalImageUrl = downloadUrl.toString();
                                    additionalImages.add(additionalImageUrl);
                                    uploadAdditionalImages(name, price, description, imageUrl, additionalImages, index + 1, sellerName);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isCreatingProduct = false;
                            btnAddProduct.setEnabled(true);
                            Toast.makeText(AddProductActivity.this, "Error al cargar una imagen adicional", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Ahora, creamos un producto sin proporcionar el ID
            Product product = new Product(name, price, imageUrl, description, sellerName, additionalImages, rating);

            // Agregamos el producto a Firestore
            db.collection("Productos")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Actualizamos el ID después de agregar el producto
                            documentReference.update("id", documentReference.getId())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            isCreatingProduct = false;
                                            btnAddProduct.setEnabled(true);
                                            Toast.makeText(AddProductActivity.this, "Error al agregar el producto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isCreatingProduct = false;
                            btnAddProduct.setEnabled(true);
                            Toast.makeText(AddProductActivity.this, "Error al agregar el producto", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
