package com.tractor.rentatractorapp.User.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.Store_Adapter;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Store_Model;
import com.tractor.rentatractorapp.databinding.FragmentStoreBinding;

import java.util.ArrayList;

public class StoreFragment extends Fragment {

    FragmentStoreBinding binding;
    ArrayList<Store_Model> storeModelsArrayList;
    DatabaseReference databaseReference;
    Store_Adapter store_adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStoreBinding.inflate(getLayoutInflater(), container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference(Variables.Stores);
        storeModelsArrayList = new ArrayList<>();
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
                    store_adapter.setData(storeModelsArrayList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getStores();

        return binding.getRoot();
    }

    private void getStores() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    storeModelsArrayList.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Store_Model storeModels = dataSnapshot.getValue(Store_Model.class);
                        storeModelsArrayList.add(storeModels);
                    }
                    store_adapter = new Store_Adapter(storeModelsArrayList, getContext());
                    binding.recyclerView.setAdapter(store_adapter);
                    store_adapter.notifyDataSetChanged();
                    binding.storeSize.setText("All Stores ( "+storeModelsArrayList.size()+" )");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}