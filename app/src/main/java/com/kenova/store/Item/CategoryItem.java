package com.kenova.store.Item;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenova.store.Activity.AllStoresByCatActivity;
import com.kenova.store.Models.CategoryModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kenova on 3/24/2019.
 */

public class CategoryItem extends RecyclerView.Adapter<CategoryItem.ItemRowHolder> {

    private ArrayList<CategoryModels> dataList;
    private Context mContext;
    private int rowLayout;

    public CategoryItem(Context context, ArrayList<CategoryModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        final CategoryModels singleItem = dataList.get(position);
        holder.text.setText(singleItem.getCategoryName());
        Picasso.with(mContext)
                .load(singleItem.getCategoryImage())
                .resize(100,100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.ic_home)
                .into(holder.images);
        Random rand = new Random();
        int i = rand.nextInt(4) + 1;


        switch (i) {
            case 1:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_primary));
                break;
            case 2:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_blue));
                break;

            case 3:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_green));
                break;

            case 4:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_yellow));
                break;

            default:
                break;
        }

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, AllStoresByCatActivity.class);
                intent.putExtra("Id", singleItem.getCategoryId());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView images, background;

        ItemRowHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            images = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }
    }
}
