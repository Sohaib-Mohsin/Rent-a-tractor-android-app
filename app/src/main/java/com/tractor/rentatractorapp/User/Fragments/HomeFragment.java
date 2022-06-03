package com.tractor.rentatractorapp.User.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.Tractor_Adapters;
import com.tractor.rentatractorapp.Helpers.Current_Login_Status;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Tractor_Models;
import com.tractor.rentatractorapp.Mutual_Activities.Login_Screen;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.databinding.CustomLogoutDialogBinding;
import com.tractor.rentatractorapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    DatabaseReference databaseReference;
    ArrayList<Tractor_Models> all_tractorsList, new_arrivals_arrayList, popular_arrayList;
    Tractor_Adapters new_Arrival_adapters, popular_adapters;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        new_arrivals_arrayList = new ArrayList<>();
        popular_arrayList = new ArrayList<>();
        all_tractorsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s != null && s.length() > 0) {
                    try {
                        popular_adapters.getFilter().filter(s);
                        new_Arrival_adapters.getFilter().filter(s);

                    } catch (Exception ex) {

                    }

                } else {
                    try {
                        popular_adapters.setData(popular_arrayList);
                        new_Arrival_adapters.setData(new_arrivals_arrayList);

                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.logout.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.material);
            alert.setCancelable(false);
            CustomLogoutDialogBinding customLogoutDialogBinding = CustomLogoutDialogBinding.inflate(getLayoutInflater());
            alert.setView(customLogoutDialogBinding.getRoot());
            Dialog dialog = alert.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().getAttributes().gravity = Gravity.TOP | Gravity.CENTER;

            customLogoutDialogBinding.keepUsing.setOnClickListener(view1 -> dialog.dismiss());

            customLogoutDialogBinding.logout.setOnClickListener(view12 -> {
                FirebaseAuth.getInstance().signOut();
                Current_Login_Status.Save_Login(getContext(), "none");
                startActivity(new Intent(getContext(), Login_Screen.class));
                getActivity().finish();
            });

            dialog.show();
        });

        get_Tractors();
        get_Profile_Image();
        return binding.getRoot();
    }

    private void get_Profile_Image() {
        FirebaseDatabase.getInstance().getReference("All_Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    try {
                        Glide.with(requireContext()).load(snapshot.child("image").getValue().toString())
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        binding.itemProgressBar.setVisibility(View.INVISIBLE);
                                        return false;
                                    }
                                }).into(binding.userProfileImage);

                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void get_Tractors() {

        databaseReference.child(Variables.Tractors).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    new_arrivals_arrayList.clear();
                    popular_arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Tractor_Models tractorModels = dataSnapshot.getValue(Tractor_Models.class);

                        if (dataSnapshot.child("status").getValue().toString().equals(Variables.New_Arrivals)) {
                            new_arrivals_arrayList.add(tractorModels);
                        } else if (dataSnapshot.child("status").getValue().toString().equals(Variables.Popular_Arrivals)) {
                            popular_arrayList.add(tractorModels);
                        }

                    }

                    new_Arrival_adapters = new Tractor_Adapters(new_arrivals_arrayList, getContext());
                    binding.recyclerViewNew.setAdapter(new_Arrival_adapters);
                    popular_adapters = new Tractor_Adapters(popular_arrayList, getContext());
                    binding.recyclerViewPop.setAdapter(popular_adapters);
                    new_Arrival_adapters.notifyDataSetChanged();
                    popular_adapters.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
