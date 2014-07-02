package de.dmxcontrol.network.UDP;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityDevice;

/**
 * Created by Qasi on 12.06.2014.
 */
/*public class Sender implements Runnable {

    private final static String TAG = "Network - Sender";

    private boolean bKeepRunning;

    private ArrayList<byte[]> sendData = new ArrayList<byte[]>();

    private DatagramSocket mSenderSocket;

    private InetAddress mServerAddress;

    private int mServerPort;


    private void sendDataOut(DatagramSocket socket) {
        try {
            for(int i = 0; i < sendData.size(); i++) {

                socket.send(new DatagramPacket(sendData.get(i), sendData.get(i).length, mServerAddress, mServerPort));

                //TCPSender.runTcpClient(new String(sendData.get(i), 0, sendData.get(i).length));
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


    public Sender(String serverAddress, int serverPort) throws SocketException, UnknownHostException {
        super();

        mServerPort = serverPort;
        try {
            mServerAddress = InetAddress.getByName(serverAddress);
        }
        catch(UnknownHostException e) {
            throw e;
        }

        try {
            // Create and open socket
            if(mSenderSocket == null) {
                mSenderSocket = new DatagramSocket(mServerPort);

                // Maybe use this to avoid a blocked transmission
                //mSenderSocket.setSoTimeout();
            }
        }
        catch(SocketException e) {
            throw e;
        }

    }

    public void addSendData(byte[] data) {
        sendData.add(data);
    }

    public void run() {

        bKeepRunning = true;

        try {
            byte count=0;
            while(bKeepRunning) {
                if(count==0){
                    EntityDevice.SendRequest(EntityDevice.class, Entity.Request_All_GUIDs);
                }
                count++;
                sendDataOut(mSenderSocket);

                Thread.sleep(33);
            }
        }
        catch(InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
        finally {
            // close socket
            if(mSenderSocket != null) {
                // close socket if it is open
                if(!mSenderSocket.isClosed()) {
                    mSenderSocket.close();
                }
                // Reset socket
                mSenderSocket = null;
            }

            // Clear data
            sendData.clear();
        }
    }

    public void kill() {
        bKeepRunning = false;
    }
}
*/