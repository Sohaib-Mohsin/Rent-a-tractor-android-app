package com.tractor.rentatractorapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tractor.rentatractorapp.Helpers.Display_Dialog;
import com.tractor.rentatractorapp.Helpers.Variables;
import com.tractor.rentatractorapp.Models.Requests_Models;
import com.tractor.rentatractorapp.Notifications.AllConstants;
import com.tractor.rentatractorapp.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Requests_Adapter extends RecyclerView.Adapter<Requests_Adapter.viewHolder> {

    Context context;
    ArrayList<Requests_Models> requests_modelsArrayList;
    Display_Dialog display_dialog;

    public Requests_Adapter(Context context, ArrayList<Requests_Models> requests_modelsArrayList) {
        this.context = context;
        this.requests_modelsArrayList = requests_modelsArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.custom_requests_layout, null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Requests_Models requests_models = requests_modelsArrayList.get(position);
        holder.Rent.setText(requests_models.getRent() + " PKR");
        holder.Time.setText(requests_models.getTime());
        holder.Product_title.setText(requests_models.getTitle());

        String req = requests_models.getRequest_status();
        if (req.equals("accepted")) {
            holder.roundedImageView.setBorderColor(context.getResources().getColor(R.color.green_dark));
        } else if (req.equals("rejected")) {
            holder.roundedImageView.setBorderColor(Color.RED);
        }

        Glide.with(context).load(requests_models.getImage_url()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progress.setVisibility(View.INVISIBLE);
                return false;
            }
        }).into(holder.roundedImageView);

        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.material);
        alert.setCancelable(false);
        View v = LayoutInflater.from(context).inflate(R.layout.custom_admin_request_dialog, null);
        alert.setView(v);
        Dialog dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        TextView Title_msg = v.findViewById(R.id.txt_message);
        Button Accept = v.findViewById(R.id.keep_using);
        Button Decline = v.findViewById(R.id.logout);
        CircleImageView Image = v.findViewById(R.id.image);
        TextView Name = v.findViewById(R.id.name);
        TextView Phone = v.findViewById(R.id.phone);
        ProgressBar progress = v.findViewById(R.id.item_progressBar);

        FirebaseDatabase.getInstance().getReference("All_Users").child(requests_models.getFrom()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Name.setText(snapshot.child("name").getValue().toString());
                    Phone.setText(snapshot.child("phone").getValue().toString());

                    Glide.with(context).load(snapshot.child("image").getValue().toString()).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progress.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    }).into(Image);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requests_models.getRequest_status().equals("requested")) {

                    Accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            display_dialog = new Display_Dialog(context, "Confirming " + requests_models.getTitle());
                            display_dialog.display_now();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("request_status", "accepted");

                            FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).child(requests_models.getFrom())
                                    .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                FirebaseDatabase.getInstance().getReference(Variables.Rent_Bucket_Items).child(requests_models.getFrom()).child(requests_models.getTimeStamp())
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    Requests_Models requests_models1 = new Requests_Models(requests_models.getImage_url(), requests_models.getTitle(), requests_models.getRent(), requests_models.getTime(),
                                                                            requests_models.getTimeStamp(), requests_models.getRequest_status(), requests_models.getFrom());

                                                                    FirebaseDatabase.getInstance().getReference(Variables.My_Rented_Items).child(requests_models.getFrom())
                                                                            .child(requests_models.getTimeStamp()).setValue(requests_models1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        getToken("Rent Request Accepted",requests_models.getFrom() , "R_Img");
                                                                                        display_dialog.dismiss_now();
                                                                                        dialog.dismiss();
                                                                                        Toast.makeText(context, "Rent Request Accepted", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        display_dialog.dismiss_now();
                                                                                        dialog.dismiss();
                                                                                        Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });

                                                                } else {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    display_dialog.dismiss_now();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                display_dialog.dismiss_now();
                                            }
                                        }
                                    });

                        }
                    });

                    Decline.setOnClickListener(view1 -> {

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("request_status", "rejected");

                        FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).child(requests_models.getFrom())
                                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseDatabase.getInstance().getReference(Variables.Rent_Bucket_Items).child(requests_models.getFrom()).child(requests_models.getTimeStamp())
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                getToken("Rent Request Rejected",requests_models.getFrom() , "R_Img");
                                                                dialog.dismiss();
                                                                Toast.makeText(context, "Request Removed", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                dialog.dismiss();
                                                                Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    });

                    dialog.show();

                } else {


                    Title_msg.setText("Get back the tractor?");
                    Accept.setText("Get Back");
                    Decline.setText("Close");

                    Accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            display_dialog = new Display_Dialog(context, "Getting back " + requests_models.getTitle());
                            display_dialog.display_now();

                            FirebaseDatabase.getInstance().getReference(Variables.Rent_Requests).child(requests_models.getFrom()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference(Variables.My_Rented_Items).child(requests_models.getFrom())
                                                        .child(requests_models.getTimeStamp()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    requests_modelsArrayList.remove(holder.getAdapterPosition());
                                                                    notifyItemRemoved(holder.getAdapterPosition());
                                                                    notifyDataSetChanged();

                                                                    getToken("Get the tractor back as your rent time duration is end now",requests_models.getFrom() , "R_Img");

                                                                    display_dialog.dismiss_now();
                                                                    dialog.dismiss();
                                                                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    display_dialog.dismiss_now();
                                                                    dialog.dismiss();
                                                                    Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                display_dialog.dismiss_now();
                                                dialog.dismiss();
                                                Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    });

                    Decline.setOnClickListener(view12 -> dialog.dismiss());
                    dialog.show();


                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return requests_modelsArrayList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        RoundedImageView roundedImageView;
        ProgressBar progress;
        TextView Product_title, Rent, Time;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            roundedImageView = itemView.findViewById(R.id.image);
            Product_title = itemView.findViewById(R.id.product_title);
            Rent = itemView.findViewById(R.id.rent);
            Time = itemView.findViewById(R.id.time);
            progress = itemView.findViewById(R.id.item_progressBar);
        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


}
