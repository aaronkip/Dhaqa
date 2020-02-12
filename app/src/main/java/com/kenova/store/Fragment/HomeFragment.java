package com.kenova.store.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kenova.store.Activity.AllEventActivity;
import com.kenova.store.Activity.AllOffersActivity;
import com.kenova.store.Constants.BaseApp;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Item.CategoryItem;
import com.kenova.store.Item.CityItem;
import com.kenova.store.Item.EventsItem;
import com.kenova.store.Item.OffersItem;
import com.kenova.store.Item.SliderItem;
import com.kenova.store.Models.CategoryModels;
import com.kenova.store.Models.CityModels;
import com.kenova.store.Models.EventsModels;
import com.kenova.store.Models.OffersModels;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment  {

    ArrayList<StoresModels> mSliderList;
    ArrayList<OffersModels> mOffersList;
    ArrayList<EventsModels> mEventList;
    ArrayList<CityModels> mCityList;
    ArrayList<CategoryModels> mCategoryList;
    RecyclerView rvCategory, rvOffers, rvEvents, rvCity;
    CategoryItem categoryItem;
    OffersItem offersItem;
    EventsItem eventsItem;
    CityItem cityItem;
    SliderItem sliderItem;
    ScrollView mScrollView;
    TextView moreoffers, moreevent;
    ProgressBar mProgressBar, progressBaroffers, progressBarevent, progressBarcity;
    ViewPager mViewPager;
    CircleIndicator circleIndicator;
    BaseApp baseApp;
    ImageView moreCategory;
    RelativeLayout nofound, rlmore;
    LinearLayout rlslider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mSliderList = new ArrayList<>();
        mCategoryList = new ArrayList<>();
        mOffersList = new ArrayList<>();
        mEventList = new ArrayList<>();
        mCityList = new ArrayList<>();
        baseApp = BaseApp.getInstance();
        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mViewPager = rootView.findViewById(R.id.viewPager);
        rlslider = rootView.findViewById(R.id.rlslider);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);
        nofound = rootView.findViewById(R.id.nofound);
        rvCategory = rootView.findViewById(R.id.category);
        rvOffers = rootView.findViewById(R.id.popularoffers);
        rvEvents = rootView.findViewById(R.id.popularevents);
        rvCity = rootView.findViewById(R.id.city);
        moreCategory = rootView.findViewById(R.id.morecategory);
        rlmore = rootView.findViewById(R.id.rlmore);
        //moreoffers = rootView.findViewById(R.id.morepopularoffers);
        moreevent = rootView.findViewById(R.id.morepopularevents);
        progressBarcity = rootView.findViewById(R.id.progresscity);
        progressBarevent = rootView.findViewById(R.id.progressevents);
        progressBaroffers = rootView.findViewById(R.id.progressoffers);

        progressBaroffers.setVisibility(View.VISIBLE);
        progressBarevent.setVisibility(View.VISIBLE);
        progressBarcity.setVisibility(View.VISIBLE);

       /* moreoffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(getActivity());
                Intent intent = new Intent(getActivity(), AllOffersActivity.class);
                getActivity().startActivity(intent);
            }
        });
*/
        moreevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(getActivity());
                Intent intent = new Intent(getActivity(), AllEventActivity.class);
                getActivity().startActivity(intent);
            }
        });

        moreCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        rvOffers.setHasFixedSize(true);
        rvOffers.setNestedScrollingEnabled(false);
        rvOffers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvEvents.setHasFixedSize(true);
        rvEvents.setNestedScrollingEnabled(false);
        rvEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvCity.setHasFixedSize(true);
        rvCity.setNestedScrollingEnabled(false);
        rvCity.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        mProgressBar.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.GONE);
        if (NetworkUtils.isConnected(requireActivity())) {
            getFeaturedItem();
            getCategory();
            getOffers();
            getEvents();
            getCity();

        } else {
            nofound.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }


            return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();

        }
    }



    private void getFeaturedItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, Constants.FEATURED + "&user_lat=" + Constants.LATITUDE + "&user_long=" + Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String respo = response.toString();
                            Log.d("response", respo);
                            getData(respo);

                            mScrollView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            rlslider.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.d("respo", error.toString());
                        }
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.getCache().clear();
            rq.add(jsonObjectRequest);

    }

    public void getData(String loginData){

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
                    item.setAddress(userdata.getString("address"));
                    item.setDistance(userdata.getString("distance"));
                    item.setImage(userdata.getString("images"));
                    item.setRateAvg(userdata.getString("rate"));
                    mSliderList.add(item);


                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

        displayData();
    }

    private void getCategory() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CATEGORY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataCategory(respo);
                        mScrollView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
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

    public void getDataCategory(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    CategoryModels item = new CategoryModels();
                    item.setCategoryId(userdata.getString("cid"));
                    item.setCategoryName(userdata.getString("cname"));
                    item.setCategoryImage(userdata.getString("cimage"));
                    mCategoryList.add(item);

                }
                if(mCategoryList.isEmpty()) {
                    rlmore.setVisibility(View.GONE);
                } else {
                    rlmore.setVisibility(View.VISIBLE);
                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getOffers() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.OFFERS+"&user_lat="+Constants.LATITUDE +"&user_long="+Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataOffers(respo);
                        mScrollView.setVisibility(View.VISIBLE);
                        progressBaroffers.setVisibility(View.GONE);
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
                    OffersModels item = new OffersModels();
                    item.setOfferid(userdata.getString("offerid"));
                    item.setName(userdata.getString("name"));
                    item.setPrice(userdata.getString("price"));
                    item.setImage(userdata.getString("image"));
                    item.setAddress(userdata.getString("address"));
                    mOffersList.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getEvents() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.EVENTS+"&user_lat="+Constants.LATITUDE +"&user_long="+Constants.LONGITUDE, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataEvents(respo);
                        mScrollView.setVisibility(View.VISIBLE);
                        progressBarevent.setVisibility(View.GONE);
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
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getCity() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CITY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataCity(respo);
                        mScrollView.setVisibility(View.VISIBLE);
                        progressBarcity.setVisibility(View.GONE);
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

    public void getDataCity(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    CityModels item = new CityModels();
                    item.setCityId(userdata.getString("cityid"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setCityImage(userdata.getString("cityimage"));
                    mCityList.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }


    private void displayData() {
        sliderItem = new SliderItem(getActivity(), mSliderList);
        mViewPager.setAdapter(sliderItem);
        circleIndicator.setViewPager(mViewPager);

        categoryItem = new CategoryItem(getActivity(), mCategoryList, R.layout.item_category);
        rvCategory.setAdapter(categoryItem);

        offersItem = new OffersItem(getActivity(), mOffersList, R.layout.item_grid);
        rvOffers.setAdapter(offersItem);

        eventsItem = new EventsItem(getActivity(), mEventList, R.layout.item_grid);
        rvEvents.setAdapter(eventsItem);

        cityItem = new CityItem(getActivity(), mCityList, R.layout.item_square);
        rvCity.setAdapter(cityItem);

        if(mSliderList.isEmpty()){
            rlslider.setVisibility(View.GONE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();


    }


}