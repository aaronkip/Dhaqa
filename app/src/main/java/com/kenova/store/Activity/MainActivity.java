package com.kenova.store.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kenova.store.Constants.BaseApp;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Constants.VersionChecker;
import com.kenova.store.Fragment.HomeFragment;
import com.kenova.store.Fragment.MessageFragment;
import com.kenova.store.Fragment.StoresFragment;
import com.kenova.store.Models.AboutModels;
import com.kenova.store.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenova.store.Fragment.FavouriteFragment;
import com.kenova.store.Fragment.ProfileFragment;
import com.kenova.store.Utils.BannerAds;
import com.kenova.store.Utils.BottomNavigationViewHelper;


public class MainActivity extends AppCompatActivity {
    long mBackPressed;


    public static SharedPreferences sharedPreferences;
    public static String user_id;
    public static String user_name;
    public static String image;
    public static String image1;
    public static String token;
    BaseApp baseApp;
    LinearLayout llsearch;
    DatabaseReference rootref;
    AboutModels modelAbout;
    public static String title="none";
    ImageView maps;

    EditText search;

    public  static MainActivity mainActivity;
    private FragmentManager fragmentManager;
    BottomNavigationView navigation;
    int previousSelect = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    HomeFragment homeFragment = new HomeFragment();
                    navigationItemSelected(0);
                    loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                    return true;
                case R.id.stores:
                    StoresFragment propertyFragment = new StoresFragment();
                    navigationItemSelected(1);
                    loadFrag(propertyFragment, getString(R.string.menu_stores), fragmentManager);
                    return true;
                case R.id.favourite:
                    FavouriteFragment matchFragment = new FavouriteFragment();
                    navigationItemSelected(2);
                    loadFrag(matchFragment, getString(R.string.menu_favourite), fragmentManager);
                    return true;
                case R.id.chat:
                    MessageFragment messageFragment = new MessageFragment();
                    navigationItemSelected(3);
                    loadFrag(messageFragment, getString(R.string.menu_chat), fragmentManager);
                    return true;
                case R.id.user:
                    ProfileFragment profileFragment = new ProfileFragment();
                    navigationItemSelected(4);
                    loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        fragmentManager = getSupportFragmentManager();
        llsearch = findViewById(R.id.llsearch);
        baseApp = BaseApp.getInstance();
        navigation = findViewById(R.id.navigation);
        maps = findViewById(R.id.maps);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        modelAbout = new AboutModels();
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
        mainActivity =this;
        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.uid, "null");
        user_name = sharedPreferences.getString(Constants.f_name, "") + " " + sharedPreferences.getString(Constants.l_name, "");
        image =sharedPreferences.getString(Constants.u_pic,"null");
        image1 =sharedPreferences.getString("image1","null");
        token=sharedPreferences.getString(Constants.device_token, FirebaseInstanceId.getInstance().getToken());
        rootref= FirebaseDatabase.getInstance().getReference();

        search = findViewById(R.id.search);

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String sSearch= search.getText().toString().trim();
                if (TextUtils.isEmpty(sSearch)) {

                    Toast.makeText(MainActivity.this, "Column Can't be Empty", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("searchtext", sSearch);
                    startActivity(intent);
                    return true;
                }

                return false;
            }

        });

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Constants.versionname=packageInfo.versionName;

    }



    @Override
    protected void onStart() {
        super.onStart();
        if (baseApp.getIsLogin()) {
            rootref.child("Users").child(user_id).child("token").setValue(token);
        } else {
            rootref.child("Users").child(user_id).child("token").setValue("null");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Check_version();

    }

    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                clickDone();

            }
        } else {
            super.onBackPressed();
        }
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }



    public void Check_version(){
        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.execute();
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
    }

    public void navigationItemSelected(int position) {
        previousSelect = position;
    }

}
