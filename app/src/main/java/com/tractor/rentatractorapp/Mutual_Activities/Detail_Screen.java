package com.tractor.rentatractorapp.Mutual_Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.michaldrabik.classicmaterialtimepicker.CmtpDateDialogFragment;
import com.tractor.rentatractorapp.Helpers.Current_Login_Status;
import com.tractor.rentatractorapp.Helpers.Display_Dialog;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Rent_Bucket_Models;
import com.tractor.rentatractorapp.Models.Requests_Models;
import com.tractor.rentatractorapp.Models.Tractor_Models;
import com.tractor.rentatractorapp.Notifications.AllConstants;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.User.Activities.MainActivity;
import com.tractor.rentatractorapp.databinding.ActivityDetailScreenBinding;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Detail_Screen extends AppCompatActivity {

    private ActivityDetailScreenBinding binding;
    private ArrayList<SlideModel> imageSlider_modelsArrayList;
    private String Product_ID, Product_Status, url_1, Rating = "0", Total_Rent = "";
    private int hour, minute, day, month, year;
    private Display_Dialog dialog;
    String hiring_date, return_date;
    Tractor_Models tractorModels;
    int total_days = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageSlider_modelsArrayList = new ArrayList<>();

        tractorModels = (Tractor_Models) getIntent().getSerializableExtra("Product_Model");

        Product_ID = tractorModels.getTimeStamp();
        binding.storeTitle.setText(tractorModels.getStore());
        binding.rent.setText(tractorModels.getRent() + " PKR/day");
        binding.desc.setText(tractorModels.getDescription());
        binding.title.setText(tractorModels.getTitle());

        getTractor_Status();
        getTractor_Images();

        if (Current_Login_Status.get_login(Detail_Screen.this).equals("Admin")) {
            binding.hiringDate.setVisibility(View.GONE);
            binding.returnDate.setVisibility(View.GONE);
            binding.rentNow.setVisibility(View.GONE);
        } else if (Current_Login_Status.get_login(Detail_Screen.this).equals("User")) {
            FirebaseDatabase.getInstance().getReference(Variables.Rent_Bucket_Items).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {

                                FirebaseDatabase.getInstance().getReference(Variables.My_Rented_Items).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.exists()) {
                                                    binding.hiringDate.setVisibility(View.VISIBLE);
                                                    binding.returnDate.setVisibility(View.VISIBLE);
                                                    binding.rentNow.setVisibility(View.VISIBLE);
                                                    binding.rentNow.setEnabled(true);
                                                    binding.rentNow.setText("Rent Now");

                                                } else if (snapshot.exists()) {
                                                    binding.hiringDate.setVisibility(View.INVISIBLE);
                                                    binding.returnDate.setVisibility(View.INVISIBLE);
                                                    binding.rentNow.setVisibility(View.VISIBLE);
                                                    binding.rentNow.setEnabled(false);
                                                    binding.rentNow.setText("You already have a rented item");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            } else {
                                binding.hiringDate.setVisibility(View.INVISIBLE);
                                binding.returnDate.setVisibility(View.INVISIBLE);
                                binding.rentNow.setVisibility(View.VISIBLE);
                                binding.rentNow.setEnabled(false);
                                binding.rentNow.setText("You already have a rented item");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

        binding.goBack.setOnClickListener(view -> finish());

        binding.productStatus.setOnClickListener(view -> {

            if (Current_Login_Status.get_login(Detail_Screen.this).equals("Admin")) {

                if (Product_Status.equals("true")) {
                    dialog = new Display_Dialog(Detail_Screen.this, "Making " + tractorModels.getTitle() + " Un Available...");
                    Product_Status = "false";
                    binding.productStatus.setText("Not Available");
                    binding.productStatus.setBackgroundResource(R.drawable.button_bg2);
                } else if (Product_Status.equals("false")) {
                    dialog = new Display_Dialog(Detail_Screen.this, "Making " + tractorModels.getTitle() + " Available...");
                    Product_Status = "true";
                    binding.productStatus.setText("Available");
                    binding.productStatus.setBackgroundResource(R.drawable.button_bg1);
                }
                dialog.display_now();

                HashMap<String, Object> map = new HashMap<>();
                map.put("isAvailable", Product_Status);

                FirebaseDatabase.getInstance().getReference(Variables.Tractors).child(Product_ID).updateChildren(map)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialog.dismiss_now();
                                finish();

                            } else {
                                dialog.dismiss_now();
                                Toast.makeText(Detail_Screen.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        });
        binding.rentNow.setOnClickListener(view -> {

            if (binding.hiringDate.getText().toString().equals("Select Hiring Date") || binding.returnDate.getText().toString().equals("Select Return Date")) {
                Toast.makeText(this, "Please select duration", Toast.LENGTH_SHORT).show();
            } else {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = format.parse(binding.hiringDate.getText().toString());
                    d2 = format.parse(binding.returnDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long difference = d2.getTime() - d1.getTime();
                difference = difference / (24 * 60 * 60 * 1000);
                total_days = (int) difference;

                if (total_days < 0) {
                    Toast.makeText(Detail_Screen.this, "Please Select valid duration", Toast.LENGTH_SHORT).show();
                } else if (total_days == 0) {
                    Toast.makeText(Detail_Screen.this, "Please Select atleast 1 day", Toast.LENGTH_SHORT).show();
                } else if (total_days > 30) {
                    Toast.makeText(Detail_Screen.this, "Not applicable for above 30 days", Toast.LENGTH_SHORT).show();
                } else {

                    int per_rent = Integer.parseInt(tractorModels.getRent());
                    Total_Rent = String.valueOf((total_days * per_rent));

                    AlertDialog.Builder alert = new AlertDialog.Builder(Detail_Screen.this, R.style.material);
                    alert.setCancelable(false);
                    View v = LayoutInflater.from(Detail_Screen.this).inflate(R.layout.custom_logout_dialog, null);
                    alert.setView(v);
                    Dialog dialog2 = alert.create();
                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog2.getWindow().getAttributes().gravity = Gravity.BOTTOM | Gravity.CENTER;

                    TextView Title_msg = v.findViewById(R.id.txt_message);
                    Button Accept = v.findViewById(R.id.keep_using);
                    Button Decline = v.findViewById(R.id.logout);

                    Title_msg.setText(Total_Rent + " PKR for " + total_days + " day(s)");
                    Accept.setText("Proceed");
                    Decline.setText("Cancel");

                    Decline.setOnClickListener(view1 -> dialog2.dismiss());

                    Accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog = new Display_Dialog(Detail_Screen.this, "Requesting " + tractorModels.getTitle());
                            dialog.display_now();

                            Rent_Bucket_Models rentBucketModels = new Rent_Bucket_Models(url_1, tractorModels.getTitle(), tractorModels.getDescription(),
                                    tractorModels.getStore(), Product_ID, Total_Rent, hiring_date + " to " + return_date, Integer.toString(total_days));

                            FirebaseDatabase.getInstance().getReference(Variables.Rent_Bucket_Items).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Product_ID).setValue(rentBucketModels).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    Requests_Models requests_models = new Requests_Models(url_1, tractorModels.getTitle(), Total_Rent, hiring_date + " To " + return_date, Product_ID, "requested", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(requests_models).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {

                                                    getToken("New Rent Request","MJU5kKcJ2iVPWhQ310BBsicrDdl1" , "R_Img");

                                                    dialog.dismiss_now();
                                                    Toast.makeText(Detail_Screen.this, "Please review in the Rent Bucket section", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Detail_Screen.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    dialog.dismiss_now();
                                                    Toast.makeText(Detail_Screen.this, task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    dialog.dismiss_now();
                                    Toast.makeText(Detail_Screen.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    dialog2.show();

                }

            }

        });
        binding.hiringDate.setOnClickListener(view -> {
            try {
                getSelected_Date("hiring");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        binding.returnDate.setOnClickListener(view -> {
            try {
                getSelected_Date("return");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

    }

    private void getSelected_Date(String data_as) throws ParseException {

        CmtpDateDialogFragment cmtpDateDialogFragment = CmtpDateDialogFragment.newInstance("Select", "Cancel");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        cmtpDateDialogFragment.setInitialDate(day, month, year);
        cmtpDateDialogFragment.setCustomSeparator("/");
        cmtpDateDialogFragment.setCancelable(false);

        cmtpDateDialogFragment.setOnDatePickedListener(cmtpDate -> {

            String selectedDate = cmtpDate.toString("/");

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            try {

                Date date = format.parse(selectedDate);

                if (date != null) {

                    if (data_as.equals("hiring")) {
                        hiring_date = format.format(date);
                        binding.hiringDate.setText(hiring_date);
                    } else if (data_as.equals("return")) {
                        return_date = format.format(date);
                        binding.returnDate.setText(return_date);
                    }

                }
            } catch (Exception ex) {

            }

        });
        cmtpDateDialogFragment.show(getSupportFragmentManager(), "Date");

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    }

    private void getTractor_Images() {

        FirebaseDatabase.getInstance().getReference().child(Variables.Tractors).child(Product_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    url_1 = snapshot.child("imagesUrls_1").getValue().toString();
                    String url_2 = snapshot.child("imagesUrls_2").getValue().toString();

                    imageSlider_modelsArrayList.add(new SlideModel(url_1, ScaleTypes.CENTER_CROP));
                    imageSlider_modelsArrayList.add(new SlideModel(url_2, ScaleTypes.CENTER_CROP));
                    binding.imageSlider.setImageList(imageSlider_modelsArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getTractor_Status() {

        FirebaseDatabase.getInstance().getReference(Variables.Tractors).child(Product_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product_Status = snapshot.child("isAvailable").getValue().toString();
                    if (Product_Status.equals("true")) {
                        binding.productStatus.setText("Available");
                        binding.productStatus.setBackgroundResource(R.drawable.button_bg1);
                    } else if (Product_Status.equals("false")) {
                        binding.productStatus.setText("Not Available");
                        binding.productStatus.setBackgroundResource(R.drawable.button_bg2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getToken(String Message, String hisID, String myImage) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("FCM_Token").child(hisID);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String token = snapshot.child("token_id").getValue().toString();

                JSONObject to = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", "New Message");
                    data.put("message", Message);
                    data.put("hisID", hisID);
                    data.put("hisImage", myImage);

                    to.put("to", token);
                    to.put("data", data);

                    sendNotification(to);

                } catch (Exception e) {
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(JSONObject to) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AllConstants.NOTIFICATION_URL, to, response -> {
       }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + AllConstants.SERVER_KEY);
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

}