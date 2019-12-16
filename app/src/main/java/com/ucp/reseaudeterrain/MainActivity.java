package com.ucp.reseaudeterrain;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ucp.reseaudeterrain.network.NetworkReceiver;
import com.ucp.reseaudeterrain.network.services.Displayable;
import com.ucp.reseaudeterrain.network.services.NetworkBackendService;

public class MainActivity extends AppCompatActivity implements Displayable {
    public final static String FILTER_MAIN_ACTIVITY = "com.ucp.reseaudeterrain.MainActivity.FILTER_MAIN_ACTIVITY";

    private TextView buttonStateView;
    private TextView sensorStateView;
    private LocalBroadcastManager localBroadcastManager;
    private NetworkBackendService networkBackendService;
    private boolean mBound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStateView = findViewById(R.id.buttonStateView);
        sensorStateView = findViewById(R.id.sensorStateView);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);   // Get an instance of a broadcast manager
        BroadcastReceiver myReceiver = new NetworkReceiver(this);     // Create a class and set in it the behavior when an information is received
        IntentFilter intentFilter = new IntentFilter(FILTER_MAIN_ACTIVITY);     // The intentFilter action should match the action of the intent send
        localBroadcastManager.registerReceiver(myReceiver, intentFilter);       // We register the receiver for the localBroadcastManager
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     * Source : https://developer.android.com/guide/components/bound-services.html#Binder
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            NetworkBackendService.LocalBinder binder = (NetworkBackendService.LocalBinder) service;
            networkBackendService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, NetworkBackendService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void handleTextReception(String textReceived) {
        Toast toast = Toast.makeText(getApplicationContext(), textReceived, Toast.LENGTH_SHORT);
        toast.show();
    }
    /**
     * Method use to call the network background service to establish a connection with the server
     */
    public void establishConnection() {
        Toast.makeText(this, "Trying to establish connection", Toast.LENGTH_SHORT).show();
        networkBackendService.establishConnection();
    }

    /***
     * The service is only unbound onDestroy, this allow the service to persist between activities
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        mBound = false;
    }

    public void rightClick(View view) {

    }
}
