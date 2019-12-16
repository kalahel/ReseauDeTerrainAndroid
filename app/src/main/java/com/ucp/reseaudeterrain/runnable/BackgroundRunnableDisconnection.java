package com.ucp.reseaudeterrain.runnable;

import com.ucp.reseaudeterrain.services.ClientInterfaceTCP;
import com.ucp.reseaudeterrain.services.NetworkBackendService;

import java.io.IOException;

/**
 * It's mandatory to use thread to use socket
 * So this class will end the connection with the server
 */

public class BackgroundRunnableDisconnection implements Runnable {
    private ClientInterfaceTCP clientInterfaceTCP;
    private NetworkBackendService networkBackendService;

    public BackgroundRunnableDisconnection(NetworkBackendService networkBackendService, ClientInterfaceTCP clientInterfaceTCP) {
        this.clientInterfaceTCP = clientInterfaceTCP;
        this.networkBackendService = networkBackendService;
    }

    /**
     * If the TCP client is  connected, disconnect it
     */
    @Override
    public void run() {

        try {
            if (this.clientInterfaceTCP.getConnected())
                this.clientInterfaceTCP.setConnected(this.clientInterfaceTCP.disconect());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}