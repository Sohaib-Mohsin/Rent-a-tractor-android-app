package com.tractor.rentatractorapp.Admin.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tractor.rentatractorapp.Helpers.Display_Dialog;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Tractor_Models;
import com.tractor.rentatractorapp.databinding.ActivityAdminAddProductsScreenBinding;

import java.util.Objects;

public class Admin_Add_Products_Screen extends AppCompatActivity {

    private ActivityAdminAddProductsScreenBinding binding;
    private final String[] product_status = {"Select Product Status", Variables.New_Arrivals, Variables.Popular_Arrivals};
    private int Selected_product_status = 0;
    private Uri product_Images_1, product_Images_2;
    private Display_Dialog dialog;
    private int Image_No = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminAddProductsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.productStatus.setAdapter(new ArrayAdapter<>(Admin_Add_Products_Screen.this, android.R.layout.simple_list_item_1, product_status));

        binding.goBack.setOnClickListener(view -> finish());
        binding.addProductImage.setOnClickListener(view -> ImagePicker.with(Admin_Add_Products_Screen.this).crop(1.0f, 1.0f).compress(1024).maxResultSize(1080, 1080).start());

        binding.productStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Selected_product_status = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.addProduct.setOnClickListener(view -> {
            String Product_title = binding.productTitle.getText().toString(), Product_Description = binding.productDescription.getText().toString(),
                    Product_Hourly_Cost = binding.productHourlyCost.getText().toString();

            if (TextUtils.isEmpty(Product_title)) {
                binding.productTitle.setError("Required Field");
            } else if (TextUtils.isEmpty(Product_Description)) {
                binding.productDescription.setError("Required Field");
            } else if (TextUtils.isEmpty(Product_Hourly_Cost)) {
                binding.productHourlyCost.setError("Required Field");
            } else if (Selected_product_status == 0) {
                Toast.makeText(Admin_Add_Products_Screen.this, "Product Status is Required", Toast.LENGTH_SHORT).show();
            } else if (product_Images_1 == null || product_Images_2 == null) {
                Toast.makeText(Admin_Add_Products_Screen.this, "Both Product Images are Required", Toast.LENGTH_SHORT).show();
            } else {
                dialog = new Display_Dialog(Admin_Add_Products_Screen.this, "Adding " + Product_title);
                dialog.display_now();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(Variables.Tractor_Images).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                storageReference.child(String.valueOf(System.currentTimeMillis())).child("ImageNo1").putFile(product_Images_1).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri Product_Uri_1 = uriTask.getResult();

                    if (uriTask.isSuccessful()) {

                        storageReference.child(String.valueOf(System.currentTimeMillis())).child("ImageNo2").putFile(product_Images_2).addOnSuccessListener(taskSnapshot1 -> {
                                    Task<Uri> uriTask1 = taskSnapshot1.getStorage().getDownloadUrl();

                                    while (!uriTask1.isSuccessful()) ;
                                    Uri Product_Uri_2 = uriTask1.getResult();
                                    if (uriTask1.isSuccessful()) {

                                        String timeStamp = String.valueOf(System.currentTimeMillis());
                                        Tractor_Models tractorModels = new Tractor_Models(Product_title, Product_Description, getIntent().getStringExtra("title"), timeStamp, Product_Hourly_Cost, Product_Uri_1.toString(), Product_Uri_2.toString(), product_status[Selected_product_status], getIntent().getStringExtra("store_id"),"true");
                                        FirebaseDatabase.getInstance().getReference().child(Variables.Tractors)
                                                .child(timeStamp).setValue(tractorModels).addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        finish();
                                                        dialog.dismiss_now();
                                                        Toast.makeText(Admin_Add_Products_Screen.this, Product_title + " added to Store", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        dialog.dismiss_now();
                                                        Toast.makeText(Admin_Add_Products_Screen.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    dialog.dismiss_now();
                                    Toast.makeText(Admin_Add_Products_Screen.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    dialog.dismiss_now();
                    Toast.makeText(Admin_Add_Products_Screen.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (Image_No == 1) {
                product_Images_1 = data.getData();
                binding.productImage1.setImageURI(product_Images_1);
                Image_No = 2;
            } else if (Image_No == 2) {
                product_Images_2 = data.getData();
                binding.productImage2.setImageURI(product_Images_2);
                Image_No = 1;
                binding.addProductImage.setText("Change Image");
            }
        }
    }
}