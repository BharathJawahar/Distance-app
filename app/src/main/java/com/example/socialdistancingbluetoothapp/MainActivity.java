package com.example.socialdistancingbluetoothapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int totalDevice = 0;
    private BluetoothAdapter mBluetoothAdapter;
    final private BroadcastReceiver mBluetoothStatusChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle extras = intent.getExtras();
            final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch(bluetoothState) {
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context,"Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                    onOff.setText("On");
                    info.setText("Press the circle to Turn Off");
                    circle.setBackgroundResource(R.drawable.circle_green);
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context,"Bluetooth Disabled", Toast.LENGTH_SHORT).show();
                    onOff.setText("Off");
                    info.setText("Press the circle to Turn On");
                    circle.setBackgroundResource(R.drawable.circle_grey);
                    break;
            }
        }
    };
    final private BroadcastReceiver mBluetoothDiscoveryStatusChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(context, "Discovery Started", Toast.LENGTH_LONG).show();
                circle.startAnimation();
                totalDevice = 0;
                noOfBtDev.setText(String.valueOf(totalDevice));
                startDiscovery.setText("Stop Discovery");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "Discovery Finished", Toast.LENGTH_LONG).show();
                circle.stopAnimation();
                startDiscovery.setText("Start Discovery");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                totalDevice += 1;
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                Toast.makeText(context, "Bluetooth Device Found. Total Devices : " + String.valueOf(totalDevice) + "\nStrength : " + String.valueOf(rssi), Toast.LENGTH_LONG).show();
                noOfBtDev.setText(String.valueOf(totalDevice));
                if((-1 * rssi) < 55) {
                    Toast.makeText(context, "Please maintain social distance", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(context)
                            .setTitle("Please maintain your Social Distance")
                            .setMessage("You have a very close contact with " + device.getName())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever...
                        }
                    }).show();
                }          }
            if(mBluetoothAdapter.isDiscovering()) {
                circle.startAnimation();
            }
            else {
                circle.stopAnimation();
            }
        }
    };

    TextView onOff, info, noOfBtDev;
    Radar circle;
    Button startDiscovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(mBluetoothStatusChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mBluetoothDiscoveryStatusChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(mBluetoothDiscoveryStatusChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(mBluetoothDiscoveryStatusChangedReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        circle = (Radar) findViewById(R.id.circle);
        onOff = (TextView) findViewById(R.id.onOff);
        info = (TextView) findViewById(R.id.info);
        noOfBtDev = (TextView) findViewById(R.id.noOfBluetooth);
        startDiscovery = (Button) findViewById(R.id.button);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "BlueTooth not supported in this device", Toast.LENGTH_LONG).show();
            circle.setBackgroundResource(R.drawable.circle_grey);
        }

        if (mBluetoothAdapter.isEnabled()) {
            onOff.setText("On");
            android.util.Log.d("Debug on MainActivity", "onCreate: Bluetooth already Enabled");
            info.setText("Press the circle to Turn Off");
            circle.setBackgroundResource(R.drawable.circle_green);

        } else {
            onOff.setText("Off");
            info.setText("Press the circle to Turn On");
            circle.setBackgroundResource(R.drawable.circle_grey);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBluetoothStatusChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mBluetoothDiscoveryStatusChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(mBluetoothDiscoveryStatusChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(mBluetoothDiscoveryStatusChangedReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBluetoothStatusChangedReceiver);
        unregisterReceiver(mBluetoothDiscoveryStatusChangedReceiver);
        if(mBluetoothAdapter.isDiscovering())   mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBluetoothStatusChangedReceiver);
        unregisterReceiver(mBluetoothDiscoveryStatusChangedReceiver);
        super.onDestroy();
    }

    public void circleClick(View view) throws InterruptedException {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            circle.stopAnimation();

        } else {
            mBluetoothAdapter.enable();
        }
    }

    public void startDiscover(View view) {
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            circle.stopAnimation();
        }
        else mBluetoothAdapter.startDiscovery();
    }

}
