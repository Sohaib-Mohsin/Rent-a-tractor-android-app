package com.tractor.rentatractorapp.User.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

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
import com.tractor.rentatractorapp.Adapters.Requests_Adapter;
import com.tractor.rentatractorapp.Admin.Activities.Request_Screen;
import com.tractor.rentatractorapp.Helpers.Current_Login_Status;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Requests_Models;
import com.tractor.rentatractorapp.Models.View_users;
import com.tractor.rentatractorapp.Mutual_Activities.Login_Screen;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.User.Activities.Maps_Screen;
import com.tractor.rentatractorapp.User.Activities.Update_Profile_Image;
import com.tractor.rentatractorapp.databinding.CustomLogoutDialogBinding;
import com.tractor.rentatractorapp.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private final int CODE_IMAGE = 1;
    Requests_Adapter requests_adapter;
    ArrayList<Requests_Models> requests_modelsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        requests_modelsArrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("All_Users");

        binding.changeImage.setText(Html.fromHtml("<u>Replace Image</u>"));
        binding.profileLocation.setText(Html.fromHtml("<u>Tap to update location</u>"));

        binding.profileLocation.setOnClickListener(view -> {startActivityForResult(new Intent(getContext(), Maps_Screen.class), 786);});
        binding.changeImage.setOnClickListener(view -> startActivity(new Intent(getContext(), Update_Profile_Image.class)));

        binding.logout.setOnClickListener(view -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.material);
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
                    Current_Login_Status.Save_Login(getContext(), "none");
                    startActivity(new Intent(getContext(), Login_Screen.class));
                    getActivity().finish();
                }
            });

            dialog.show();

        });

        getProfile();
        getRentedItems();

        return binding.getRoot();
    }

    private void getRentedItems() {

        FirebaseDatabase.getInstance().getReference(Variables.My_Rented_Items).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    requests_modelsArrayList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Requests_Models requests_models = dataSnapshot.getValue(Requests_Models.class);
                        requests_modelsArrayList.add(requests_models);
                    }
                    requests_adapter = new Requests_Adapter(getContext(), requests_modelsArrayList);
                    binding.recyclerView.setAdapter(requests_adapter);
                    requests_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getProfile() {
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    View_users view_users = snapshot.getValue(View_users.class);

                    binding.profileEmail.setText(firebaseAuth.getCurrentUser().getEmail().substring(0, 3).concat("***").concat("@gmail.com"));
                    binding.profilename.setText(view_users.getName());
                    binding.profilephone.setText("+92********".concat(view_users.getPhone().substring(11, 13)));
                    binding.profileLocation.setText(Html.fromHtml("<u>"+view_users.getLocation()+"</u>"));
                    try {
                        Glide.with(getContext()).load(view_users.getImage()).addListener(new RequestListener<Drawable>() {
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
                    } catch (Exception ex) {
                        binding.profileImage.setImageResource(R.drawable.developer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 786 && resultCode == RESULT_OK && data != null) {
            String location = data.getStringExtra("LOCATION");
            binding.profileLocation.setText(Html.fromHtml("<u>" + location + "</u>"));

            HashMap<String, Object> map = new HashMap<>();
            map.put("Location", location);

            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}