package com.example.reviste_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    private EditText etProductName, etProductPrice, etProductDescription;
    private ImageView imgProductImage;
    private Button btnUploadImage, btnAddProduct;
    private Button[] btnUploadAdditionalImages = new Button[4];
    private Uri imageUri;
    private Uri[] additionalImageUris = new Uri[4];
    private FirebaseFirestore db;
    private boolean isCreatingProduct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductDescription = findViewById(R.id.etProductDescription);
        imgProductImage = findViewById(R.id.imgProductImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        db = FirebaseFirestore.getInstance();

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        // Configurar controladores de eventos para cargar imágenes adicionales
        for (int i = 0; i < 4; i++) {
            int buttonId = getResources().getIdentifier("btnUploadAdditionalImage" + (i + 1), "id", getPackageName());
            btnUploadAdditionalImages[i] = findViewById(buttonId);
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
                    String price = etProductPrice.getText().toString();
                    String description = etProductDescription.getText().toString();

                    if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUri == null) {
                        isCreatingProduct = false;
                        btnAddProduct.setEnabled(true);
                    } else {
                        uploadProductData(name, price, description);
                    }
                }
            }
        });
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void selectAdditionalImageFromGallery(final int index) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, index + 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProductImage.setImageURI(imageUri);
        } else if (requestCode >= 2 && requestCode <= 5 && resultCode == RESULT_OK && data != null) {
            int index = requestCode - 2;
            additionalImageUris[index] = data.getData();
        }
    }

    private void uploadProductData(final String name, final String price, final String description) {
        String imageName = "products/" + System.currentTimeMillis() + ".jpg";

        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imageName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                final String imageUrl = downloadUrl.toString(); // Definir la variable imageUrl

                                // Crea una lista para almacenar las URLs de las imágenes adicionales
                                final List<String> additionalImages = new ArrayList<>();
                                uploadAdditionalImages(name, price, description, imageUrl, additionalImages, 0); // Comenzar la carga de imágenes adicionales
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isCreatingProduct = false;
                        btnAddProduct.setEnabled(true);
                    }
                });
    }

    private void uploadAdditionalImages(final String name, final String price, final String description, final String imageUrl, final List<String> additionalImages, final int index) {
        if (index < 4 && additionalImageUris[index] != null) {
            String imageName = "additional_images/" + System.currentTimeMillis() + ".jpg";
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imageName);

            storageRef.putFile(additionalImageUris[index])
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    String additionalImageUrl = downloadUrl.toString();
                                    additionalImages.add(additionalImageUrl);
                                    uploadAdditionalImages(name, price, description, imageUrl, additionalImages, index + 1);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isCreatingProduct = false;
                            btnAddProduct.setEnabled(true);
                        }
                    });
        } else {
            Product product = new Product("", name, price, imageUrl, description, "", additionalImages);

            db.collection("Productos")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
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
                        }
                    });
        }
    }
}
