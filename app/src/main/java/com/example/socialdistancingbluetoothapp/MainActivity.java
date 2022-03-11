package com.example.socialdistancingbluetoothapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private static final int SCAN_INTERVAL_MS = 250;
    private static final int REQUEST_ENABLE_BT = 1;
    DataBaseHelper dataBaseHelper;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private ScanSettings mScanSettings;
    private boolean mScanning = false;
    private Handler mScanHandler = new Handler();


    int totalDevice = 0;
    final private BroadcastReceiver mBluetoothStatusChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle extras = intent.getExtras();
            final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (bluetoothState) {
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                    onOff.setText("On");
                    info.setText("Press the circle to Turn Off");
                    circle.setBackgroundResource(R.drawable.circle_green);
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
                    onOff.setText("Off");
                    info.setText("Press the circle to Turn On");
                    circle.setBackgroundResource(R.drawable.circle_grey);
                    break;
            }
        }
    };
    final private BroadcastReceiver mBluetoothDiscoveryStatusChangedReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
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
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                noOfBtDev.setText(String.valueOf(totalDevice));
                boolean flag = false;
                if ((-1 * rssi) < 55) {
                    flag = true;
                    NotificationChannel notificationChannel = new NotificationChannel("channel1", "Channel 1", NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.setDescription("This is Channel ");
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(notificationChannel);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    Notification notification = new NotificationCompat.Builder(context, "channel1").setSmallIcon(R.drawable.ic_one).setContentTitle("Please maintain Social Distance").setContentText("You have a very close contact with" + device.getName()).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_ALARM).build();
                    notificationManagerCompat.notify(1, notification);
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.raw);
                    mediaPlayer.start();
                    Toast.makeText(context, "Please maintain social distance", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(context)
                            .setTitle("Please maintain your Social Distance")
                            .setMessage("You have a very close contact with " + device.getName())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mediaPlayer.stop();
                                }
                            }).show();
                }
                DeviceModel deviceModel;

                LocalDateTime myDateObj = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedDate = myDateObj.format(myFormatObj);

                deviceModel = new DeviceModel(device.getName(), "Chennai", rssi, flag, formattedDate);

                dataBaseHelper.addOne(deviceModel);
                Toast.makeText(context, "Bluetooth Device Found. Total Devices : " + String.valueOf(totalDevice) + "\nStrength : " + String.valueOf(rssi) + "\nAdded to Database", Toast.LENGTH_LONG).show();


            }
            if (mBluetoothAdapter.isDiscovering()) {
                circle.startAnimation();
            } else {
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
        dataBaseHelper = new DataBaseHelper(MainActivity.this);

        checkPermissions(MainActivity.this, this);
        getBluetoothHandles();
        enableBluetooth();

        if (mBluetoothAdapter.isEnabled()) {
            beginScanning();
        }



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

    public void beginScanning() {
        mScanSettings = getScanSettings();
        //mScanHandler.post(mScanRunnable);
    }

    private ScanSettings getScanSettings() {
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

        return scanSettingsBuilder.build();
    }


    private void getBluetoothHandles() {
        mBluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    private void enableBluetooth() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public static void checkPermissions(Activity activity, Context context){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_PRIVILEGED,
        };

        if(!hasPermissions(context, PERMISSIONS)){
            ActivityCompat.requestPermissions( activity, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

    public void history(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startDiscover(View view) throws InterruptedException {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            circle.stopAnimation();
        }
        else {
            mBluetoothAdapter.startDiscovery();
            Toast.makeText(this, "Hey", Toast.LENGTH_LONG).show();
        }
    }
}