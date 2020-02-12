package com.kenova.store.Constants;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kenova on 10/23/2018.
 */

public class Constants {

    public static String BASEURL = "http://salon.ficarfoods.com/";
    public static String CONNECTION = BASEURL +"/api.php?";

    public static String SIGNUP = CONNECTION + "signup";
    public static String SETTINGAPP = CONNECTION + "settingapp";
    public static String EDITPROFILE = CONNECTION + "editprofile";
    public static String USERDATA = CONNECTION + "userdata";
    public static String UPLOADIMAGES = CONNECTION + "uploadImages";
    public static String DELETEIMAGES = CONNECTION + "deleteImages";
    public static String FEATURED = CONNECTION + "homefeatured";
    public static String CATEGORY = CONNECTION + "homecategory";
    public static String OFFERS = CONNECTION + "homeoffers";
    public static String ALLOFFERS = CONNECTION + "alloffers";
    public static String EVENTS = CONNECTION + "homeevents";
    public static String ALLEVENTS = CONNECTION + "allevents";
    public static String ALLSTORE = CONNECTION + "allstore";
    public static String ALLCATEGORY = CONNECTION + "allcategory";
    public static String STOREDETAIL = CONNECTION + "storeid=";
    public static String OFFERSDETAIL = CONNECTION + "offerid=";
    public static String BYRATING = CONNECTION + "byrating";
    public static String INTERESTED = CONNECTION + "interested=";
    public static String CITY = CONNECTION + "city";
    public static String BYCAT = CONNECTION + "cid=";
    public static String BYCITY = CONNECTION + "cityid=";
    public static String SEARCH = CONNECTION + "searchtext=";
    public static String DISTANCE = CONNECTION + "distance";
    public static String EVENTSDETAIL = CONNECTION + "eventid=";
    public static String PARTICIPATE = CONNECTION + "participate=";
    public static String UPDATESTORES = CONNECTION + "updatestores";
    public static String MYSTORES = CONNECTION + "mystores=";
    public static String ADDSTORES = CONNECTION + "addstores";
    public static String ADDOFFERS = CONNECTION + "addoffers";
    public static String ADDEVENT = CONNECTION + "addevent";
    public static String EDITOFFERS = CONNECTION + "editoffers";
    public static String OFFERDATA = CONNECTION + "offerdata=";
    public static String DELETESTORE = CONNECTION + "deletestore=";
    public static String DELETEOFFERS = CONNECTION + "deleteoffers=";
    public static String EDITEVENT = CONNECTION + "editevent";
    public static String DELETEEVENT = CONNECTION + "deleteevent=";
    public static String EVENTDATA = CONNECTION + "eventdata=";
    public static String ALLREVIEW = CONNECTION + "allreview=";
    public static String RATING = CONNECTION + "addreview=";


    public static String pref_name = "pref_name";
    public static String f_name="f_name";
    public static String l_name="l_name";
    public static String uid="uid";
    public static String u_pic="u_pic";
    public static String Lat="Lat";
    public static String Lon="Lon";
    public static String device_token="device_token";
    public static Double LATITUDE;
    public static Double LONGITUDE;

    public static String versionname="1.0";

    public static int permission_camera_code=786;
    public static int permission_write_data=788;
    public static int permission_Read_data=789;
    public static int permission_Recording_audio=790;
    public static int Select_image_from_gallry_code=3;
    public static int successmsg;

    public static String gif_firstpart="https://media.giphy.com/media/";
    public static String gif_secondpart="/100w.gif";

    public static String gif_firstpart_chat="https://media.giphy.com/media/";
    public static String gif_secondpart_chat="/200w.gif";

    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);


        public static final String FCM_KEY = "AIzaSyCsCK6qCcR11IEt1hDjKnHSEs80ikk9PQg";
        public static final LatLngBounds BOUNDS = new LatLngBounds(
                new LatLng(-7.216001, 0), // southwest
                new LatLng(0, 107.903316)); // northeast


}
