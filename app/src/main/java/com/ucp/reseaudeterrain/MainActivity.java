package com.ucp.reseaudeterrain;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ucp.reseaudeterrain.services.Displayable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements Displayable {
    private TextView buttonStateView;
    private TextView sensorStateView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStateView = findViewById(R.id.buttonStateView);
        sensorStateView = findViewById(R.id.sensorStateView);

    }

    @Override
    public void handleTextReception(String textReceived) {
        Toast toast = Toast.makeText(getApplicationContext(), textReceived, Toast.LENGTH_SHORT);
        toast.show();
    }
}
