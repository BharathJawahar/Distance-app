package com.example.socialdistancingbluetoothapp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(@Nullable Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE DEVICE_TABLE(DEVICE_NAME TEXT, LOCATION TEXT, STRENGTH INT, DANGER BOOL, TIME TEXT)");
        //delete();
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean addOne(DeviceModel deviceModel) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DEVICE_NAME", deviceModel.getDeviceName());
        contentValues.put("LOCATION", deviceModel.getLocation());
        contentValues.put("STRENGTH", deviceModel.getStrength());
        contentValues.put("DANGER", deviceModel.isDanger());
        contentValues.put("TIME", deviceModel.getTime());

        sqLiteDatabase.insert("DEVICE_TABLE", null, contentValues);


        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                String time = c.getString(4);
                DeviceModel dm = new DeviceModel(name,loc,st,dan,time);
                list.add(dm);
            }while (c.moveToNext());
        }
        return  list;
    }

    public String[][] get1(){
        List<DeviceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM DEVICE_TABLE";
        Cursor c = db.rawQuery(q, null);
        String[][] name = new String[100][5];
        int i=0;
        if(c.moveToFirst()) {
            do{
                name[i][0] = c.getString(0);
                name[i][1] = c.getString(1);
                name[i][2] = String.valueOf(c.getInt(2));
                name[i][3] = String.valueOf(c.getInt(3) == 1 ? true : false);
                name[i++][4] = c.getString(4);
            }while (c.moveToNext());
        }
        return name;
    }

    public void delete() {
        SQLiteDatabase db = this.getWritableDatabase();
            String q = "DELETE FROM DEVICE_TABLE";
       db.rawQuery(q, null);
    }
}
