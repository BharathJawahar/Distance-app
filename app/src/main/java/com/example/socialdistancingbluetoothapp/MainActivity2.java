package com.example.socialdistancingbluetoothapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ListView list;
    DataBaseHelper db;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        list = findViewById(R.id.list);
        db = new DataBaseHelper(this);
        List<DeviceModel> deviceModelList = db.get();
        arrayAdapter = new ArrayAdapter<DeviceModel>(this, android.R.layout.simple_list_item_1, deviceModelList);
        list.setAdapter(arrayAdapter);
    }

    public void delete(View view) {
        db.delete();
        List<DeviceModel> deviceModelList = db.get();
        arrayAdapter = new ArrayAdapter<DeviceModel>(this, android.R.layout.simple_list_item_1, deviceModelList);
        list.setAdapter(arrayAdapter);
    }

}