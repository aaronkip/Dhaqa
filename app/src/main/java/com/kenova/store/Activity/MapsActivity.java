package com.kenova.store.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.BubbleTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    GoogleMap gMap;
    CameraPosition cameraPosition;
    LatLng center, latLng;
    String title, image, Id;
    private ImageView icon, marker_bg, backbtn;
    private View marker_view;
    private Location lastKnownLocation;
    StoresModels item;
    ProgressBar progress;
    RelativeLayout rlmap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        item = new StoresModels();
        backbtn = findViewById(R.id.back_btn);
        progress = findViewById(R.id.progressBar);
        rlmap = findViewById(R.id.rlmap);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progress.setVisibility(View.VISIBLE);
        rlmap.setVisibility(View.GONE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        marker_view = inflater.inflate(R.layout.maps_marker, null);
        icon = (ImageView) marker_view.findViewById(R.id.marker_icon);
        marker_bg = (ImageView) marker_view.findViewById(R.id.marker_bg);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        Double lat= Double.valueOf(Constants.LATITUDE);
        Double lon= Double.valueOf(Constants.LONGITUDE);
        center = new LatLng(lat, lon);
        cameraPosition = new CameraPosition.Builder().target(center).zoom(8).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        getFeaturedItem();
    }

    private void addMarker(final LatLng latlng, final String title, String image, final String Id) {
        CameraUpdate location;
        Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Marker mk = gMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(title)

                );

                mk.setTag(Id);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("picasso", "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(this)
                .load(image)
                .resize(250,250)
                .centerCrop()
                .transform(new BubbleTransformation(5))
                .into(mTarget);

        location = CameraUpdateFactory.newLatLngZoom(latlng, 12);
        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                BannerAds.ShowInterstitialAds(MapsActivity.this);
                Intent intent = new Intent(MapsActivity.this, StoreDetailActivity.class);
                intent.putExtra("Id", (String) marker.getTag());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            } else {
            }
        }
    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15f)
            );

            gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));

        }
    }
    private void getFeaturedItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLSTORE, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        getData(respo);
                        progress.setVisibility(View.GONE);
                        rlmap.setVisibility(View.VISIBLE);
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
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    Id = item.getStoreid();
                    title = item.getName();
                    image = item.getImage();
                    latLng = new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));
                    addMarker(latLng, title, image, Id);

                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

}