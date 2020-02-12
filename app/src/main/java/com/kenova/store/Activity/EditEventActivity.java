package com.kenova.store.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Models.EventsModels;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.Tools;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zanjou.http.debug.Logger;
import com.zanjou.http.request.FileUploadListener;
import com.zanjou.http.request.RequestStateListener;
import com.zanjou.http.request.Requesthttp;
import com.zanjou.http.response.JsonResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


public class EditEventActivity extends AppCompatActivity {
    EditText name, tags, description, lat, lon;
    TextView open, closed, toolbartext;
    private LatLng destinationLocation;
    TextView address;
    LinearLayout lladdress, llprice;
    Button submit;
    boolean isimage = false;
    String message;
    private ArrayList<Image> imageset = new ArrayList<>();
    private final int DESTINATION_ID = 1;
    ImageView image, backbtn;
    ProgressDialog dialog;
    String Id, eventId;
    StoresModels item;
    ScrollView scrollView;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_offers);
        dialog = new ProgressDialog(EditEventActivity.this);
        item = new StoresModels();
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        eventId = i.getStringExtra("eventId");

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        tags = findViewById(R.id.tags);
        description = findViewById(R.id.description);
        image = findViewById(R.id.image);
        submit = findViewById(R.id.submit);
        lladdress = findViewById(R.id.lladdress);
        backbtn = findViewById(R.id.back_btn);
        open = findViewById(R.id.open);
        closed = findViewById(R.id.closed);
        progress = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        toolbartext = findViewById(R.id.toolbartext);
        llprice = findViewById(R.id.llprice);

        llprice.setVisibility(View.GONE);

        toolbartext.setText("Edit Event");
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
                Intent intent = new Intent(EditEventActivity.this, PicklocationActivity.class);
                intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
                startActivityForResult(intent, PicklocationActivity.LOCATION_PICKER_ID);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getname = name.getText().toString();
                String getaddress = address.getText().toString();
                String getopen = open.getText().toString();
                String getclosed = closed.getText().toString();
                String gettags = tags.getText().toString();
                String getdescription = description.getText().toString();
                if(TextUtils.isEmpty(getname)){
                    Toast.makeText(EditEventActivity.this, "Please Enter Store Name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getopen)){
                    Toast.makeText(EditEventActivity.this, "Please Enter Open Time", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getclosed)){
                    Toast.makeText(EditEventActivity.this, "Please Enter Closed Time", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(gettags)){
                    Toast.makeText(EditEventActivity.this, "Please Enter Tags", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getaddress)){
                    Toast.makeText(EditEventActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getdescription)){
                    Toast.makeText(EditEventActivity.this, "Please Enter Description", Toast.LENGTH_SHORT).show();
                }
                else if(imageset ==null){
                    Toast.makeText(EditEventActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
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
        getData();

    }

    private void opentime() {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        ((TextView) findViewById(R.id.open)).setText(Tools.getFormattedDateSimple(date_ship_millis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    private void closedtime() {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        ((TextView) findViewById(R.id.closed)).setText(Tools.getFormattedDateSimple(date_ship_millis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
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
        Requesthttp request = Requesthttp.create(Constants.EDITEVENT);
            request.setMethod("POST")
                    .setTimeout(0)
                    .setLogger(new Logger(Logger.ERROR))
                    .addParameter("eventid", eventId)
                    .addParameter("eventname", name.getText().toString())
                    .addParameter("storeid", Id)
                    .addParameter("edescription", description.getText().toString())
                    .addParameter("etags", tags.getText().toString())
                    .addParameter("eaddress", address.getText().toString())
                    .addParameter("elatitude", lat.getText().toString())
                    .addParameter("elongitude", lon.getText().toString())
                    .addParameter("datestart", open.getText().toString())
                    .addParameter("dateend", closed.getText().toString())
                    .addParameter("userid", MainActivity.user_id);


        if (isimage) {
            request.addParameter("eimage", new File(imageset.get(0).getPath()));
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

                            Toast.makeText(EditEventActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditEventActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditEventActivity.this, MainActivity.class);
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
                (Request.Method.POST, Constants.EVENTDATA +eventId, parameters, new Response.Listener<JSONObject>() {
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
                    final EventsModels item = new EventsModels();
                    item.setEventid(userdata.getString("eventid"));
                    item.setName(userdata.getString("name"));
                    item.setDescription(userdata.getString("description"));
                    item.setAddress(userdata.getString("address"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    item.setTags(userdata.getString("tags"));
                    item.setDatestart(userdata.getString("date"));
                    item.setDateend(userdata.getString("dateend"));
                    item.setImage(userdata.getString("image"));
                    item.setUserid(userdata.getString("userid"));
                    item.setInterested(userdata.getString("participate"));
                    item.setStoreid(userdata.getString("storeid"));
                    item.setStorename(userdata.getString("storename"));

                    name.setText(item.getName());
                    address.setText(item.getAddress());
                    tags.setText(item.getTags());
                    description.setText(item.getDescription());
                    lat.setText(item.getLatitude());
                    lon.setText(item.getLongitude());
                    open.setText(item.getDatestart());
                    closed.setText(item.getDateend());

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
