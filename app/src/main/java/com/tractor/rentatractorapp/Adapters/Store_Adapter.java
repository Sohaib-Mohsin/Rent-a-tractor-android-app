package com.tractor.rentatractorapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tractor.rentatractorapp.Admin.Activities.Admin_Products_Screen;
import com.tractor.rentatractorapp.Models.Store_Model;
import com.tractor.rentatractorapp.Models.Tractor_Models;
import com.tractor.rentatractorapp.R;
import java.util.ArrayList;
import java.util.Collection;

public class Store_Adapter extends RecyclerView.Adapter<Store_Adapter.viewHolder> {

    ArrayList<Store_Model> home_modelsArrayList;
    ArrayList<Store_Model> filterList;
    Context context;

    public Store_Adapter(ArrayList<Store_Model> home_modelsArrayList, Context context) {
        this.home_modelsArrayList = home_modelsArrayList;
        this.filterList = home_modelsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_store_items, parent, false);
        return new viewHolder(view);
    }
    public void setData(ArrayList<Store_Model> employeesModel) {
        this.filterList = employeesModel;
        this.home_modelsArrayList = employeesModel;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Store_Model store_model = home_modelsArrayList.get(position);
        holder.Title.setText(store_model.getTitle());
        holder.Location.setText(store_model.getLocation());
        holder.ratingBar.setRating(store_model.getRating());

        String Store_Status = store_model.getStatus();
        if(Store_Status.equals("Opened")){
            holder.Image.setBorderColor(Color.GREEN);
        }
        else if(Store_Status.equals("Closed")){
            holder.Image.setBorderColor(Color.RED);
        }

        try {
            Glide.with(context).load(store_model.getImagesUrl()).addListener(new RequestListener<Drawable>() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, Admin_Products_Screen.class).putExtra("Store_Model", store_model)));

    }

    @Override
    public int getItemCount() {
        return home_modelsArrayList.size();
    }
    public Filter getFilter() {

        return filter;
    }
    Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                ArrayList<Store_Model> filteredPosts = new ArrayList<>();

                String filterPattern = constraint.toString().trim();

                for (Store_Model employeesModel : filterList) {
                    String filterString = filterPattern.toLowerCase().trim();
                    if (employeesModel.getTitle().toLowerCase().contains(filterString) || employeesModel.getLocation().toLowerCase().contains(filterString)) {
                        filteredPosts.add(employeesModel);
                    }
                }

                results.count = filteredPosts.size();
                results.values = filteredPosts;
            } else {

                results.count = filterList.size();
                results.values = filterList;
            }

            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            home_modelsArrayList = new ArrayList<>();
            home_modelsArrayList.addAll((Collection<? extends Store_Model>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    static class viewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        RoundedImageView Image;
        TextView Title, Location;
        ProgressBar progressBar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.rating);
            Image = itemView.findViewById(R.id.image);
            Title = itemView.findViewById(R.id.title);
            Location = itemView.findViewById(R.id.loc);
            progressBar = itemView.findViewById(R.id.item_progressBar);
        }
    }
}
