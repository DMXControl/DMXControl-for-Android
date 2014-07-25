package de.dmxcontrol.network.TCP;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.cuelist.EntityCuelist;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.executor.EntityExecutor;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.preset.EntityPreset;
import de.dmxcontrol.programmer.EntityProgrammer;

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
        if(socket == null) {
            return;
        }
        OutputStreamWriter printWriter = new OutputStreamWriter(socket.getOutputStream());
        printWriter.write(message);
        printWriter.flush();
    }

    private void sendDataOut() {
        try {
            byte[] out;
            for(int i = 0; i < sendData.size(); i++) {
                try {
                    out = sendData.get(i);
                    if(out != null) {
                        if(out.length > 0) {
                            writeMessage(client, new String(out, 0, out.length));
                        }
                    }
                    sendData.remove(out);
                }
                catch(UnknownHostException e) {
                    Log.w("TCPSender", "UnknownHostException:" + e.getMessage());
                    e.printStackTrace();
                }
                catch(IOException e) {
                    Log.w("TCPSender", "IOException: " + e.getMessage());
                    e.printStackTrace();
                }
                catch(NullPointerException e) {
                    Log.w("TCPSender", "NullPointerException: " + e.getMessage());
                    e.printStackTrace();
                }
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
                        if(e.getMessage().contains("(Connection timed out)")) {
                            //No EXCEPTION!!!
                        }
                        else {
                            e.printStackTrace();
                        }
                    }
                }


                if(count == 0) {
                    EntityDevice.SendRequest(EntityDevice.class, Entity.Request_All_GUIDs);
                }
                else if(count == 8) {
                    EntityGroup.SendRequest(EntityGroup.class, Entity.Request_All_GUIDs);
                }
                else if(count == 16) {
                    EntityExecutor.SendRequest(EntityExecutor.class, Entity.Request_All_GUIDs);
                }
                else if(count == 24) {
                    EntityExecutorPage.SendRequest(EntityExecutorPage.class, Entity.Request_All_GUIDs);
                }
                else if(count == 32) {
                    EntityCuelist.SendRequest(EntityCuelist.class, Entity.Request_All_GUIDs);
                }
                else if(count == 40) {
                    EntityCuelist.SendRequest(EntityPreset.class, Entity.Request_All_GUIDs);
                }
                else if(count == 48) {
                    EntityCuelist.SendRequest(EntityProgrammer.class, Entity.Request_All_GUIDs);
                }

                count++;
                if(count > 48) {
                    count = 0;
                }

                sendDataOut();

                Thread.sleep(33);
            }
        }
        catch(InterruptedException e) {
            Log.w("TCPSender", "InterruptedException" + e.getMessage());
            Log.d(TAG, e.getMessage());
        }
        finally {
            // Clear data
            sendData.clear();
            if(client != null) {
                try {
                    client.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void kill() {
        bKeepRunning = false;
        try {
            if(client != null) {
                client.close();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}