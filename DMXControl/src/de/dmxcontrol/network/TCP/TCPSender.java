package de.dmxcontrol.network.TCP;

import android.util.Log;

import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityDevice;

/**
 * Created by Qasi on 01.07.2014.
 */

public class TCPSender implements Runnable {

    private final static String TAG = "Network - Sender";
    private boolean bKeepRunning;
    private ArrayList<byte[]> sendData = new ArrayList<byte[]>();
    private InetAddress mServerAddress;
    private Socket client;
    private int mServerPort;

    public Socket getSocket() {
        return this.client;
    }

    private void writeMessage(java.net.Socket socket, String message) throws IOException {
        OutputStreamWriter printWriter = new OutputStreamWriter(socket.getOutputStream());
        printWriter.write(message);
        printWriter.flush();
    }

    private void sendDataOut() {
        try {
            for(int i = 0; i < sendData.size(); i++) {
                try {
                    writeMessage(client, new String(sendData.get(i), 0, sendData.get(i).length));
                }
                catch(UnknownHostException e) {

                    e.printStackTrace();

                }
                catch(IOException e) {

                    e.printStackTrace();

                }
                sendData.set(i, null);
                sendData.remove(i);
            }
        }
        catch(Exception e) {
            // Just log this and do not notify user
            Log.w(TAG, DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }


    public TCPSender(String serverAddress, int serverPort) throws SocketException, UnknownHostException {
        super();

        mServerPort = serverPort;

        try {
            mServerAddress = InetAddress.getByName(serverAddress);
        }
        catch(UnknownHostException e) {
            throw e;
        }
    }

    public void addSendData(byte[] data) {
        sendData.add(data);
    }

    public void run() {
        bKeepRunning = true;

        try {
            byte count = 0;

            while(bKeepRunning) {

                if(client != null) {

                    if(client.isClosed()) {
                        client = null;
                    }

                    if(!client.isConnected() && !client.isClosed()) {
                        try {
                            client.close();
                            client = null;
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(client == null) {
                    try {
                        client = new Socket(mServerAddress, mServerPort);
                        client.setKeepAlive(true);
                        Thread.sleep(1000);
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }


                if(count == 0) {
                    EntityDevice.SendRequest(EntityDevice.class, Entity.Request_All_GUIDs);
                }

                if(count > 128) {
                    count = 0;
                }
                count++;

                sendDataOut();

                Thread.sleep(33);
            }
        }
        catch(InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
        finally {
            // Clear data
            sendData.clear();

            try {
                client.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill() {
        bKeepRunning = false;
    }
}