package com.example.socialdistancingbluetoothapp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(@Nullable Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE DEVICE_TABLE(DEVICE_NAME TEXT, LOCATION TEXT, STRENGTH INT, DANGER BOOL)");
        //delete();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(DeviceModel deviceModel) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DEVICE_NAME", deviceModel.getDeviceName());
        contentValues.put("LOCATION", deviceModel.getLocation());
        contentValues.put("STRENGTH", deviceModel.getStrength());
        contentValues.put("DANGER", deviceModel.isDanger());

        sqLiteDatabase.insert("DEVICE_TABLE", null, contentValues);


        return true;
    }

    public List<DeviceModel> get(){
        List<DeviceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM DEVICE_TABLE";
        Cursor c = db.rawQuery(q, null);
        if(c.moveToFirst()) {
            do{
                String name = c.getString(0);
                String loc = c.getString(1);
                int st = c.getInt(2);
                boolean dan = c.getInt(3) == 1 ? true : false;
                DeviceModel dm = new DeviceModel(name,loc,st,dan);
                list.add(dm);
            }while (c.moveToNext());
        }
        return  list;
    }

    public void delete() {
        SQLiteDatabase db = this.getWritableDatabase();
            String q = "DELETE FROM DEVICE_TABLE";
       db.rawQuery(q, null);
    }


}
