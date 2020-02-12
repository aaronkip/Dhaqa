package com.kenova.store.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.kenova.store.Models.StoresModels;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "store.db";
    public static final String TABLE_FAVOURITE_NAME = "favourite";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATE = "rate";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ADDRESS = "address";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_NAME + "("
                + KEY_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_RATE + " TEXT,"
                + KEY_DISTANCE + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_ADDRESS + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_NAME);
        // Create tables again
        onCreate(db);
    }

    public boolean getFavouriteById(String story_id) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{story_id};
        Cursor cursor = db.rawQuery("SELECT id FROM favourite WHERE id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeFavouriteById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  favourite " + " WHERE " + KEY_ID + " = " + _id);
        db.close();
    }

    public long addFavourite(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }

    public ArrayList<StoresModels> getFavourite() {
        ArrayList<StoresModels> chapterList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                StoresModels contact = new StoresModels();
                contact.setStoreid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                contact.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                contact.setRateAvg(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RATE)));
                contact.setDistance(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DISTANCE)));
                contact.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)));
                contact.setCname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));

                chapterList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapterList;
    }
}
