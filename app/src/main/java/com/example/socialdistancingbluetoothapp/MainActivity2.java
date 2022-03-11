package com.example.socialdistancingbluetoothapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ListView list;
    DataBaseHelper db;
    TextView output1;
    ScrollView sv;
    //ArrayAdapter arrayAdapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //list = findViewById(R.id.list);
        db = new DataBaseHelper(this);
        output1 = findViewById(R.id.output1);
        StringBuilder b = new StringBuilder();
        String[][] a = db.get1();
        for (int i=0;i<a.length;i++){
            if(a[i][0]==null)
                continue;
            b.append("Name : "+a[i][0]);
            b.append("\nLocation : "+a[i][1]);
            b.append("\nStrength : "+a[i][2]);
            b.append("\nDanger : "+a[i][3]);
            b.append("\nTime : "+a[i][4]);
            b.append("\n\n\n");
        }
        output1.setText(b);

        //List<DeviceModel> deviceModelList = db.get();
        //arrayAdapter = new ArrayAdapter<DeviceModel>(this, android.R.layout.simple_list_item_1, deviceModelList);
        //list.setAdapter(arrayAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void delete(View view) {
        //db.delete();
        //List<DeviceModel> deviceModelList = db.get();
        //arrayAdapter = new ArrayAdapter<DeviceModel>(this, android.R.layout.simple_list_item_1, deviceModelList);
        //list.setAdapter(arrayAdapter);
    }

}