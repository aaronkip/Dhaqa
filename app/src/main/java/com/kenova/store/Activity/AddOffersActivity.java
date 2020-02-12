package com.kenova.store.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Models.StoresModels;
import com.kenova.store.R;
import com.kenova.store.Utils.Tools;
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


public class AddOffersActivity extends AppCompatActivity {
    EditText name, tags, description, lat, lon, price;
    TextView open, closed, toolbartext;
    private LatLng destinationLocation;
    TextView address;
    LinearLayout lladdress;
    Button submit;
    boolean isimage = false;
    String message;
    private ArrayList<Image> imageset = new ArrayList<>();
    private final int DESTINATION_ID = 1;
    ImageView image, backbtn;
    ProgressDialog dialog;
    String Id;
    StoresModels item;
    ScrollView scrollView;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_offers);
        dialog = new ProgressDialog(AddOffersActivity.this);
        item = new StoresModels();
        Intent i = getIntent();
        Id = i.getStringExtra("Id");

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
        price = findViewById(R.id.price);

        toolbartext.setText("Add Offers");
        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddOffersActivity.this, PicklocationActivity.class);
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
                    Toast.makeText(AddOffersActivity.this, "Please Enter Store Name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getopen)){
                    Toast.makeText(AddOffersActivity.this, "Please Enter Open Time", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getclosed)){
                    Toast.makeText(AddOffersActivity.this, "Please Enter Closed Time", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(gettags)){
                    Toast.makeText(AddOffersActivity.this, "Please Enter Tags", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getaddress)){
                    Toast.makeText(AddOffersActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getdescription)){
                    Toast.makeText(AddOffersActivity.this, "Please Enter Description", Toast.LENGTH_SHORT).show();
                }
                else if(imageset ==null){
                    Toast.makeText(AddOffersActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
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
        Requesthttp request = Requesthttp.create(Constants.ADDOFFERS);
            request.setMethod("POST")
                    .setTimeout(0)
                    .setLogger(new Logger(Logger.ERROR))
                    .addParameter("offersname", name.getText().toString())
                    .addParameter("price", price.getText().toString())
                    .addParameter("storeid", Id)
                    .addParameter("odescription", description.getText().toString())
                    .addParameter("otags", tags.getText().toString())
                    .addParameter("oaddress", address.getText().toString())
                    .addParameter("olatitude", lat.getText().toString())
                    .addParameter("olongitude", lon.getText().toString())
                    .addParameter("datestart", open.getText().toString())
                    .addParameter("dateend", closed.getText().toString())
                    .addParameter("userid", MainActivity.user_id);


        if (isimage) {
            request.addParameter("image", new File(imageset.get(0).getPath()));
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

                            Toast.makeText(AddOffersActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddOffersActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddOffersActivity.this, MainActivity.class);
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



}
