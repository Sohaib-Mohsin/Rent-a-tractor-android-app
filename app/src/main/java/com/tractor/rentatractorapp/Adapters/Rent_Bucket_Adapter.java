package com.tractor.rentatractorapp.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tractor.rentatractorapp.Models.Rent_Bucket_Models;
import com.tractor.rentatractorapp.Models.Tractor_Models;
import com.tractor.rentatractorapp.R;

import java.util.ArrayList;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;

public class Rent_Bucket_Adapter extends RecyclerView.Adapter<Rent_Bucket_Adapter.viewHolder> {

    ArrayList<Rent_Bucket_Models> rentBucketModelsArrayList;
    Context context;

    public Rent_Bucket_Adapter(ArrayList<Rent_Bucket_Models> rentBucketModelsArrayList, Context context) {
        this.rentBucketModelsArrayList = rentBucketModelsArrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_cart_items, parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Rent_Bucket_Models rentBucketModels = rentBucketModelsArrayList.get(position);
        holder.Title.setText(rentBucketModels.getTitle());
        holder.Description.setText(rentBucketModels.getDescription());
        holder.Rent.setText(rentBucketModels.getCost()+" PKR");
        holder.Time.setText(rentBucketModels.getTime());
        Glide.with(context).load(rentBucketModels.getImageUrl()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.Image);


    }

    @Override
    public int getItemCount() {
        return rentBucketModelsArrayList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        RoundedImageView Image;
        TextView Title, Description, Rent, Time;
        ProgressBar progressBar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.image);
            Title = itemView.findViewById(R.id.title);
            Description = itemView.findViewById(R.id.desc);
            Rent = itemView.findViewById(R.id.rent);
            Time = itemView.findViewById(R.id.time);
            progressBar = itemView.findViewById(R.id.item_progressBar);

        }
    }
}