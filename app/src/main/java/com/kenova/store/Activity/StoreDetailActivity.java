package com.kenova.store.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ornolfr.ratingview.RatingView;
import com.kenova.store.Constants.BaseApp;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Fragment.ChatFragment;
import com.kenova.store.Item.TagsItem;
import com.kenova.store.Item.EventsItem;
import com.kenova.store.Item.OffersItem;
import com.kenova.store.Models.EventsModels;
import com.kenova.store.Models.OffersModels;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kenova on 3/26/2019.
 */

public class StoreDetailActivity extends AppCompatActivity {

    TextView name, address, distance, city, category, opennow, time, username;
    ImageView likebtn, chat, phone, image;
    WebView description;
    FloatingActionButton fab;

    BaseApp baseApp;
    RecyclerView tags,rvOffers, rvEvents;
    OffersItem offersItem;
    EventsItem eventsItem;
    TagsItem tagsItem;
    ArrayList<OffersModels> mOffersList;
    ArrayList<EventsModels> mEventList;
    ArrayList<String> mTags;
    DatabaseHelper databaseHelper;
    String Id;
    StoresModels item;
    RatingView ratingView;
    RelativeLayout progress, rldelete, rledit, rlchat, rlphone;
    Button addoffers, addevent;
    LinearLayout lloffers, llevent, llreview;
    ImageView backbtn, userimage;
    CardView manicure, facial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_detail);
        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        baseApp = BaseApp.getInstance();
        mOffersList = new ArrayList<>();
        mEventList = new ArrayList<>();
        mTags = new ArrayList<>();

        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        item = new StoresModels();

        rlchat = findViewById(R.id.rlchat);
        rldelete = findViewById(R.id.rldelete);
        rlphone = findViewById(R.id.rlphone);
        addoffers = findViewById(R.id.addoffers);
        addevent = findViewById(R.id.addevent);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        distance = findViewById(R.id.price);
        city = findViewById(R.id.city);
        category = findViewById(R.id.category);
        opennow = findViewById(R.id.opennow);
        time = findViewById(R.id.time);
        progress = findViewById(R.id.progress);
        fab = findViewById(R.id.fab);
        chat= findViewById(R.id.chat);
        description = findViewById(R.id.description);
        likebtn = findViewById(R.id.chat);
        phone = findViewById(R.id.phone);
        backbtn = findViewById(R.id.back_btn);
        rvOffers = findViewById(R.id.offers);
        rvEvents = findViewById(R.id.event);
        llevent = findViewById(R.id.llevent);
        lloffers = findViewById(R.id.lloffers);
        tags = findViewById(R.id.tags);
        rledit = findViewById(R.id.rledit);
        image = findViewById(R.id.image);
        userimage = findViewById(R.id.imageuser);
        username = findViewById(R.id.username);
        llreview = findViewById(R.id.llreview);
        ratingView = findViewById(R.id.ratingView);

        progress.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);
        isFavourite();

        lloffers.setVisibility(View.GONE);
        llevent.setVisibility(View.GONE);

        tags.setHasFixedSize(true);
        tags.setNestedScrollingEnabled(false);
        tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvOffers.setHasFixedSize(true);
        rvOffers.setNestedScrollingEnabled(false);
        rvOffers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvEvents.setHasFixedSize(true);
        rvEvents.setNestedScrollingEnabled(false);
        rvEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rldelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDelete();
            }
        });
        getData();



    }

    public void clickDelete() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Delete?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delete();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void delete() {
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.DELETESTORE+Id+"&userid="+MainActivity.user_id, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.STOREDETAIL +Id+"&user_lat="+Constants.LATITUDE +"&user_long="+Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataStore(respo);
                        getDataOffers(respo);
                        getDataEvents(respo);
                        fab.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataStore(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    item.setStoreid(userdata.getString("storeid"));
                    item.setUserId(userdata.getString("userid"));
                    item.setNameUser(userdata.getString("fullname"));
                    item.setImageUser(userdata.getString("imageprofile"));
                    item.setName(userdata.getString("name"));
                    item.setPhone(userdata.getString("phone"));
                    item.setOpen(userdata.getString("open"));
                    item.setClosed(userdata.getString("closed"));
                    item.setTags(userdata.getString("tags"));
                    item.setDistance(userdata.getString("distance"));
                    item.setAddress(userdata.getString("address"));
                    item.setCid(userdata.getString("cid"));
                    item.setCname(userdata.getString("cname"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setImage(userdata.getString("images"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    item.setDescription(userdata.getString("description"));

                    ratingView.setRating(Float.parseFloat(item.getRateAvg()));
                    username.setText(item.getNameUser());
                    name.setText(item.getName());
                    address.setText(item.getAddress());
                    distance.setText(item.getDistance());
                    city.setText(item.getCityName());
                    category.setText(item.getCname());
                    time.setText(" "+"("+item.getOpen()+" - "+item.getclosed()+")");
                    rledit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent = new Intent(StoreDetailActivity.this, EditStoreActivity.class);
                                intent.putExtra("Id", item.getStoreid());
                                startActivity(intent);

                        }
                    });
                    llreview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(StoreDetailActivity.this, AllReviewActivity.class);
                            intent.putExtra("Id", item.getStoreid());
                            startActivity(intent);

                        }
                    });
                    chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (baseApp.getIsLogin()) {
                                chatFragment(MainActivity.user_id, item.getUserId(), item.getNameUser(), item.getImageUser());
                            } else {
                                Intent intent = new Intent(StoreDetailActivity.this, LoginFormActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                    String mimeType = "text/html";
                    String encoding = "utf-8";
                    String htmlText = item.getDescription();

                    String text = "<html dir=" + "><head>"
                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/NeoSans_Pro_Regular.ttf\")}body{font-family: MyFont;color: #a5a5a5;text-align:justify;line-height:1.2}"
                            + "</style></head>"
                            + "<body>"
                            + htmlText
                            + "</body></html>";
                    description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + item.getLatitude() + "," + item.getLongitude() + " (" + item.getName() + ")";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            startActivity(intent);
                        }
                    });

                    likebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContentValues fav = new ContentValues();
                            if (databaseHelper.getFavouriteById(item.getStoreid())) {
                                databaseHelper.removeFavouriteById(item.getStoreid());
                                likebtn.setColorFilter(getResources().getColor(R.color.white));
                                Toast.makeText(StoreDetailActivity.this, "Remove To Favourite", Toast.LENGTH_SHORT).show();
                            } else {
                                fav.put(DatabaseHelper.KEY_ID, item.getStoreid());
                                fav.put(DatabaseHelper.KEY_TITLE, item.getName());
                                fav.put(DatabaseHelper.KEY_IMAGE, item.getImage());
                                fav.put(DatabaseHelper.KEY_RATE, item.getRateAvg());
                                fav.put(DatabaseHelper.KEY_ADDRESS, item.getAddress());
                                fav.put(DatabaseHelper.KEY_CATEGORY, item.getCname());
                                fav.put(DatabaseHelper.KEY_DISTANCE, item.getDistance());
                                databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                                likebtn.setColorFilter(getResources().getColor(R.color.colorPrimary));
                                Toast.makeText(StoreDetailActivity.this, "Add To Favourite", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    if (!item.getTags().isEmpty())
                        mTags = new ArrayList<>(Arrays.asList(item.getTags().split(",")));

                    phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + item.getPhone()));
                            startActivity(callIntent);
                        }
                    });

                    Picasso.with(this)
                            .load(item.getImage())
                            .resize(250,250)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.image_placeholder)
                            .into(image);

                    Picasso.with(this)
                            .load(item.getImageUser())
                            .resize(100,100)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.image_placeholder)
                            .into(userimage);

                    addoffers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(StoreDetailActivity.this, AddOffersActivity.class);
                            intent.putExtra("Id", item.getStoreid());
                            startActivity(intent);
                        }
                    });

                    addevent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(StoreDetailActivity.this, AddEventActivity.class);
                            intent.putExtra("Id", item.getStoreid());
                            startActivity(intent);
                        }
                    });

                    if(item.getUserId().equals(MainActivity.user_id)) {
                        addoffers.setVisibility(View.VISIBLE);
                        addevent.setVisibility(View.VISIBLE);
                        rlphone.setVisibility(View.GONE);
                        rlchat.setVisibility(View.GONE);
                        rldelete.setVisibility(View.VISIBLE);
                        rledit.setVisibility(View.VISIBLE);
                    } else {
                        addoffers.setVisibility(View.GONE);
                        addevent.setVisibility(View.GONE);
                        rlphone.setVisibility(View.VISIBLE);
                        rlchat.setVisibility(View.VISIBLE);
                        rldelete.setVisibility(View.GONE);
                        rledit.setVisibility(View.GONE);
                    }

                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
        tagsItem = new TagsItem(this, mTags);
        tags.setAdapter(tagsItem);
    }

    public void getDataOffers(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    JSONArray username_obj = userdata.getJSONArray("offers");
                    for (int j = 0; i < msg.length(); j++) {
                        JSONObject userdata1 = username_obj.getJSONObject(j);
                        if (!userdata1.has("nodata")) {
                            lloffers.setVisibility(View.VISIBLE);

                            OffersModels item = new OffersModels();
                            item.setOfferid(userdata1.getString("offerid"));
                            item.setName(userdata1.getString("name"));
                            item.setPrice(userdata1.getString("price"));
                            item.setImage(userdata1.getString("image"));
                            item.setAddress(userdata1.getString("address"));
                            mOffersList.add(item);
                        }
                    }


                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        offersItem = new OffersItem(this, mOffersList, R.layout.item_grid);
        rvOffers.setAdapter(offersItem);
    }

    public void getDataEvents(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    JSONArray username_obj = userdata.getJSONArray("events");
                    for (int j = 0; i < msg.length(); j++) {
                        JSONObject userdata1 = username_obj.getJSONObject(j);
                        if (!userdata1.has("nodata")) {
                            llevent.setVisibility(View.VISIBLE);
                            EventsModels item = new EventsModels();
                            item.setEventid(userdata1.getString("eventid"));
                            item.setName(userdata1.getString("name"));
                            item.setDatestart(userdata1.getString("datestart"));
                            item.setImage(userdata1.getString("image"));
                            item.setAddress(userdata1.getString("address"));
                            mEventList.add(item);
                        }
                    }

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

        eventsItem = new EventsItem(this, mEventList, R.layout.item_grid);
        rvEvents.setAdapter(eventsItem);
    }

    public void chatFragment(String senderid,String receiverid,String name,String picture){
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();

    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            likebtn.setColorFilter(getResources().getColor(R.color.colorPrimary));
        } else {
            likebtn.setColorFilter(getResources().getColor(R.color.white));
        }
    }
}
