package com.tractor.rentatractorapp.Mutual_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.Tractor_Adapters;
import com.tractor.rentatractorapp.Models.Tractor_Models;
import com.tractor.rentatractorapp.databinding.ActivityStoreScreenBinding;

import java.util.ArrayList;

public class Store_Screen extends AppCompatActivity {

    ActivityStoreScreenBinding binding;
    DatabaseReference databaseReference, Store_Images;
    String StoreID, Status, Product_ID;
    ArrayList<Tractor_Models> tractorModelsArrayList;
    Tractor_Adapters tractor_adapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoreScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startCode();
//        getStoreImages();
        getProducts();
     binding.back.setOnClickListener(view -> {
         finish();

     });

    }


    private void startCode() {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Store_Images = FirebaseDatabase.getInstance().getReference("Store_Images");
        StoreID = getIntent().getStringExtra("store_id");
        Status = getIntent().getStringExtra("status");
        binding.Store.setText(getIntent().getStringExtra("title"));
        binding.location.setText(getIntent().getStringExtra("location"));
        binding.desc.setText(getIntent().getStringExtra("desc"));
        try{
            Float rating = Float.parseFloat(getIntent().getStringExtra("rating"));
        binding.rating.setRating(rating);
        }
        catch (Exception ex){
            binding.rating.setRating(5.0f);

        }
        tractorModelsArrayList = new ArrayList<>();

    }

//    private void getStoreImages() {
//
//        Store_Images.child(StoreID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.exists()) {
//
//                    imageSlider_modelsArrayList.clear();
//
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        ImageSlider_models imageSlider_models = dataSnapshot.getValue(ImageSlider_models.class);
//                        imageSlider_modelsArrayList.add(imageSlider_models);
//                    }
//                    imageSlider_adapter = new ImageSlider_Adapter(Store_Screen.this, imageSlider_modelsArrayList);
//                    binding.imageSlider.setSliderAdapter(imageSlider_adapter);
//                    imageSlider_adapter.notifyDataSetChanged();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void getProducts() {

        databaseReference.child("Stores").child(StoreID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product_ID = snapshot.child("product_id").getValue().toString();

                    databaseReference.child(Status).child(Product_ID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {

                            for (DataSnapshot dataSnapshot1 : snapshot2.getChildren()) {
                                Tractor_Models tractorModels = dataSnapshot1.getValue(Tractor_Models.class);
                                tractorModelsArrayList.add(tractorModels);
                            }
                            tractor_adapters = new Tractor_Adapters(tractorModelsArrayList, Store_Screen.this);
                            binding.recyclerView.setAdapter(tractor_adapters);
                            tractor_adapters.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}