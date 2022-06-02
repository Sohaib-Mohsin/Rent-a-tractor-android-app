package com.tractor.rentatractorapp.User.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tractor.rentatractorapp.Adapters.Rent_Bucket_Adapter;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Rent_Bucket_Models;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.databinding.FragmentRentBucketBinding;

import java.util.ArrayList;
import java.util.Objects;

public class Rent_Bucket_Fragment extends Fragment {

    private FragmentRentBucketBinding binding;
    private ArrayList<Rent_Bucket_Models> rentBucketModelsArrayList;
    private Rent_Bucket_Adapter rentBucket_adapter;
    private Dialog dialog;
    private TextView Title_msg;
    private Button Close;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRentBucketBinding.inflate(getLayoutInflater(), container, false);

        rentBucketModelsArrayList = new ArrayList<>();

        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext(), R.style.material);
        alert.setCancelable(false);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_request_dialog, null);
        alert.setView(v);
        dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        Title_msg = v.findViewById(R.id.request_status);
        Close = v.findViewById(R.id.close);

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });

            }
        });

        getReviewItems();
        Check_Request();

        return binding.getRoot();
    }

    private void Check_Request() {

        FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String request = snapshot.child("request_status").getValue().toString();
                    if (request.equals("accepted")) {
                        Close.setBackgroundResource(R.drawable.button_bg1);
                        Close.setText("Done");
                        Title_msg.setText("Congrats\nYour request is Accepted\n\nOur Delivery man will be there on time\nGet the Amount Ready");

                    } else if (request.equals("rejected")) {
                        Close.setBackgroundResource(R.drawable.button_bg2);
                        Close.setText("Close");
                        Title_msg.setText("Oops\nYour request is Rejected");
                    }
                    dialog.show();

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getReviewItems() {

        FirebaseDatabase.getInstance().getReference(Variables.Rent_Bucket_Items).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            rentBucketModelsArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Rent_Bucket_Models rentBucketModels = dataSnapshot.getValue(Rent_Bucket_Models.class);
                                rentBucketModelsArrayList.add(rentBucketModels);
                            }
                            rentBucket_adapter = new Rent_Bucket_Adapter(rentBucketModelsArrayList, getContext());
                            binding.recyclerView.setAdapter(rentBucket_adapter);
                            rentBucket_adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}