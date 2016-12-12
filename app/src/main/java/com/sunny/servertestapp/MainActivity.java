package com.sunny.servertestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private TextView ipInfo;
    private ServerSocket serverSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipInfo = (TextView) findViewById(R.id.ipInfo);
        ipInfo.setText(getLocalIPAddress());
    }

    private class myServerSocketRunnable implements Runnable{
        static final int serverSocketPort = 8085;
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(serverSocketPort);
                serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getLocalIPAddress(){
        String ip = "";
        try{
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while(inetAddressEnumeration.hasMoreElements()){
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    if(inetAddress.isSiteLocalAddress())
                        ip += "Local Ip Address : " + inetAddress.getHostAddress() + "\n";
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Error!!\n";
        }
        return ip;
    }
}
