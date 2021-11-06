package com.example.socialdistancingbluetoothapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    public ArrayList arrayOfFoundBTDevices;

    View circle;
    TextView onOff;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circle = (View) findViewById(R.id.circle);
        onOff = (TextView) findViewById(R.id.onOff);
        info = (TextView) findViewById(R.id.info);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if( !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 99);
        }

        if (mBluetoothAdapter.isEnabled()) {
            onOff.setText("On");
            info.setText("Press the circle to Turn Off");

        }
        else {
            onOff.setText("Off");
            info.setText("Press the circle to Turn On");
        }

    }

    public void circleClick(View view) {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(),"Bluetooth Disabled",Toast.LENGTH_SHORT).show();
            onOff.setText("Off");
            info.setText("Press the circle to Turn On");
            /*ColorDrawable[] colorDrawables = {new ColorDrawable(Color.DKGRAY)};
            TransitionDrawable transitionDrawable = new TransitionDrawable(colorDrawables);
            circle.setBackground(transitionDrawable);
            transitionDrawable.startTransition(2000);*/

        }
        else {
            mBluetoothAdapter.enable();
            Toast.makeText(getApplicationContext(),"Bluetooth Enabled",Toast.LENGTH_SHORT).show();
            onOff.setText("On");
            info.setText("Press the circle to Turn Off");
            /*ColorDrawable[] colorDrawables = {new ColorDrawable(Color.GREEN)};
            TransitionDrawable transitionDrawable = new TransitionDrawable(colorDrawables);
            circle.setBackground(transitionDrawable);
            transitionDrawable.startTransition(2000);*/
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBluetoothAdapter.cancelDiscovery();
    }

    private void displayListOfFoundDevices() {
        arrayOfFoundBTDevices = new ArrayList<BluetoothObject>();
        mBluetoothAdapter.startDiscovery();
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent)
          {
              String action = intent.getAction();
              // When discovery finds a device
              if (BluetoothDevice.ACTION_FOUND.equals(action))
              {
                  // Get the bluetoothDevice object from the Intent
                  BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                  // Get the "RSSI" to get the signal strength as integer,
                  // but should be displayed in "dBm" units
                  int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                  // Create the device object and add it to the arrayList of devices
                  BluetoothObject bluetoothObject = new BluetoothObject();
                  bluetoothObject.setName(device.getName());
                  bluetoothObject.setAddress(device.getAddress());
                  bluetoothObject.setState(device.getBondState());
                  bluetoothObject.setStrength(rssi);

                  arrayOfFoundBTDevices.add(bluetoothObject);

                  // 1. Pass context and data to the custom adapter

                  // 2. setListAdapter
              }
          }

        };

    }
}

class BluetoothObject {
    String name, address;
    int strength, state;

    public void setName(String name) {
        this.name = name;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getState() {
        return state;
    }
}