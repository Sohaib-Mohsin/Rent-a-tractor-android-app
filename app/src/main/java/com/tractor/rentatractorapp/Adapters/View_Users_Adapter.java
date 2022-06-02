package com.tractor.rentatractorapp.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tractor.rentatractorapp.Admin.Activities.View_all_users;
import com.tractor.rentatractorapp.Models.View_users;
import com.tractor.rentatractorapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_Users_Adapter extends RecyclerView.Adapter<View_Users_Adapter.viewHolder>{

    Context context;
    ArrayList<View_users> view_all_usersArrayList;

    public View_Users_Adapter(Context context, ArrayList<View_users> view_all_usersArrayList) {
        this.context = context;
        this.view_all_usersArrayList = view_all_usersArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_users, null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        View_users view_users= view_all_usersArrayList.get(position);

        holder.Name.setText(view_users.getName());
        holder.Email.setText(view_users.getEmail());
        holder.Location.setText(view_users.getLocation());
        holder.Phone.setText(view_users.getPhone());

        Glide.with(context).load(view_users.getImage()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                return false;
            }
        }).into(holder.Image);

    }

    @Override
    public int getItemCount() {
        return view_all_usersArrayList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        CircleImageView Image;
        TextView Name, Email, Location, Phone;
        ProgressBar progressBar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.image);
            Name = itemView.findViewById(R.id.username);
            Email = itemView.findViewById(R.id.email);
            Location = itemView.findViewById(R.id.location);
            Phone = itemView.findViewById(R.id.phoneNumber);
            progressBar = itemView.findViewById(R.id.item_progressBar);

        }
    }
}
