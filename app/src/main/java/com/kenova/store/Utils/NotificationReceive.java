package com.kenova.store.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenova.store.Fragment.ChatFragment;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Activity.MainActivity;
import com.kenova.store.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kenova on 12/22/2018.
 */

public class NotificationReceive extends FirebaseMessagingService {

    String  pic;
    String  title;
    String  message;
    String senderid;
    String receiverid;
    SharedPreferences sharedPreferences;

    sendNotification sendNotification=new sendNotification(this);

    Handler handler=new Handler();
    Runnable runnable;

    Snackbar snackbar;

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            sharedPreferences=getSharedPreferences(Constants.pref_name,MODE_PRIVATE);
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
            pic=remoteMessage.getData().get("icon");
            senderid=remoteMessage.getData().get("senderid");
            receiverid=remoteMessage.getData().get("receiverid");
                if(!ChatFragment.senderidForCheckNotification.equals(senderid)){
                     sendNotification.execute(pic);

                    }

            }


        }



    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sharedPreferences= getSharedPreferences(Constants.pref_name,MODE_PRIVATE);
        if((s!=null || !s.equals("null")) && sharedPreferences.getString(Constants.device_token,"null").equals("null"))
        sharedPreferences.edit().putString(Constants.device_token,s).commit();

    }

    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;

        public sendNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            // in notification first we will get the image of the user and then we will show the notification to user
            // in onPostExecute
            InputStream in;
            try {

                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);


            if(MainActivity.mainActivity !=null){


                if(snackbar !=null){
                    snackbar.getView().setVisibility(View.INVISIBLE);
                    snackbar.dismiss();
                }

                if(handler!=null && runnable!=null) {
                    handler.removeCallbacks(runnable);
                }


              View layout = MainActivity.mainActivity.getLayoutInflater().inflate(R.layout.item_layout_custom_notification,null);
              TextView titletxt= layout.findViewById(R.id.fullname);
              TextView messagetxt=layout.findViewById(R.id.message);
              ImageView imageView=layout.findViewById(R.id.userimages);
              titletxt.setText(title);
              messagetxt.setText(message);
              imageView.setImageBitmap(result);


              snackbar = Snackbar.make(MainActivity.mainActivity.findViewById(R.id.container), "", Snackbar.LENGTH_LONG);

                Snackbar.SnackbarLayout snackbarLayout= (Snackbar.SnackbarLayout) snackbar.getView();
                TextView textView = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
                textView.setVisibility(View.INVISIBLE);

                final ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
                if (params instanceof CoordinatorLayout.LayoutParams) {
                    ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
                } else {
                    ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
                }

                snackbarLayout.setPadding(0,50,0,0);
                snackbarLayout.addView(layout, 0);


                snackbar.getView().setVisibility(View.INVISIBLE);

                snackbar.setCallback(new Snackbar.Callback(){
                    @Override
                    public void onShown(Snackbar sb) {
                        super.onShown(sb);
                        snackbar.getView().setVisibility(View.VISIBLE);
                    }

                });


                runnable=new Runnable() {
                    @Override
                    public void run() {
                        snackbar.getView().setVisibility(View.INVISIBLE);

                    }
                };

               handler.postDelayed(runnable, 30000);


                snackbar.setDuration(Snackbar.LENGTH_LONG);
                snackbar.show();



                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        snackbar.dismiss();
                        snackbar.getView().setVisibility(View.INVISIBLE);
                        chatFragment(MainActivity.user_id,senderid,title);

                    }
                });


            }

        }}



    public void chatFragment(String senderid,String receiverid,String name){
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = MainActivity.mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",pic);
        args.putString("name",name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();
    }




}
