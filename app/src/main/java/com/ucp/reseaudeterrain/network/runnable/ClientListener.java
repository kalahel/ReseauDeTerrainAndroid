package com.ucp.reseaudeterrain.network.runnable;


import android.util.Log;

import com.ucp.reseaudeterrain.network.services.NetworkBackendService;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.util.Arrays.copyOf;

/**
 * Runnable class that will only listen what is send by the server
 * and displays it on a TextArea.
 * TODO CHANGE InputStreamReader to DataInputStream
 */
public class ClientListener implements Runnable {
    private DataInputStream flux_entree;
    private Boolean readyToRead;
    private NetworkBackendService networkBackendService;

    /**
     * @param inputStream           The reader on the socket
     * @param networkBackendService The service in charge of handling the data received
     */
    public ClientListener(DataInputStream inputStream, NetworkBackendService networkBackendService) {
        this.flux_entree = inputStream;
        this.networkBackendService = networkBackendService;
    }

    private char[] resetBuffer(char[] b, int length) {
        for (int i = 0; i < length; i++)
            b[i] = 0;
        return b;
    }

    @Override
    public void run() {
        try {
            String dataReceived = "";
            dataReceived = this.flux_entree.readUTF();
            this.networkBackendService.sendMessageToReceiver(dataReceived);
            Log.d("ClientListener", dataReceived);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setReadyToRead(Boolean readyToRead) {
        this.readyToRead = readyToRead;
    }
}