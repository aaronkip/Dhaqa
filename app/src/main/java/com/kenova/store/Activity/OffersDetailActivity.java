package com.kenova.store.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.kenova.store.Constants.BaseApp;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Item.TagsItem;
import com.kenova.store.Models.OffersModels;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.DatabaseHelper;
import com.kenova.store.Utils.NetworkUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import customfonts.Button_SF_Pro_Display_Medium;

/**
 * Created by kenova on 3/26/2019.
 */

public class OffersDetailActivity extends AppCompatActivity {

    TextView name, address, price, storename, totaluser, date, toolbartext;
    WebView description;
    FloatingActionButton fab;
    String Id;
    Button interested;
    RelativeLayout rledt, rldelete;
    BaseApp baseApp;
    RecyclerView tags;
    TagsItem tagsItem;
    RelativeLayout progress;
    ProgressDialog pDialog;
    ArrayList<OffersModels> mOffersList;
    ArrayList<String> mTags;
    DatabaseHelper databaseHelper;
    StoresModels item;
    ImageView backbtn, image;
    Button_SF_Pro_Display_Medium order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_detail);
        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        baseApp = BaseApp.getInstance();
        mTags = new ArrayList<>();
        mOffersList = new ArrayList<>();

        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        item = new StoresModels();
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        fab = findViewById(R.id.fab);
        description = findViewById(R.id.description);
        backbtn = findViewById(R.id.back_btn);
        progress = findViewById(R.id.progress);
        tags = findViewById(R.id.tags);
        image = findViewById(R.id.image);
        price = findViewById(R.id.price);
        storename = findViewById(R.id.storename);
        totaluser =  findViewById(R.id.totaluser);
        date = findViewById(R.id.date);
        interested = findViewById(R.id.interested);
        rldelete = findViewById(R.id.rldelete);
        rledt = findViewById(R.id.rledit);
        toolbartext =findViewById(R.id.toolbartext);
        order= findViewById(R.id.order);

        toolbartext.setText("Offer Detail");

        progress.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);
        tags.setHasFixedSize(true);
        tags.setNestedScrollingEnabled(false);
        tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getOffers();
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

        order.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View view)
                {
                    Intent navigation = new Intent(OffersDetailActivity.this, ShoppingCheckoutStep.class);
                    startActivity(navigation);
                }
            });

        tags.setHasFixedSize(true);
        tags.setNestedScrollingEnabled(false);
        tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
                (Request.Method.POST, Constants.DELETEOFFERS+Id, parameters, new Response.Listener<JSONObject>() {
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

    private void getOffers() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.OFFERSDETAIL+Id, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataOffers(respo);
                        progress.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
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

    public void getDataOffers(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    final OffersModels item = new OffersModels();
                    item.setOfferid(userdata.getString("offerid"));
                    item.setName(userdata.getString("name"));
                    item.setDescription(userdata.getString("description"));
                    item.setAddress(userdata.getString("address"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    item.setTags(userdata.getString("tags"));
                    item.setDatestart(userdata.getString("date"));
                    item.setDateend(userdata.getString("dateend"));
                    item.setPrice(userdata.getString("price"));
                    item.setImage(userdata.getString("image"));
                    item.setInterested(userdata.getString("interested"));
                    item.setStoreid(userdata.getString("storeid"));
                    item.setStorename(userdata.getString("storename"));
                    item.setUserid(userdata.getString("userid"));
                    mOffersList.add(item);

                    name.setText(item.getName());
                    address.setText(item.getAddress());

                    Double getprice = Double.valueOf(item.getPrice());
                    String total = String.format(Locale.US, "$%s",
                    NumberFormat.getNumberInstance(Locale.US).format(getprice));
                    price.setText(total);

                    storename.setText(item.getStorename());
                    totaluser.setText(item.getInterested());
                    date.setText(item.getDatestart()+"-"+item.getDateend());

                    storename.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BannerAds.ShowInterstitialAds(OffersDetailActivity.this);
                            Intent intent = new Intent(OffersDetailActivity.this, StoreDetailActivity.class);
                            intent.putExtra("Id", item.getStoreid());
                            startActivity(intent);

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

                    Picasso.with(this)
                            .load(item.getImage())
                            .resize(250,250)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.image_placeholder)
                            .into(image);

                    rledt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OffersDetailActivity.this, EditOffersActivity.class);
                            intent.putExtra("Id", item.getStoreid());
                            intent.putExtra("offerId", item.getOfferid());
                            startActivity(intent);
                        }
                    });

                    if(item.getUserid().equals(MainActivity.user_id)) {
                        rldelete.setVisibility(View.VISIBLE);
                        rledt.setVisibility(View.VISIBLE);
                        interested.setVisibility(View.GONE);
                    } else {
                        rldelete.setVisibility(View.GONE);
                        rledt.setVisibility(View.GONE);
                        interested.setVisibility(View.VISIBLE);
                    }

                    if (!item.getTags().isEmpty())
                        mTags = new ArrayList<>(Arrays.asList(item.getTags().split(",")));

                    interested.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (baseApp.getIsLogin()) {

                            pDialog = new ProgressDialog(OffersDetailActivity.this);
                            pDialog.setMessage("Loading...");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            if (NetworkUtils.isConnected(OffersDetailActivity.this)) {
                                JSONObject parameters = new JSONObject();
                                RequestQueue rq = Volley.newRequestQueue(OffersDetailActivity.this);
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                        (Request.Method.POST, Constants.INTERESTED+Id+"&userid="+ MainActivity.user_id, parameters, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                String respo=response.toString();
                                                Log.d("responce",respo);
                                                pDialog.dismiss();
                                                Toast.makeText(OffersDetailActivity.this, "You are interested", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // TODO: Handle error
                                                Log.d("respo",error.toString());
                                                Toast.makeText(OffersDetailActivity.this, "Problem", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                rq.getCache().clear();
                                rq.add(jsonObjectRequest);
                            } else {
                                Toast.makeText(OffersDetailActivity.this, "No connection Internet", Toast.LENGTH_SHORT).show();
                            }
                            } else {
                                Intent intent = new Intent(OffersDetailActivity.this, LoginFormActivity.class);
                                startActivity(intent);
                            }

                        }
                    });

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        tagsItem = new TagsItem(this, mTags);
        tags.setAdapter(tagsItem);
    }

}
