package com.kenova.store.Item;

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

import com.github.ornolfr.ratingview.RatingView;
import com.kenova.store.Activity.EventsDetailActivity;
import com.kenova.store.Models.EventsModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kenova on 3/24/2019.
 */

public class EventsItem extends RecyclerView.Adapter<EventsItem.ItemRowHolder> {

    private ArrayList<EventsModels> dataList;
    private Context mContext;
    private int rowLayout;
    private DatabaseHelper databaseHelper;

    public EventsItem(Context context, ArrayList<EventsModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
        databaseHelper = new DatabaseHelper(mContext);

    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final EventsModels singleItem = dataList.get(position);
        holder.text.setText(singleItem.getName());
        holder.price.setText(singleItem.getDatestart());
        holder.address.setText(singleItem.getAddress());
        Picasso.with(mContext)
                .load(singleItem.getImage())
                .resize(100,100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.images);




        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(mContext);
                    Intent intent = new Intent(mContext, EventsDetailActivity.class);
                    intent.putExtra("Id", singleItem.getEventid());
                    mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text, price, address, purpose;
        ImageView images;
        RatingView ratingView;
        LinearLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            images = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            price = itemView.findViewById(R.id.price);
            address = itemView.findViewById(R.id.address);
            ratingView = itemView.findViewById(R.id.ratingView);
        }
    }
}
