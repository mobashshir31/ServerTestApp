package com.sunny.servertestapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private TextView ipInfo;
    private TextView portInfo;
    private TextView messageText;
    private ServerSocket serverSocket;
    static final int serverSocketPort = 8085;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        portInfo = (TextView) findViewById(R.id.portInfo);
        String str = "Server Port: " + serverSocketPort;
        portInfo.setText(str);
        ipInfo = (TextView) findViewById(R.id.ipInfo);
        messageText = (TextView) findViewById(R.id.messageText);
        ipInfo.setText(getLocalIPAddress());

        Thread serverThread = new Thread(new MyServerSocketRunnable());
        serverThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyServerSocketRunnable implements Runnable{

        private int count = 0;
        @Override
        public void run() {
            Socket clientSocket;
            try {
                serverSocket = new ServerSocket(serverSocketPort);
                while(count<15){
                    clientSocket = serverSocket.accept();
                    count++;
                    //Communicate with the newly connected client socket
                    ServerCommunicateTask serverCommunicateTask = new ServerCommunicateTask(count);
                    serverCommunicateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, clientSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ServerCommunicateTask extends AsyncTask<Socket,Void,String>{
        private int clientNumber;
        ServerCommunicateTask(int clientNumber) {
            this.clientNumber = clientNumber;
        }

        @Override
        protected String doInBackground(Socket... params) {
            Socket socket = params[0];
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            String dispMessage="";
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dispMessage = "Client #" + clientNumber + " is: " + socket.getInetAddress() + ":" + socket.getPort();
                String messageFromClient = dataInputStream.readUTF();
                dispMessage += "\nMessage from Client: " + messageFromClient + "\n\n";
                String replyMessage = "You are Client #" + clientNumber;
                dataOutputStream.writeUTF(replyMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(dataInputStream!=null){
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(dataOutputStream!=null){
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(socket!=null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return dispMessage;
        }

        @Override
        protected void onPostExecute(String s) {
            messageText.append(s);
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
