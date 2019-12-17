package com.ucp.reseaudeterrain.network.runnable;


import android.util.Log;

import com.ucp.reseaudeterrain.network.services.ClientInterfaceTCP;
import com.ucp.reseaudeterrain.network.services.NetworkBackendService;

import java.io.IOException;
import java.io.InputStreamReader;

import static java.util.Arrays.copyOf;

/**
 * Runnable class that will only listen what is send by the server
 * and displays it on a TextArea.
 */
public class ClientListener implements Runnable {
    public final static String CLASS_TAG = "ClientListener";
    public final static String CONNEXION_LOST_TAG = "CONNEXION_LOST";

    private InputStreamReader inputStream;
    private Boolean isReadyToRead;
    private NetworkBackendService networkBackendService;
    private ClientInterfaceTCP clientInterfaceTCP;

    /**
     * @param inputStream           The reader on the socket
     * @param networkBackendService The service in charge of handling the data received
     * @param clientInterfaceTCP    reference to clientInterfaceTCP
     */
    public ClientListener(InputStreamReader inputStream, NetworkBackendService networkBackendService, ClientInterfaceTCP clientInterfaceTCP) {
        this.inputStream = inputStream;
        this.networkBackendService = networkBackendService;
        this.clientInterfaceTCP = clientInterfaceTCP;
    }

    private char[] resetBuffer(char[] b, int length) {
        for (int i = 0; i < length; i++)
            b[i] = 0;
        return b;
    }

    @Override
    public void run() {
        char[] buf = new char[1024];

        while (this.isReadyToRead) {
            try {
//                if (this.inputStream.ready()) {
                int numberOfRealChar = this.inputStream.read(buf);
                String stringToPrint = "Received number of char : " + numberOfRealChar;
                Log.d(CLASS_TAG, stringToPrint);
                char[] shortenedBuffer;
                // Connection has been lost
                if (numberOfRealChar == -1) {
                    Log.d(CLASS_TAG,"Connexion lost");
                    this.isReadyToRead = false;
                    this.networkBackendService.sendMessageToReceiver(CONNEXION_LOST_TAG);
                    this.clientInterfaceTCP.setConnected(false);
                } else {
                    shortenedBuffer = copyOf(buf, numberOfRealChar);

                    System.out.println("ClientListener : READ " + String.valueOf(shortenedBuffer));
                    this.networkBackendService.sendMessageToReceiver(String.valueOf(shortenedBuffer));
                    Log.d("ClientListener", String.valueOf(shortenedBuffer));
                    buf = resetBuffer(buf, 1024);
                }
//                }
//                else{
/////                   this.inputStream.read();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void setIsReadyToRead(Boolean isReadyToRead) {
        this.isReadyToRead = isReadyToRead;
    }
}