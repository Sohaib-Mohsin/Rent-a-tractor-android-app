package com.tractor.rentatractorapp.User.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tractor.rentatractorapp.Models.View_users;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.databinding.ActivityUpdateProfileImageBinding;

import java.util.HashMap;

public class Update_Profile_Image extends AppCompatActivity {

    ActivityUpdateProfileImageBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference("All_Users");
        storageReference = FirebaseStorage.getInstance().getReference("User_Images");

        binding.updateNow.setText(Html.fromHtml("<u>Update Profile Image</u>"));
        binding.updateNow.setOnClickListener(view -> ImagePicker.with(Update_Profile_Image.this).crop(1.0f, 1.0f).compress(1024).maxResultSize(1080, 1080).start());
        binding.goBack.setOnClickListener(view -> finish());
        getImage();
    }

    private void getImage() {
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    View_users view_users = snapshot.getValue(View_users.class);

                    binding.profileEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, 3).concat("***").concat("@gmail.com"));
                    binding.profilename.setText(view_users.getName());
                    binding.profilephone.setText("+92********".concat(view_users.getPhone().substring(11, 13)));
                    binding.profileLocation.setText(Html.fromHtml("<u>"+view_users.getLocation()+"</u>"));

                    Glide.with(Update_Profile_Image.this).load(view_users.getImage()).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    binding.itemProgressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            binding.profileImage.setImageURI(uri);
            binding.itemProgressBar.setVisibility(View.VISIBLE);

            StorageReference Image_Reference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Image_Reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful()) ;
                            Uri Uri_2 = uriTask.getResult();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("image", Uri_2.toString());

                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Update_Profile_Image.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();
                                                binding.itemProgressBar.setVisibility(View.INVISIBLE);
                                            } else {
                                                Toast.makeText(Update_Profile_Image.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Update_Profile_Image.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
}