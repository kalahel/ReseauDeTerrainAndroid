package com.ucp.reseaudeterrain.network.services;

import android.util.Log;

import com.ucp.reseaudeterrain.network.runnable.ClientListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

// TODO CHANGE InputStreamReader to DataInputStream
public class ClientInterfaceTCP {
    private DataOutputStream outputStream;
    private Socket socket;

    private ClientListener clientListener;
    private DataInputStream inputStream;
    private NetworkBackendService networkBackendService;
    private int portNumber;
    private String address;
    private Boolean isConnected;

    public ClientInterfaceTCP(NetworkBackendService networkBackendService) {
        this.socket = null;
        this.outputStream = null;
        this.inputStream = null;
        this.portNumber = 5000;
        this.address = "dankest.space";
        this.isConnected = false;
        this.networkBackendService = networkBackendService;
    }

    /**
     * Send a string to the server
     *
     * @param messageToSend Message to send to the server
     */
    public void sendString(String messageToSend) {
        try {
            this.outputStream.writeUTF(messageToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Terminate the connection
     * Stop the listening.
     *
     * @return True if the disconnection happened correctly.
     * @throws IOException exception to handle
     */
    public Boolean disconnect() throws IOException {

        this.clientListener.setReadyToRead(false);
        this.outputStream.close();
        this.inputStream.close();
        this.socket.close();
        return true;
    }

    /**
     * Connect to the server
     * Start a thread to listen to the server responses.
     *
     * @return True if the connection happened correctly.
     * @throws IOException exception to handle
     */
    public Boolean connection() throws IOException {


        try {
            Log.d("ClientInterfaceTCP", "Trying to create Socket");
            socket = new Socket(this.address, this.portNumber);
            Log.d("ClientInterfaceTCP", "Socket creation succesfull");
            Log.d("ClientInterfaceTCP", "Trying to create PrintWriter");
            outputStream = new DataOutputStream(socket.getOutputStream());
            Log.d("ClientInterfaceTCP", "PrintWriter creation succesfull");

//            inputStream = new InputStreamReader(
//                    socket.getInputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            // We start to listen
            clientListener = new ClientListener(this.inputStream, this.networkBackendService);
            Thread thread = new Thread(clientListener);
            clientListener.setReadyToRead(true);
            thread.start();

        } catch (UnknownHostException e) {
            System.err.println("Hote inconnu");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }
}