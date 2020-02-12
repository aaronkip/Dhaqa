package com.kenova.store.Activity;

import android.content.Intent;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Item.ListItem;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyStoreActivity extends AppCompatActivity {

    ArrayList<StoresModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    ImageView backbtn;
    LinearLayout noresult;
    RelativeLayout notFound, progress;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        listItem = new ArrayList<>();
        noresult = findViewById(R.id.noresult);
        recyclerView = findViewById(R.id.recycle);
        notFound = findViewById(R.id.notfound);
        progress = findViewById(R.id.progress);
        backbtn = findViewById(R.id.back_btn);
        fab = findViewById(R.id.fab);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        progress.setVisibility(View.VISIBLE);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStoreActivity.this, AddStoresActivity.class);
                startActivity(intent);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }



    private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.MYSTORES+MainActivity.user_id+"&user_lat=" + Constants.LATITUDE + "&user_long=" + Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataAll(respo);
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

    public void getDataAll(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    StoresModels item = new StoresModels();
                    item.setStoreid(userdata.getString("storeid"));
                    item.setName(userdata.getString("name"));
                    item.setImage(userdata.getString("images"));
                    item.setDistance(userdata.getString("distance"));
                    item.setAddress(userdata.getString("address"));
                    item.setCname(userdata.getString("cname"));
                    item.setRateAvg(userdata.getString("rate"));
                    listItem.add(item);
                    adapter = new ListItem(this, listItem, R.layout.item_list);
                    recyclerView.setAdapter(adapter);
                }
                if(listItem.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }
}
