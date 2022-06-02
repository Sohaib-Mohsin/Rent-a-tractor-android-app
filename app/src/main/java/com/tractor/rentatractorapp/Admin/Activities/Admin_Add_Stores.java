package com.tractor.rentatractorapp.Admin.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tractor.rentatractorapp.Helpers.Display_Dialog;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Store_Model;
import com.tractor.rentatractorapp.User.Activities.Maps_Screen;
import com.tractor.rentatractorapp.databinding.ActivityAdminAddStoresBinding;

import java.util.Objects;

public class Admin_Add_Stores extends AppCompatActivity {

    private ActivityAdminAddStoresBinding binding;
    private String Store_Images_Url;
    private Display_Dialog dialog;
    private Uri imageUriCrop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddStoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addStoreImage.setOnClickListener(view -> ImagePicker.with(Admin_Add_Stores.this).crop(1.0f, 1.0f).compress(1024).maxResultSize(1080, 1080).start());
        binding.goBack.setOnClickListener(view -> finish());
        binding.storeLocation.setOnClickListener(view -> startActivityForResult(new Intent(Admin_Add_Stores.this, Maps_Screen.class), 786));
        binding.createStore.setOnClickListener(view -> {
            String Store_title = binding.storeTitle.getText().toString(), Store_Description = binding.storeDescription.getText().toString(),
                    Store_Location = binding.storeLocation.getText().toString();

            if (TextUtils.isEmpty(Store_title)) {
                binding.storeTitle.setError("Required Field");
            } else if (TextUtils.isEmpty(Store_Description)) {
                binding.storeDescription.setError("Required Field");
            } else if (TextUtils.isEmpty(Store_Location)) {
                binding.storeLocation.setError("Required Field");
            } else if (imageUriCrop == null) {
                Toast.makeText(Admin_Add_Stores.this, "Store Image is required", Toast.LENGTH_SHORT).show();
            } else {
                dialog = new Display_Dialog(Admin_Add_Stores.this, "Adding "+Store_title);
                dialog.display_now();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(Variables.Store_Images).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                storageReference.child(String.valueOf(System.currentTimeMillis())).putFile(imageUriCrop).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri Store_Image = uriTask.getResult();

                        if (uriTask.isSuccessful()) {
                            String timeStamp = String.valueOf(System.currentTimeMillis());
                            Store_Model store_model = new Store_Model(Store_title, Store_Description, timeStamp, Store_Image.toString(), Store_Location, 0.0f, "Opened");
                            FirebaseDatabase.getInstance().getReference(Variables.Stores).child(timeStamp).setValue(store_model).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Admin_Add_Stores.this, "Store Created", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss_now();
                                    startActivity(new Intent(Admin_Add_Stores.this, Admin_Stores_Screen.class));
                                    finish();
                                } else {
                                    dialog.dismiss_now();
                                    Toast.makeText(Admin_Add_Stores.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss_now();
                        Toast.makeText(Admin_Add_Stores.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 786 && resultCode == RESULT_OK && data!=null){
            String location = data.getStringExtra("LOCATION");
            binding.storeLocation.setText(location);
        }

        if (resultCode == RESULT_OK && data != null) {
            imageUriCrop = data.getData();
            binding.storeImage.setImageURI(imageUriCrop);
            binding.addStoreImage.setText("Change Image");
        }


    }

}