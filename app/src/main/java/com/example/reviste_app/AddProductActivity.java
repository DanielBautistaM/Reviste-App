package com.example.reviste_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
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

    private static final int REQUEST_IMAGE_CAPTURE = 101;

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
                showImagePickerDialog();
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
                    showAdditionalImagePickerDialog(index);
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

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        String[] options = {"Gallery", "Camera"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    selectImageFromGallery();
                } else if (which == 1) {
                    dispatchTakePictureIntent();
                }
            }
        });
        builder.show();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProductImage.setImageURI(imageUri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageUri = getImageUri(getApplicationContext(), imageBitmap);
            imgProductImage.setImageURI(imageUri);
        } else if (requestCode >= 2 && requestCode <= 5 && resultCode == RESULT_OK && data != null) {
            int index = requestCode - 2;
            additionalImageUris[index] = data.getData();
            imgPreviewAdditionalImages[index].setImageURI(additionalImageUris[index]);
        } else if (requestCode >= 102 && requestCode <= 105 && resultCode == RESULT_OK && data != null) {
            int index = requestCode - 102;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            additionalImageUris[index] = getImageUri(getApplicationContext(), imageBitmap);
            imgPreviewAdditionalImages[index].setImageURI(additionalImageUris[index]);
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void showAdditionalImagePickerDialog(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        String[] options = {"Gallery", "Camera"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    selectAdditionalImageFromGallery(index);
                } else if (which == 1) {
                    dispatchTakeAdditionalPictureIntent(index);
                }
            }
        });
        builder.show();
    }

    private void selectAdditionalImageFromGallery(final int index) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, index + 2);
    }

    private void dispatchTakeAdditionalPictureIntent(final int index) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, index + 102);
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
                    }
                });
    }

    private void uploadAdditionalImages(final String name, final Double price, final String description, final String imageUrl, final List<String> additionalImages, final int index, final String sellerName) {
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
                        }
                    });
        } else {
            Product product = new Product(name, price, imageUrl, description, sellerName, additionalImages, rating);

            db.collection("Productos")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
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
    }
}
