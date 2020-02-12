package com.kenova.store.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.kenova.store.Item.ReviewItem;
import com.kenova.store.Models.ReviewModels;
import com.kenova.store.R;
import com.kenova.store.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllReviewActivity extends AppCompatActivity {

    ArrayList<ReviewModels> listItem;
    public RecyclerView recyclerView;
    ReviewItem adapter;
    ImageView backbtn;
    LinearLayout noresult;
    RelativeLayout notFound, progress;
    FloatingActionButton fab;
    String Id;
    BaseApp baseApp;
    ProgressDialog pDialog;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View rating_sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        baseApp = BaseApp.getInstance();
        Intent i = getIntent();
        Id = i.getStringExtra("Id");

        rating_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(rating_sheet);

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
                if (baseApp.getIsLogin()) {
                    rateNow();
                } else {
                    Intent intent = new Intent(AllReviewActivity.this, LoginFormActivity.class);
                    startActivity(intent);
                }
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
                (Request.Method.POST, Constants.ALLREVIEW+Id, parameters, new Response.Listener<JSONObject>() {
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
                    ReviewModels item = new ReviewModels();
                    item.setStoreid(userdata.getString("storeid"));
                    item.setUserid(userdata.getString("userid"));
                    item.setUsername(userdata.getString("fullname"));
                    item.setUserimage(userdata.getString("imageprofile"));
                    item.setRate(userdata.getString("rate"));
                    item.setReview(userdata.getString("review"));
                    listItem.add(item);
                    adapter = new ReviewItem(this, listItem, R.layout.item_review);
                    recyclerView.setAdapter(adapter);

                    if(item.getUserid().equals(MainActivity.user_id)) {
                        fab.setVisibility(View.GONE);
                    }

                }
                if(listItem.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }




            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private void rateNow() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View mDialog = getLayoutInflater().inflate(R.layout.sheet_rating, null);
        ImageView btnclose = mDialog.findViewById(R.id.bt_close);
        final RatingView ratingView = mDialog.findViewById(R.id.ratingView);
        Button submit = mDialog.findViewById(R.id.submit);
        final EditText review = mDialog.findViewById(R.id.review);
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        ratingView.setRating(0);

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(review.getText().toString())) {
                    Toast.makeText(AllReviewActivity.this, "Please Enter Messege", Toast.LENGTH_SHORT).show();
                } else {
                    pDialog = new ProgressDialog(AllReviewActivity.this);
                    pDialog.setMessage("Loading...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    if (NetworkUtils.isConnected(AllReviewActivity.this)) {
                        JSONObject parameters = new JSONObject();
                        RequestQueue rq = Volley.newRequestQueue(AllReviewActivity.this);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Constants.RATING + Id +"&rate=" + ratingView.getRating() + "&userid=" + MainActivity.user_id + "&review=" + review.getText().toString(), parameters, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String respo = response.toString();
                                        Log.d("responce", respo);
                                        pDialog.dismiss();
                                        Toast.makeText(AllReviewActivity.this, "Thanks For Review", Toast.LENGTH_SHORT).show();

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                        Log.d("respo", error.toString());
                                        Toast.makeText(AllReviewActivity.this, "Problem", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        rq.getCache().clear();
                        rq.add(jsonObjectRequest);
                        Intent intent = new Intent(AllReviewActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        mBottomSheetDialog.hide();
                    } else {
                        Toast.makeText(AllReviewActivity.this, "No connection Internet", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(mDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }
}
