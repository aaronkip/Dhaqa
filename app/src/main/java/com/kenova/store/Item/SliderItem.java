package com.kenova.store.Item;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ornolfr.ratingview.RatingView;
import com.kenova.store.Activity.StoreDetailActivity;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kenova on 3/23/2019.
 */

public class SliderItem extends PagerAdapter {

    private LayoutInflater inflater;
    private Activity context;
    private ArrayList<StoresModels> mList;

    public SliderItem(Activity context, ArrayList<StoresModels> storesModels) {
        this.context = context;
        this.mList = storesModels;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = LayoutInflater.from(context).inflate(R.layout.item_slider, container, false);
        assert imageLayout != null;
        RelativeLayout rootlayout = imageLayout.findViewById(R.id.rootLayout);
        ImageView imageView = imageLayout.findViewById(R.id.image);
        TextView name = imageLayout.findViewById(R.id.text);
        TextView address = imageLayout.findViewById(R.id.address);
        Button distance = imageLayout.findViewById(R.id.price);
        RatingView ratingView = imageLayout.findViewById(R.id.ratingView);

        final StoresModels storesModels = mList.get(position);
        Picasso.with(context)
                .load(storesModels.getImage())
                .placeholder(R.drawable.image_placeholder).into(imageView);
        name.setText(storesModels.getName());
        distance.setText(storesModels.getDistance());
        address.setText(storesModels.getAddress());
        ratingView.setRating(Float.parseFloat(storesModels.getRateAvg()));

        rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StoreDetailActivity.class);
                intent.putExtra("Id", storesModels.getStoreid());
                context.startActivity(intent);

            }
        });
        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
