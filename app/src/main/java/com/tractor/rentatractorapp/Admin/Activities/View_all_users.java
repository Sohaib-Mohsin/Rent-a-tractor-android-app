package com.tractor.rentatractorapp.Admin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.View_Users_Adapter;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.View_users;
import com.tractor.rentatractorapp.databinding.ActivityViewAllUsersBinding;

import java.util.ArrayList;

public class View_all_users extends AppCompatActivity {

    ActivityViewAllUsersBinding binding;
    ArrayList<View_users> view_usersArrayList;
    View_Users_Adapter view_users_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        view_usersArrayList = new ArrayList<>();

        binding.goBack.setOnClickListener(view -> finish());

        FirebaseDatabase.getInstance().getReference("All_Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    view_usersArrayList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        View_users view_users = dataSnapshot.getValue(View_users.class);
                        view_usersArrayList.add(view_users);
                    }
                    view_users_adapter = new View_Users_Adapter(View_all_users.this, view_usersArrayList);
                    binding.recyclerView.setAdapter(view_users_adapter);
                    view_users_adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}