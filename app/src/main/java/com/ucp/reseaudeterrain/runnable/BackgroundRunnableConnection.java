package com.ucp.reseaudeterrain.runnable;

import com.ucp.reseaudeterrain.services.ClientInterfaceTCP;
import com.ucp.reseaudeterrain.services.NetworkBackendService;

import java.io.IOException;

/**
 * It's mandatory to use thread to use socket
 * So this class will allow a connection to be established with the server
 */

public class BackgroundRunnableConnection implements Runnable {
    private ClientInterfaceTCP clientInterfaceTCP;
    private NetworkBackendService networkBackendService;
    public final static String SERVER_UNREACHABLE_TAG = "SERVER_UNREACHABLE";


    public BackgroundRunnableConnection(NetworkBackendService networkBackendService, ClientInterfaceTCP clientInterfaceTCP) {
        this.clientInterfaceTCP = clientInterfaceTCP;
        this.networkBackendService = networkBackendService;
    }

    /**
     * If the TCP client is not already connected, connect it
     * And send a ping to the server
     */
    @Override
    public void run() {

        try {
            if (!this.clientInterfaceTCP.getConnected())
                this.clientInterfaceTCP.setConnected(this.clientInterfaceTCP.connection());
            if (this.clientInterfaceTCP.getConnected())
                new Thread(new BackgroundRunnableSendString(this.clientInterfaceTCP, this.networkBackendService, "BPING")).start();
            else
                this.networkBackendService.sendMessageToReceiver(SERVER_UNREACHABLE_TAG);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}