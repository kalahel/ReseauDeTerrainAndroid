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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ucp.reseaudeterrain.network.NetworkReceiver;
import com.ucp.reseaudeterrain.network.runnable.BackgroundRunnableConnection;
import com.ucp.reseaudeterrain.network.runnable.ClientListener;
import com.ucp.reseaudeterrain.network.services.Displayable;
import com.ucp.reseaudeterrain.network.services.NetworkBackendService;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements Displayable {
    public final static String FILTER_MAIN_ACTIVITY = "com.ucp.reseaudeterrain.MainActivity.FILTER_MAIN_ACTIVITY";
    public final static String PROTOCOL_INFO = "INFO";
    public final static String PROTOCOL_DISCONNECT = "DISC";
    public final static String PROTOCOL_GET = "GET";
    public final static String PROTOCOL_MOTOR = "m0";


    private TextView buttonStateView;
    private TextView sensorStateView;
    private TextView motorStateView;
    private TextView connexionStateView;
    private Button retryConnectionButton;
    private LocalBroadcastManager localBroadcastManager;
    private NetworkBackendService networkBackendService;
    private boolean mBound = false;
    private boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.isConnected = false;

        buttonStateView = findViewById(R.id.buttonStateView);
        sensorStateView = findViewById(R.id.sensorStateView);
        motorStateView = findViewById(R.id.motorStateView);
        connexionStateView = findViewById(R.id.connexionStateView);
        retryConnectionButton = findViewById(R.id.retryConnectionButton);
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
        switch (textReceived) {
            case BackgroundRunnableConnection
                    .SERVER_REACHED_TAG:
                this.connexionStateView.setText("Connected");
                this.retryConnectionButton.setVisibility(View.INVISIBLE);
                this.isConnected = true;
                break;
            case BackgroundRunnableConnection
                    .SERVER_UNREACHABLE_TAG:
            case ClientListener
                    .CONNEXION_LOST_TAG:
                this.connexionStateView.setText("Disconnected");
                this.motorStateView.setText("Disconnected");
                this.retryConnectionButton.setVisibility(View.VISIBLE);
                this.isConnected = false;
                break;
            default:
                String[] receivedStrings = textReceived.split(",");
                if (receivedStrings.length == 7) {
                    // Type
                    switch (receivedStrings[3]) {
                        case PROTOCOL_DISCONNECT :
                            switch (receivedStrings[5]) {
                                case PROTOCOL_MOTOR:
                                    motorStateView.setText("Disconnect");
                                    break;
                            }
                            break;
                        default:
                            // Sensor ID
                            switch (receivedStrings[5]) {
                                case PROTOCOL_MOTOR:
                                    motorStateView.setText(receivedStrings[6]);
                                    break;
                            }
                            break;
                    }
                } else {
                    toast = Toast.makeText(getApplicationContext(), "Received frame with incorrect format", Toast.LENGTH_SHORT);
                    toast.show();
                }
        }
    }

    /**
     * Method use to call the network background service to re-establish a connection with the server
     */
    public void reEstablishConnection() {
        Toast.makeText(this, "Trying to re-establish connection", Toast.LENGTH_SHORT).show();
        networkBackendService.reEstablishConnection();
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
        networkBackendService.sendMessageToServer(NetworkBackendService.LOCAL_DEST_TAG + "," +
                NetworkBackendService.SOURCE_TAG + "," +
                (new Date()).getTime() / 1000 + "," +
                "SET," +
                2 + "," +
                "m0," +
                "20:0");
    }

    public void leftClick(View view) {
        networkBackendService.sendMessageToServer(NetworkBackendService.LOCAL_DEST_TAG + "," +
                NetworkBackendService.SOURCE_TAG + "," +
                (new Date()).getTime() / 1000 + "," +
                "SET," +
                2 + "," +
                "m0," +
                "-20:0");
    }

    public void retryConnection(View view) {
        if (!isConnected) {
            this.reEstablishConnection();
        }
    }

    public void upClick(View view) {
        networkBackendService.sendMessageToServer(NetworkBackendService.LOCAL_DEST_TAG + "," +
                NetworkBackendService.SOURCE_TAG + "," +
                (new Date()).getTime() / 1000 + "," +
                "SET," +
                2 + "," +
                "m0," +
                "0:-20");
    }

    public void downClick(View view) {
        networkBackendService.sendMessageToServer(NetworkBackendService.LOCAL_DEST_TAG + "," +
                NetworkBackendService.SOURCE_TAG + "," +
                (new Date()).getTime() / 1000 + "," +
                "SET," +
                2 + "," +
                "m0," +
                "0:20");
    }
}
