package com.ucp.reseaudeterrain.network.runnable;


import android.util.Log;

import com.ucp.reseaudeterrain.network.services.NetworkBackendService;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.util.Arrays.copyOf;

/**
 * Runnable class that will only listen what is send by the server
 * and displays it on a TextArea.
 */
public class ClientListener implements Runnable {
    private InputStreamReader inputStream;
    private Boolean readyToRead;
    private NetworkBackendService networkBackendService;

    /**
     * @param inputStream           The reader on the socket
     * @param networkBackendService The service in charge of handling the data received
     */
    public ClientListener(InputStreamReader inputStream, NetworkBackendService networkBackendService) {
        this.inputStream = inputStream;
        this.networkBackendService = networkBackendService;
    }

    private char[] resetBuffer(char[] b, int length) {
        for (int i = 0; i < length; i++)
            b[i] = 0;
        return b;
    }

    @Override
    public void run() {
        char[] buf = new char[1024];

        while (this.readyToRead) {
            try {
                if (this.inputStream.ready()) {
                    int numberOfRealChar = this.inputStream.read(buf);
                    char[] shortenedBuffer;
                    shortenedBuffer = copyOf(buf, numberOfRealChar);

                    System.out.println("ClientListener : READ " + String.valueOf(shortenedBuffer));
                    this.networkBackendService.sendMessageToReceiver(String.valueOf(shortenedBuffer));
                    Log.d("ClientListener", String.valueOf(shortenedBuffer));
                    buf = resetBuffer(buf, 1024);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void setReadyToRead(Boolean readyToRead) {
        this.readyToRead = readyToRead;
    }
}