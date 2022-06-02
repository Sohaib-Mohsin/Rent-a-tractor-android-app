package com.tractor.rentatractorapp.Admin.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.Admin_Tractor_Adapter;
import com.tractor.rentatractorapp.Helpers.Current_Login_Status;
import com.tractor.rentatractorapp.Helpers.Display_Dialog;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Store_Model;
import com.tractor.rentatractorapp.Models.Tractor_Models;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.databinding.ActivityAdminProductsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Admin_Products_Screen extends AppCompatActivity {

    private ActivityAdminProductsBinding binding;
    private String StoreID, Store_Status;
    private ArrayList<Tractor_Models> tractorModelsArrayList;
    private Admin_Tractor_Adapter tractor_adapters;
    Display_Dialog display_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tractorModelsArrayList = new ArrayList<>();
        Store_Model store_model = (Store_Model) getIntent().getSerializableExtra("Store_Model");

        StoreID = store_model.getTimeStamp();
        binding.Store.setText(store_model.getTitle());
        binding.location.setText(store_model.getLocation());
        binding.desc.setText(store_model.getDescription());

        try {
            Glide.with(Admin_Products_Screen.this).load(store_model.getImagesUrl()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    binding.itemProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(binding.storeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.goBack.setOnClickListener(view -> finish());

        if (Current_Login_Status.get_login(Admin_Products_Screen.this).equals("Admin")) {
            binding.createProducts.setVisibility(View.VISIBLE);
        }
        else if(Current_Login_Status.get_login(Admin_Products_Screen.this).equals("User")){
            binding.createProducts.setVisibility(View.INVISIBLE);
        }
        binding.storeStatus.setOnClickListener(view -> {

            if (Current_Login_Status.get_login(Admin_Products_Screen.this).equals("Admin")) {
                binding.createProducts.setVisibility(View.VISIBLE);
                if (Store_Status.equals("Opened")) {
                    display_dialog = new Display_Dialog(Admin_Products_Screen.this, "Closing Store...");
                    display_dialog.display_now();
                    Store_Status = "Closed";
                    binding.storeStatus.setImageResource(R.drawable.ic_store_closed);
                } else if (Store_Status.equals("Closed")) {
                    display_dialog = new Display_Dialog(Admin_Products_Screen.this, "Opening Store...");
                    display_dialog.display_now();
                    Store_Status = "Opened";
                    binding.storeStatus.setImageResource(R.drawable.ic_store_opened);
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("status", Store_Status);

                FirebaseDatabase.getInstance().getReference(Variables.Stores).child(StoreID).updateChildren(map)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Admin_Products_Screen.this, "Store " + Store_Status, Toast.LENGTH_SHORT).show();
                                display_dialog.dismiss_now();
                                finish();
                            } else {
                                display_dialog.dismiss_now();
                                Toast.makeText(Admin_Products_Screen.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        binding.createProducts.setOnClickListener(view -> startActivity(new Intent(Admin_Products_Screen.this, Admin_Add_Products_Screen.class).putExtra("title", store_model.getTitle()).putExtra("store_id", StoreID)));

        getStore_Status();
        getTractors();
    }

    private void getStore_Status() {
        FirebaseDatabase.getInstance().getReference(Variables.Stores).child(StoreID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Store_Status = snapshot.child("status").getValue().toString();
                    if (Store_Status.equals("Opened")) {
                        binding.storeStatus.setImageResource(R.drawable.ic_store_opened);
                    } else if (Store_Status.equals("Closed")) {
                        binding.storeStatus.setImageResource(R.drawable.ic_store_closed);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getTractors() {
        FirebaseDatabase.getInstance().getReference().child(Variables.Tractors).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                tractorModelsArrayList.clear();
                for (DataSnapshot dataSnapshot1 : snapshot2.getChildren()) {
                    if (dataSnapshot1.child("store_ID").getValue().toString().equals(StoreID)) {
                        Tractor_Models tractorModels = dataSnapshot1.getValue(Tractor_Models.class);
                        tractorModelsArrayList.add(tractorModels);
                    }
                }
                tractor_adapters = new Admin_Tractor_Adapter(tractorModelsArrayList, Admin_Products_Screen.this);
                binding.recyclerView.setAdapter(tractor_adapters);
                tractor_adapters.notifyDataSetChanged();
                binding.availableTractor.setText("Available Tractors ( " + tractorModelsArrayList.size() + " )");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}