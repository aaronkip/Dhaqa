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
import com.kenova.store.Activity.OffersDetailActivity;
import com.kenova.store.Models.OffersModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by kenova on 3/24/2019.
 */

public class AllOffersItem extends RecyclerView.Adapter<AllOffersItem.ItemRowHolder> {

    private ArrayList<OffersModels> dataList;
    private Context mContext;
    private int rowLayout;
    private DatabaseHelper databaseHelper;

    public AllOffersItem(Context context, ArrayList<OffersModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
        databaseHelper = new DatabaseHelper(mContext);

    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_all, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final OffersModels singleItem = dataList.get(position);
        holder.text.setText(singleItem.getName());
        Double getprice = Double.valueOf(singleItem.getPrice());
        String total = String.format(Locale.US, "$%s",
                NumberFormat.getNumberInstance(Locale.US).format(getprice));
        holder.price.setText(total);
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
                Intent intent = new Intent(mContext, OffersDetailActivity.class);
                intent.putExtra("Id", singleItem.getOfferid());
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
