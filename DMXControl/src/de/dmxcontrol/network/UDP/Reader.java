package de.dmxcontrol.network.UDP;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.cuelist.EntityCuelist;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.executor.EntityExecutor;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.preset.EntityPreset;

/**
 * Created by Qasi on 12.06.2014.
 */
/*public class Reader implements Runnable {

    private final static String TAG = "Network - Reader";

    private boolean bKeepRunning;

    private DatagramSocket mReaderSocket;

    private int mReaderPort;

    private void receiveData(DatagramSocket socket) {
        try {

            DatagramPacket packet = new DatagramPacket( new byte[4095], 4095 );
            socket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength());

            if(message.length() > 0) {
                JSONObject o = new JSONObject(message);

                String type = o.getString("Type");

                if(type.equals("Device")) {
                    ReceivedData.get().Devices.add(EntityDevice.Receive(o));
                }
                if(type.equals("DeviceGUIDList")) {
                    ReceivedData.get().Devices.setGUIDsList(o.getJSONArray("GUIDs"));
                }
                else if(type.equals("DeviceGroup")) {
                    ReceivedData.get().Groups.add(EntityGroup.Receive(o));
                }
                else if(type.equals("Preset")) {
                    ReceivedData.get().Presets.add(EntityPreset.Receive(o));
                }
                else if(type.equals("Executor")) {
                    ReceivedData.get().Executors.add(EntityExecutor.Receive(o));
                }
                else if(type.equals("ExecutorPage")) {
                    ReceivedData.get().ExecutorPages.add(EntityExecutorPage.Receive(o));
                }
                else if(type.equals("Cuelist")) {
                    ReceivedData.get().Cuelists.add(EntityCuelist.Receive(o));
                }
            }

        }
        catch(Throwable e) {
            Log.e(TAG, "Can't receive Android app data packet " + e.getMessage());
            DMXControlApplication.SaveLog();
        }
    }

    public Reader(int localListenPort) throws SocketException {
        super();

        mReaderPort = localListenPort;

        try {
            // Create and open socket
            if(mReaderSocket == null) {
                mReaderSocket = new DatagramSocket(mReaderPort);

                // Maybe use this to avoid a blocked transmission
                //mReaderSocket.setSoTimeout();
            }
        }
        catch(SocketException e) {
            throw e;
        }
    }

    public void run() {

        bKeepRunning = true;

        try {
            while(bKeepRunning) {
                receiveData(mReaderSocket);

                Thread.sleep(2);
            }
        }
        catch(InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
        finally {
            // close socket
            if(mReaderSocket != null) {
                // close socket if it is open
                if(!mReaderSocket.isClosed()) {
                    mReaderSocket.close();
                }
                // Reset socket
                mReaderSocket = null;
            }
        }
    }

    public void kill() {
        bKeepRunning = false;
    }
}*/
