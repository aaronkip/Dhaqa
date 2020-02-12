package com.kenova.store.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.kenova.store.Models.CategoryModels;
import com.kenova.store.Models.CityModels;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StoresFragment extends Fragment {
    View getView;
    Context context;
    ArrayList<StoresModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    String sortdata;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    ArrayList<String> catNameList, cityNameList;
    ArrayList<CategoryModels> mListCat;
    ArrayList<CityModels> mListCity;
    ImageView search, backbtn;
    LinearLayout noresult;
    RelativeLayout notFound, progress;
    FloatingActionButton fab;
    int save_sort=1;
    Toolbar toolbar;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_recycle, container, false);
        context = getContext();
        bottom_sheet = getView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        listItem = new ArrayList<>();
        mListCat= new ArrayList<>();
        cityNameList= new ArrayList<>();
        catNameList= new ArrayList<>();
        mListCity= new ArrayList<>();
        recyclerView = getView.findViewById(R.id.recycle);
        fab = getView.findViewById(R.id.fab);
        notFound = getView.findViewById(R.id.notfound);
        progress = getView.findViewById(R.id.progress);
        backbtn = getView.findViewById(R.id.back_btn);
        search = getView.findViewById(R.id.search);
        toolbar = getView.findViewById(R.id.toolbar);
        noresult = getView.findViewById(R.id.noresult);
        toolbar.setVisibility(View.GONE);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        progress.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        if (NetworkUtils.isConnected(requireActivity())) {
            getData();
        }



        return getView;
    }

    private void sort() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View mDialog = getLayoutInflater().inflate(R.layout.sheet_list, null);
        RadioGroup radioGroupSort = mDialog.findViewById(R.id.myRadioGroup);
        RadioButton filter_dis = mDialog.findViewById(R.id.sort_distance);
        RadioButton filter_high = mDialog.findViewById(R.id.sort_rating);
        RadioButton filter_all=mDialog.findViewById(R.id.sort_all);

        if(save_sort==1) {
            filter_all.setChecked(true);
        }else if(save_sort==2){
            filter_high.setChecked(true);
        }
        else if(save_sort==3){
            filter_dis.setChecked(true);
        }
        sortdata = "DESC";
        radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int Check) {
                listItem.clear();
                if (Check == R.id.sort_distance) {
                    save_sort=3;
                    if (NetworkUtils.isConnected(context)) {
                        JSONObject parameters = new JSONObject();
                        RequestQueue rq = Volley.newRequestQueue(context);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Constants.DISTANCE+"&user_lat="+Constants.LATITUDE +"&user_long="+Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
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
                }
                if (Check == R.id.sort_rating) {
                    save_sort=2;
                    if (NetworkUtils.isConnected(context)) {
                        JSONObject parameters = new JSONObject();
                        RequestQueue rq = Volley.newRequestQueue(context);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Constants.BYRATING+"&user_lat="+Constants.LATITUDE +"&user_long="+Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
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
                }
                else if (Check == R.id.sort_all) {
                    save_sort=1;
                    sortdata = ("Sort All");
                    if (NetworkUtils.isConnected(context)) {
                        getData();
                    }}

            }
        });

        mBottomSheetDialog = new BottomSheetDialog(context);
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

     private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLSTORE+"&user_lat=" + Constants.LATITUDE + "&user_long=" + Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
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

                }
                if(listItem.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        adapter = new ListItem(context, listItem, R.layout.item_list);
        recyclerView.setAdapter(adapter);
    }


}
