package com.ucp.reseaudeterrain.network.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ucp.reseaudeterrain.MainActivity;
import com.ucp.reseaudeterrain.network.runnable.BackgroundRunnableConnection;
import com.ucp.reseaudeterrain.network.runnable.BackgroundRunnableDisconnection;
import com.ucp.reseaudeterrain.network.runnable.BackgroundRunnableSendString;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class NetworkBackendService extends Service {
    public final static String MESSAGE_SEND_TAG = "com.example.mat.networktestclient.NetworkBackendService.MESSAGE_SEND_TO_RECEIVER";
    public final static String CLASS_TAG = "NetworkBackendService";
    public final static String NETWORK_INTENT_TAG = "com.example.mat.networktestclient.backend.services.NetworkBackendService.NETWORK_VALUE";
    public final static String NETWORK_MESSAGE_TAG = "com.example.mat.networktestclient.backend.services.NetworkBackendService.MESSAGE_VALUE";

    int mStartMode;       // indicates how to behave if the service is killed
    private final IBinder mBinder = new LocalBinder();      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    private ClientInterfaceTCP clientInterfaceTCP; // Backend class used for connection
    private LocalBroadcastManager localBroadcastManager;
    private boolean authentified;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * Source : https://developer.android.com/guide/components/bound-services.html#Binder
     */
    public class LocalBinder extends Binder {
        public NetworkBackendService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NetworkBackendService.this;
        }
    }


    /**
     * At the creation instantiate a TCP client
     * we do it here to no ensure that the client has the same lifetime has this service
     * the same goes for the local broadcast manager
     */
    @Override
    public void onCreate() {
        super.onCreate();
        this.authentified = false;
        this.clientInterfaceTCP = new ClientInterfaceTCP(this);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Log.d(CLASS_TAG, "Instantiation of this this service");
        this.establishConnection();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Will execute one of this class method, choosen by the value passed by the intent
     * This will allow every class to start/end connection and Send string to the server
     *
     * @param intent  the intent containing the action to make and possibly the message to send
     * @param flags   flags
     * @param startId startId
     * @return mStartMode
     */
    @Override
    @Deprecated
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getIntExtra(NETWORK_INTENT_TAG, -1)) {
            case 0:
                this.establishConnection();
                break;
            case 1:
                this.sendMessageToServer(intent.getStringExtra(NETWORK_MESSAGE_TAG));
                break;
            case 2:
                this.terminateConnection();
                break;
            case 3:
                this.stopSelf();    // This will stop the service once this valued is passed
            default:
                Log.e(CLASS_TAG, "ERROR SWITCH VALUE IS INVALID");
        }

        //Displayable displayable = (Displayable) intent.getSerializableExtra(MainActivity.MAINACTIVITYTAG);

        //localBroadcastManager.sendBroadcast(new Intent("com.example.mat.testfirstactivity.TRANSMISSION"));


        return mStartMode;
    }

    /**
     * Used to send a string to the receiver
     * will sort message received and send them to the corresponding activity
     *
     * @param message Message to send to the receiver
     */
    public void sendMessageToReceiver(String message) {
        Intent intent = new Intent(MainActivity.FILTER_MAIN_ACTIVITY);
        intent.putExtra(MESSAGE_SEND_TAG, message);
        this.localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * Will use a thread to start the connection with the server
     * and send a string to ping the server
     */
    public void establishConnection() {
        Log.d(CLASS_TAG, "Trying To connect");
        new Thread(new BackgroundRunnableConnection(this, this.clientInterfaceTCP)).start();
        //this.sendMessageToServer("BPING");
    }

    /**
     * Will use a thread to send a message to the server
     *
     * @param messageToSend String to send to the server
     */
    public void sendMessageToServer(String messageToSend) {
        String msg = "Sending " + messageToSend + " to server";
        Log.d(CLASS_TAG, msg);
        new Thread(new BackgroundRunnableSendString(this.clientInterfaceTCP, this, messageToSend)).start();
    }

    /**
     * Will use a thread to end the connection with the server
     */
    public void terminateConnection() {
        Log.d(CLASS_TAG, "Trying To disconnect");
        new Thread(new BackgroundRunnableDisconnection(this, this.clientInterfaceTCP)).start();
    }

    @Override
    public void onDestroy() {
        this.terminateConnection();
        super.onDestroy();
    }

    public boolean isAuthentified() {
        return authentified;
    }

    public void setAuthentified(boolean authentified) {
        this.authentified = authentified;
    }
}