package com.kenova.store.Item;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ornolfr.ratingview.RatingView;
import com.kenova.store.Activity.StoreDetailActivity;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kenova on 3/24/2019.
 */

public class ListItem extends RecyclerView.Adapter<ListItem.ItemRowHolder> {

    private ArrayList<StoresModels> dataList;
    private Context mContext;
    private int rowLayout;
    private DatabaseHelper databaseHelper;

    public ListItem(Context context, ArrayList<StoresModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
        databaseHelper = new DatabaseHelper(mContext);

    }


    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_list, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final StoresModels singleItem = dataList.get(position);
        holder.text1.setText(singleItem.getName());
        holder.price1.setText(singleItem.getDistance());
        holder.address1.setText(singleItem.getAddress());
        holder.category.setText(singleItem.getCname());
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRateAvg()));
        Picasso.with(mContext)
                .load(singleItem.getImage())
                .resize(100,100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.images);

        if (databaseHelper.getFavouriteById(singleItem.getStoreid())) {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
        }

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(singleItem.getStoreid())) {
                    databaseHelper.removeFavouriteById(singleItem.getStoreid());
                    holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
                    Toast.makeText(mContext, "Remove To Favourite", Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, singleItem.getStoreid());
                    fav.put(DatabaseHelper.KEY_TITLE, singleItem.getName());
                    fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getImage());
                    fav.put(DatabaseHelper.KEY_RATE, singleItem.getRateAvg());
                    fav.put(DatabaseHelper.KEY_ADDRESS, singleItem.getAddress());
                    fav.put(DatabaseHelper.KEY_DISTANCE, singleItem.getDistance());
                    fav.put(DatabaseHelper.KEY_CATEGORY, singleItem.getCname());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
                    Toast.makeText(mContext, "Add To Favourite", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, StoreDetailActivity.class);
                intent.putExtra("Id", singleItem.getStoreid());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView text1, price1, address1, category;
        ImageView images, favourite;
        RatingView ratingView;
        LinearLayout rootlayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootLayout);
            images = itemView.findViewById(R.id.image);
            text1 = itemView.findViewById(R.id.text);
            favourite = itemView.findViewById(R.id.favourite);
            price1 = itemView.findViewById(R.id.price);
            category = itemView.findViewById(R.id.category);
            address1 = itemView.findViewById(R.id.address);
            ratingView = itemView.findViewById(R.id.ratingView);
        }
    }
}
