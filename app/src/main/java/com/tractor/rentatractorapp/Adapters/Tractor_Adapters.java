package com.tractor.rentatractorapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.tractor.rentatractorapp.Mutual_Activities.Detail_Screen;

import java.util.ArrayList;
import java.util.Collection;

public class Tractor_Adapters extends RecyclerView.Adapter<Tractor_Adapters.viewHolder> {

    ArrayList<Tractor_Models> tractorModelsArrayList;
    ArrayList<Tractor_Models> filteredList;
    Context context;

    public Tractor_Adapters(ArrayList<Tractor_Models> tractorModelsArrayList, Context context) {
        this.tractorModelsArrayList = tractorModelsArrayList;
        this.filteredList = tractorModelsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_home, parent, false);
        return new viewHolder(view);
    }
    public void setData(ArrayList<Tractor_Models> employeesModel) {
        this.filteredList = employeesModel;
        this.tractorModelsArrayList = employeesModel;
        notifyDataSetChanged();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Tractor_Models tractorModels = tractorModelsArrayList.get(position);

        holder.Title.setText(tractorModels.getTitle());
        holder.Description.setText(tractorModels.getDescription());
        holder.Rent.setText(tractorModels.getRent()+" PKR/day");
        if(tractorModels.getIsAvailable().equals("true")){
            holder.Is_Available.setText("Available");
            holder.Is_Available.setTextColor(context.getResources().getColor(R.color.green_dark));
            holder.Is_Available.setBackgroundResource(R.drawable.left_btn_dr);
        }
        else{
            holder.Is_Available.setText("Not Available");
            holder.Is_Available.setTextColor(Color.RED);
            holder.Is_Available.setBackgroundResource(R.drawable.right_btn_dr);
        }

        Glide.with(context).load(tractorModels.getImagesUrls_1()).addListener(new RequestListener<Drawable>() {
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

        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, Detail_Screen.class).putExtra("Product_Model", tractorModels)));

    }

    @Override
    public int getItemCount() {
        return tractorModelsArrayList.size();
    }

    public Filter getFilter() {

        return filter;
    }
    Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                ArrayList<Tractor_Models> filteredPosts = new ArrayList<>();

                String filterPattern = constraint.toString().trim();

                for (Tractor_Models employeesModel : filteredList) {
                    String filterString = filterPattern.toLowerCase().trim();
                    if (employeesModel.getTitle().toLowerCase().contains(filterString) || employeesModel.getRent().contains(filterPattern)) {
                        filteredPosts.add(employeesModel);
                    }
                }

                results.count = filteredPosts.size();
                results.values = filteredPosts;
            } else {

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tractorModelsArrayList = new ArrayList<>();
            tractorModelsArrayList.addAll((Collection<? extends Tractor_Models>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    static class viewHolder extends RecyclerView.ViewHolder {

        RoundedImageView Image;
        TextView Title, Description, Rent, Is_Available;
        ProgressBar progressBar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.image);
            Title = itemView.findViewById(R.id.title);
            Description = itemView.findViewById(R.id.desc);
            Rent = itemView.findViewById(R.id.rent);
            progressBar = itemView.findViewById(R.id.item_progressBar);
            Is_Available = itemView.findViewById(R.id.isAvailable);
        }
    }
}
