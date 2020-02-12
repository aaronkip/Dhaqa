package com.kenova.store.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Item.AllEventsItem;
import com.kenova.store.Models.EventsModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllEventActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    ArrayList<EventsModels> mEventList;
    AllEventsItem eventItem;
    ImageView backbtn;
    LinearLayout noresult;
    FloatingActionButton fab;
    RelativeLayout notFound, progress;
    TextView toolbarname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        recyclerView = findViewById(R.id.recycle);
        notFound = findViewById(R.id.notfound);
        mEventList = new ArrayList<>();
        progress = findViewById(R.id.progress);
        backbtn = findViewById(R.id.back_btn);
        noresult = findViewById(R.id.noresult);
        toolbarname = findViewById(R.id.toolbarname);
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progress.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        getEvents();
    }

    private void getEvents() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLEVENTS+"&user_lat="+Constants.LATITUDE +"&user_long="+Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataEvents(respo);
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

    public void getDataEvents(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    EventsModels item = new EventsModels();
                    item.setEventid(userdata.getString("eventid"));
                    item.setName(userdata.getString("name"));
                    item.setDatestart(userdata.getString("datestart"));
                    item.setImage(userdata.getString("image"));
                    item.setAddress(userdata.getString("address"));
                    mEventList.add(item);


                }

                if(mEventList.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        eventItem = new AllEventsItem(this, mEventList, R.layout.item_grid);
        recyclerView.setAdapter(eventItem);
    }


}
