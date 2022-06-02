package com.tractor.rentatractorapp.Mutual_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.tractor.rentatractorapp.Admin.Activities.Admin_Stores_Screen;
import com.tractor.rentatractorapp.Helpers.Current_Login_Status;
import com.tractor.rentatractorapp.Helpers.Display_Dialog;
import com.tractor.rentatractorapp.User.Activities.MainActivity;
import com.tractor.rentatractorapp.databinding.ActivityLoginScreenBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login_Screen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private FirebaseAuth firebaseAuth;
    private Display_Dialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progress_dialog = new Display_Dialog(Login_Screen.this, "Getting in...");
        binding.gotoSignUp.setOnClickListener(view -> {
            startActivity(new Intent(Login_Screen.this, SignUp_Screen.class));
            finish();
        });
        binding.loginNow.setOnClickListener(view -> {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches() || binding.password.length() < 6) {
                Toast.makeText(Login_Screen.this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
            } else {
                Login_User_Admin(binding.email.getText().toString(), binding.password.getText().toString(), "User");
            }
        });
        binding.loginAdmin.setOnClickListener(view -> {

            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches() || binding.password.length() < 6) {
                Toast.makeText(Login_Screen.this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
            } else {
                Login_User_Admin("Admincs@gmail.com", binding.password.getText().toString(), "Admin");
            }
        });
        binding.forgot.setOnClickListener(view -> {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()) {
                Toast.makeText(Login_Screen.this, "Please enter valid email!", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.sendPasswordResetEmail(binding.email.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login_Screen.this, "Please check your email to reset password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Login_Screen.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void Login_User_Admin(String email, String password, String As) {

        progress_dialog.display_now();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> data) {

                        String token = data.getResult().getToken();
                        Map<String, Object> tokenMap = new HashMap<>();
                        tokenMap.put("token_id", token);
                        tokenMap.put("uid", FirebaseAuth.getInstance().getUid());

                        FirebaseDatabase.getInstance().getReference().child("FCM_Token").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (As.equals("Admin")) {
                                                Current_Login_Status.Save_Login(Login_Screen.this, "Admin");
                                                startActivity(new Intent(Login_Screen.this, Admin_Stores_Screen.class));
                                            } else if (As.equals("User")) {
                                                Current_Login_Status.Save_Login(Login_Screen.this, "User");
                                                startActivity(new Intent(Login_Screen.this, MainActivity.class));
                                            }
                                            progress_dialog.dismiss_now();
                                            finish();
                                        } else {
                                            progress_dialog.dismiss_now();
                                            Toast.makeText(Login_Screen.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

            } else {
                progress_dialog.dismiss_now();
                Toast.makeText(Login_Screen.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null) {
            if (Current_Login_Status.get_login(Login_Screen.this).equals("Admin")) {
                startActivity(new Intent(Login_Screen.this, Admin_Stores_Screen.class));
            } else if (Current_Login_Status.get_login(Login_Screen.this).equals("User")) {
                startActivity(new Intent(Login_Screen.this, MainActivity.class));
            }
            finish();
        }
    }
}