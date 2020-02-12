package com.kenova.store.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.kenova.store.Models.StoresModels;
import com.squareup.picasso.MemoryPolicy;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.zanjou.http.debug.Logger;
import com.zanjou.http.request.FileUploadListener;
import com.zanjou.http.request.RequestStateListener;
import com.zanjou.http.request.Requesthttp;
import com.zanjou.http.response.JsonResponseListener;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Models.CategoryModels;
import com.kenova.store.Models.CityModels;
import com.kenova.store.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EditStoreActivity extends AppCompatActivity {
    EditText name, phone, tags, description, lat, lon;
    TextView open, closed, toolbartext;
    private LatLng destinationLocation;
    TextView address;
    LinearLayout lladdress;
    Button submit;
    ArrayList<String> propertyPurpose, catNameList, cityNameList;
    ArrayList<CategoryModels> categoriList;
    ArrayList<CityModels> cityList;
    boolean isimage = false;
    String message;
    private ArrayList<Image> imageset = new ArrayList<>();
    private final int DESTINATION_ID = 1;
    ImageView image, backbtn;
    Spinner city, category;
    ProgressDialog dialog;
    String Id;
    StoresModels item;
    ScrollView scrollView;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_store);
        dialog = new ProgressDialog(EditStoreActivity.this);
        propertyPurpose = new ArrayList<>();
        categoriList = new ArrayList<>();
        catNameList = new ArrayList<>();
        cityNameList = new ArrayList<>();
        cityList = new ArrayList<>();
        item = new StoresModels();
        Intent i = getIntent();
        Id = i.getStringExtra("Id");

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        tags = findViewById(R.id.tags);
        description = findViewById(R.id.description);
        image = findViewById(R.id.image);
        submit = findViewById(R.id.submit);
        city = findViewById(R.id.city);
        category = findViewById(R.id.category);
        lladdress = findViewById(R.id.lladdress);
        backbtn = findViewById(R.id.back_btn);
        open = findViewById(R.id.open);
        closed = findViewById(R.id.closed);
        progress = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        toolbartext = findViewById(R.id.toolbartext);

        toolbartext.setText("Edit Store");

        progress.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);


        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditStoreActivity.this, PicklocationActivity.class);
                intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
                startActivityForResult(intent, PicklocationActivity.LOCATION_PICKER_ID);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getname = name.getText().toString();
                String getphone = phone.getText().toString();
                String getaddress = address.getText().toString();
                String getdescription = description.getText().toString();
                if(TextUtils.isEmpty(getname)){
                    Toast.makeText(EditStoreActivity.this, "Please Enter Property Name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getphone)){
                    Toast.makeText(EditStoreActivity.this, "Please Enter Phone", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getaddress)){
                    Toast.makeText(EditStoreActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getdescription)){
                    Toast.makeText(EditStoreActivity.this, "Please Enter Description", Toast.LENGTH_SHORT).show();
                }
                else if(imageset ==null){
                    Toast.makeText(EditStoreActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadData();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opentime();
            }
        });

        closed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closedtime();
            }
        });

        getCity();
        getData();
        getCategory();

    }

    private void opentime() {
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                try {

                    String timeString = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date time = sdf.parse(timeString);

                    sdf = new SimpleDateFormat("hh:mm");
                    String formatedTime = sdf.format(time);
                    open.setText(formatedTime);

                } catch (Exception e) {}
            }
        }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Timepickerdialog");
    }

    private void closedtime() {
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                try {

                    String timeString = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date time = sdf.parse(timeString);

                    sdf = new SimpleDateFormat("hh:mm");
                    String formatedTime = sdf.format(time);
                    closed.setText(formatedTime);

                } catch (Exception e) {}
            }
        }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Timepickerdialog");
    }

    private void getCategory() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CATEGORY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataCategory(respo);

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

    public void getDataCategory(String loginData) {
        if (null == loginData || loginData.length() == 0) {
        } else {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        CategoryModels itemcat = new CategoryModels();
                        itemcat.setCategoryId(userdata.getString("cid"));
                        catNameList.add(userdata.getString("cname"));
                        itemcat.setCategoryImage(userdata.getString("cimage"));
                        categoriList.add(itemcat);

                        ArrayAdapter<String> catSpinner = new ArrayAdapter<>(EditStoreActivity.this, R.layout.spinner, catNameList);
                        catSpinner.setDropDownViewResource(R.layout.spinner);
                        category.setAdapter(catSpinner);
                        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                // TODO Auto-generated method stub
                                if (position == 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                                } else {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // TODO Auto-generated method stub

                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    private void getCity() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CITY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataCity(respo);
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
                    CityModels item_city = new CityModels();
                    item_city.setCityId(userdata.getString("cityid"));
                    cityNameList.add(userdata.getString("cityname"));
                    item_city.setCityImage(userdata.getString("cityimage"));
                    cityList.add(item_city);
                    ArrayAdapter<String> citySpinner = new ArrayAdapter<>(EditStoreActivity.this, R.layout.spinner, cityNameList);
                    citySpinner.setDropDownViewResource(R.layout.spinner);
                    city.setAdapter(citySpinner);
                    city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {
                            // TODO Auto-generated method stub
                            if (position == 0) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);

                            } else {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    public void chooseImage() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("add picture")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(201);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PicklocationActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String addressset = data.getStringExtra(PicklocationActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(PicklocationActivity.LOCATION_LATLNG);
                address.setText(addressset);
                destinationLocation = latLng;
                String lattext = String.valueOf(destinationLocation.latitude);
                String lontext = String.valueOf(destinationLocation.longitude);
                lat.setText(lattext);
                lon.setText(lontext);
            }
        }
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                imageset = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(imageset.get(0).getPath()));
                Picasso.with(this).load(uri).into(image);
                isimage = true;

            } else {

            }
        }

    }

    public void uploadData() {
        Requesthttp request = Requesthttp.create(Constants.UPDATESTORES);
            request.setMethod("POST")
                    .setTimeout(0)
                    .setLogger(new Logger(Logger.ERROR))
                    .addParameter("storeid", Id)
                    .addParameter("name", name.getText().toString())
                    .addParameter("description", description.getText().toString())
                    .addParameter("phone", phone.getText().toString())
                    .addParameter("tags", tags.getText().toString())
                    .addParameter("address", address.getText().toString())
                    .addParameter("latitude", lat.getText().toString())
                    .addParameter("longitude", lon.getText().toString())
                    .addParameter("cityid", cityList.get(city.getSelectedItemPosition()).getCityId())
                    .addParameter("open", open.getText().toString())
                    .addParameter("closed", closed.getText().toString())
                    .addParameter("userid", MainActivity.user_id)
                    .addParameter("cid", categoriList.get(category.getSelectedItemPosition()).getCategoryId());


        if (isimage) {
            request.addParameter("images", new File(imageset.get(0).getPath()));
        }

        request.setFileUploadListener(new FileUploadListener() {
            @Override
            public void onUploadingFile(File file, long size, long uploaded) {

            }
        })
                .setRequestStateListener(new RequestStateListener() {
                    @Override
                    public void onStart() {
                        dialog.setMessage("Please wait...");
                        dialog.setIndeterminate(false);
                        dialog.setCancelable(true);
                        dialog.show();
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new JsonResponseListener() {
                    @Override
                    public void onOkResponse(JSONObject jsonObject) throws JSONException {
                        JSONArray jsonArray = jsonObject.getJSONArray("200");
                        JSONObject objJson = jsonArray.getJSONObject(0);
                        Constants.successmsg = objJson.getInt("success");
                        message = objJson.getString("msg");
                        if (Constants.successmsg == 0) {

                            Toast.makeText(EditStoreActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditStoreActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditStoreActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(JSONObject jsonObject) {

                    }

                    @Override
                    public void onParseError(JSONException e) {

                    }
                }).execute();
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
                        progress.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
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
                    item.setName(userdata.getString("name"));
                    item.setPhone(userdata.getString("phone"));
                    item.setOpen(userdata.getString("open"));
                    item.setClosed(userdata.getString("closed"));
                    item.setTags(userdata.getString("tags"));
                    item.setAddress(userdata.getString("address"));
                    item.setCid(userdata.getString("cid"));
                    item.setCityId(userdata.getString("cityid"));
                    item.setCname(userdata.getString("cname"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setImage(userdata.getString("images"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    item.setDescription(userdata.getString("description"));

                    name.setText(item.getName());
                    phone.setText(item.getPhone());
                    address.setText(item.getAddress());
                    open.setText(item.getOpen());
                    closed.setText(item.getclosed());
                    tags.setText(item.getTags());
                    description.setText(item.getDescription());
                    lat.setText(item.getLatitude());
                    lon.setText(item.getLongitude());

                    Picasso.with(this)
                            .load(item.getImage())
                            .resize(250,250)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.image_placeholder)
                            .into(image);

                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

}
