package com.tractor.rentatractorapp.Mutual_Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.OtpView;
import com.tractor.rentatractorapp.Helpers.Current_Login_Status;
import com.tractor.rentatractorapp.Helpers.Display_Dialog;
import com.tractor.rentatractorapp.Models.View_users;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.User.Activities.MainActivity;
import com.tractor.rentatractorapp.databinding.ActivitySignUpScreenBinding;
import com.tractor.rentatractorapp.databinding.PhoneVerificationLayoutBinding;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SignUp_Screen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ActivitySignUpScreenBinding binding;
    String VerificationID;
    AlertDialog alertDialog;
    boolean verified = false;
    private Display_Dialog display_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("All_Users");
        display_dialog = new Display_Dialog(SignUp_Screen.this, "Sending Verification Code");

        binding.gotoLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignUp_Screen.this, Login_Screen.class));
            finish();
        });
        binding.signUpNow.setOnClickListener(view -> {

            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches() || TextUtils.isEmpty(binding.name.getText().toString()) ||
                    binding.password.length() < 6 || binding.phoneNumber.length() != 10) {
                Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
            } else {
                sendCode("+".concat(binding.counterCode.getSelectedCountryCode()).concat(binding.phoneNumber.getText().toString().trim()));
            }

        });

    }

    private void sendCode(String number) {

         display_dialog.display_now();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(SignUp_Screen.this).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);

                        display_dialog.dismiss_now();


                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp_Screen.this, R.style.material);
                        View v = getLayoutInflater().inflate(R.layout.phone_verification_layout, null);

                        builder.setView(v);
                        builder.setCancelable(false);
                        alertDialog = builder.create();
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.getWindow().getAttributes().gravity = Gravity.BOTTOM | Gravity.CENTER;

                        OtpView otpView = v.findViewById(R.id.otp_view);
                        TextView otpText = v.findViewById(R.id.otp_text);
                        Button Verify = v.findViewById(R.id.verifyNow);

                        otpText.setText("Enter OTP sent to ".concat(number));

                        otpView.setOtpCompletionListener(otp -> {

                            try {

                                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(VerificationID, otp);
                                firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        verified = true;
                                    } else {
                                        Toast.makeText(SignUp_Screen.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception ex) {
                                Toast.makeText(SignUp_Screen.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });
                        VerificationID = verifyID;

                        Verify.setOnClickListener(view -> {

                            if (verified) {
                                display_dialog = new Display_Dialog(SignUp_Screen.this, "Registering ...");
                                display_dialog.display_now();

                                Create_User_and_Save_to_DB(number);

                            } else {
                                display_dialog.dismiss_now();
                                Toast.makeText(SignUp_Screen.this, "Error in Verifying Phone Number", Toast.LENGTH_SHORT).show();
                            }

                        });

                        alertDialog.show();

                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void Create_User_and_Save_to_DB(String number) {

        firebaseAuth.createUserWithEmailAndPassword(binding.email.getText().toString().trim(), binding.password.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        View_users view_users = new View_users(binding.email.getText().toString().trim(),"Tap to set location", binding.name.getText().toString().trim(), number, "");

                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(view_users).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                display_dialog.dismiss_now();

                                Toast.makeText(SignUp_Screen.this, "Login to continue!", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(SignUp_Screen.this, Login_Screen.class));
                                finish();

                            } else {
                                display_dialog.dismiss_now();

                                Toast.makeText(SignUp_Screen.this, task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        display_dialog.dismiss_now();

                        Toast.makeText(SignUp_Screen.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}