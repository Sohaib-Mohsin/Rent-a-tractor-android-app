package com.tractor.rentatractorapp.Admin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.Requests_Adapter;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Requests_Models;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.databinding.ActivityRequestScreenBinding;

import java.util.ArrayList;

public class Request_Screen extends AppCompatActivity {

    ActivityRequestScreenBinding binding;
    Requests_Adapter requests_adapter;
    ArrayList<Requests_Models> requests_modelsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goBack.setOnClickListener(view -> finish());
        requests_modelsArrayList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    requests_modelsArrayList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Requests_Models requests_models = dataSnapshot.getValue(Requests_Models.class);
                        requests_modelsArrayList.add(requests_models);
                    }
                    requests_adapter = new Requests_Adapter(Request_Screen.this, requests_modelsArrayList);
                    binding.recyclerView.setAdapter(requests_adapter);
                    requests_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}