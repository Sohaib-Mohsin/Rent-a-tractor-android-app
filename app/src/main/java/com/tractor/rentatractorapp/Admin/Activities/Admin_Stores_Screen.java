package com.tractor.rentatractorapp.Admin.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.Store_Adapter;
import com.tractor.rentatractorapp.Helpers.Current_Login_Status;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Store_Model;
import com.tractor.rentatractorapp.Models.View_users;
import com.tractor.rentatractorapp.Mutual_Activities.Login_Screen;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.databinding.ActivityAdminStoresScreenBinding;
import com.tractor.rentatractorapp.databinding.CustomLogoutDialogBinding;

import java.util.ArrayList;
import java.util.Objects;

public class Admin_Stores_Screen extends AppCompatActivity {

    private ActivityAdminStoresScreenBinding binding;
    private FirebaseAuth firebaseAuth;
    private Store_Model store_model;
    private ArrayList<Store_Model> storeModelsArrayList;
    private Store_Adapter store_adapter;
    private boolean isRequested = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminStoresScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        storeModelsArrayList = new ArrayList<>();

        binding.viewAllUsers.setOnClickListener(view -> startActivity(new Intent(Admin_Stores_Screen.this, View_all_users.class)));
        binding.notification.setOnClickListener(view -> startActivity(new Intent(Admin_Stores_Screen.this, Request_Screen.class)));

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s != null && s.length() > 0) {
                    try {
                        store_adapter.getFilter().filter(s);

                    } catch (Exception ex) {

                    }

                } else {
                    try {
                        store_adapter.setData(storeModelsArrayList);

                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.logout.setOnClickListener(view -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(Admin_Stores_Screen.this, R.style.material);
            alert.setCancelable(false);
            CustomLogoutDialogBinding customLogoutDialogBinding = CustomLogoutDialogBinding.inflate(getLayoutInflater());
            alert.setView(customLogoutDialogBinding.getRoot());
            Dialog dialog = alert.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().getAttributes().gravity = Gravity.TOP | Gravity.CENTER;

            customLogoutDialogBinding.keepUsing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            customLogoutDialogBinding.logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseAuth.signOut();
                    Current_Login_Status.Save_Login(Admin_Stores_Screen.this, "none");
                    startActivity(new Intent(Admin_Stores_Screen.this, Login_Screen.class));
                    finish();
                }
            });

            dialog.show();

        });

        binding.createStore.setOnClickListener(view -> startActivity(new Intent(Admin_Stores_Screen.this, Admin_Add_Stores.class)));

        getStores();
        CheckRequests();

    }

    private void CheckRequests() {
        FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (Objects.requireNonNull(dataSnapshot.child("request_status").getValue()).toString().equals("requested")) {
                            binding.notification.setImageResource(R.drawable.ic_requests);
                            break;
                        }
                        else {
                            binding.notification.setImageResource(R.drawable.ic_baseline_notifications_none_24);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStores() {
        FirebaseDatabase.getInstance().getReference().child(Variables.Stores).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    storeModelsArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Store_Model store_model2 = dataSnapshot.getValue(Store_Model.class);
                        storeModelsArrayList.add(store_model2);
                    }
                    store_adapter = new Store_Adapter(storeModelsArrayList, Admin_Stores_Screen.this);
                    binding.recyclerView.setAdapter(store_adapter);
                    store_adapter.notifyDataSetChanged();
                    binding.availableStores.setText("Available Stores ( " + storeModelsArrayList.size() + " )");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckRequests();
    }
}