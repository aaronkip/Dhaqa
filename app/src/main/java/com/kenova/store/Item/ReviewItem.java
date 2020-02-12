package com.kenova.store.Item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ornolfr.ratingview.RatingView;
import com.kenova.store.Models.ReviewModels;
import com.kenova.store.R;
import com.kenova.store.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kenova on 11/20/2019.
 */

public class ReviewItem extends RecyclerView.Adapter<ReviewItem.ItemRowHolder>{
    private ArrayList<ReviewModels> dataList;
    private Context mContext;
    private int rowLayout;
    private DatabaseHelper databaseHelper;

    public ReviewItem(Context context, ArrayList<ReviewModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
        databaseHelper = new DatabaseHelper(mContext);

    }

    @NonNull
    @Override
    public ReviewItem.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewItem.ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewItem.ItemRowHolder holder, final int position) {
        final ReviewModels singleItem = dataList.get(position);
        holder.fullname.setText(singleItem.getUsername());
        holder.review.setText(singleItem.getReview());
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRate()));
        Picasso.with(mContext)
                .load(singleItem.getUserimage())
                .resize(100,100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.images);

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView fullname, review;
        ImageView images;
        RatingView ratingView;
        RelativeLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            images = itemView.findViewById(R.id.userimages);
            fullname = itemView.findViewById(R.id.fullname);
            review = itemView.findViewById(R.id.review);
            ratingView = itemView.findViewById(R.id.ratingView);
        }
    }





}