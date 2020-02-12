package com.kenova.store.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kenova.store.Constants.Constants;
import com.kenova.store.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kenova.store.Fragment.EnableLlocationFragment;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class RegisterActivity extends AppCompatActivity {
    com.kenova.store.Constants.BaseApp BaseApp;
    ImageView profileImage, backbtn;
    EditText fullName;
    SharedPreferences sharedPreferences;

    DatabaseReference rootref;

    Button submit;
    IOSDialog iosDialog;

    ImageButton editProfileImage;
    byte [] imageByteArray;
    String phone_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        BaseApp = com.kenova.store.Constants.BaseApp.getInstance();
        phone_no =getIntent().getExtras().getString("number");
        phone_no = phone_no.replace("+","");


        sharedPreferences = getSharedPreferences(Constants.pref_name, Context.MODE_PRIVATE);

        rootref = FirebaseDatabase.getInstance().getReference();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();


        profileImage = findViewById(R.id.profileimage);
        fullName = findViewById(R.id.firstname);
        editProfileImage = findViewById(R.id.editphotoprofile);
        submit = findViewById(R.id.submit);
        backbtn = findViewById(R.id.back_btn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String f_name= fullName.getText().toString();

                if(imageByteArray ==null){
                    Toast.makeText(RegisterActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(f_name)){

                    Toast.makeText(RegisterActivity.this, "Please enter your First Name", Toast.LENGTH_SHORT).show();

                }
                else {

                    SaveData();
                }

            }
        });

    }

    private void selectImage() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            if (requestCode == 2) {

                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
            }
            else if (requestCode == 123) {
                handleCrop(resultCode, data);
            }

        }

    }



    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this,123);
    }


    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri group_image_uri=Crop.getOutput(result);
            profileImage.setImageBitmap(null);
            profileImage.setImageURI(null);
            profileImage.setImageURI(group_image_uri);


            Bitmap bmpSample = BitmapFactory.decodeFile(group_image_uri.getPath());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmpSample.compress(Bitmap.CompressFormat.JPEG, 100, out);

            imageByteArray =out.toByteArray();




        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void SaveData(){
        iosDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filelocation = storageReference.child("User_image")
                .child(phone_no + ".jpg");
        filelocation.putBytes(imageByteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                CallSignup(phone_no,
                        fullName.getText().toString(),
                        taskSnapshot.getDownloadUrl().toString());

            }});


    }

    private void CallSignup(String user_id,
                                     String f_name, String picture) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", user_id);
            parameters.put("fullname",f_name);
            parameters.put("imageprofile",picture);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.SIGNUP, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        iosDialog.cancel();
                        signupData(respo);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Toast.makeText(RegisterActivity.this, "Somthing Wrong with Api", Toast.LENGTH_LONG).show();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void signupData(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray jsonArray=jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Constants.uid,userdata.optString("userid"));
                editor.putString(Constants.f_name,userdata.optString("fullname"));
                editor.putString(Constants.u_pic,userdata.optString("imageprofile"));
                BaseApp.saveIsLogin(true);
                editor.commit();

                enableLocation();
            }else {
                Toast.makeText(this, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }


    private void enableLocation() {
        EnableLlocationFragment enable_llocationFragment = new EnableLlocationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.splash, enable_llocationFragment).addToBackStack(null).commit();

    }



}
