package de.dmxcontrol.network.UDP;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.Entity;

/**
 * Created by Qasi on 12.06.2014.
 */
//private MyDatagramReceiver myDatagramReceiver = null;

//protected void onResume() {
//        myDatagramReceiver = new MyDatagramReceiver();
//        myDatagramReceiver.start();
//        }

//protected void onPause() {
//        myDatagramReceiver.kill();
//        }

public class Sender extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage = "";
    private KernelPingDeserielizer lastKernelPing;
    private Entity lastEntity;

    public enum Type {
        DEVICE,
        DEVICECOUNT,
        GROUP,
        GROUPCOUNT,
        PRESET,
        PRESETCOUNT,
        EXECUTOR,
        EXECUTORCOUNT;

        public static Type convert(byte value) {
            return Type.values()[value];
        }
    }

    private ArrayList<byte[]> sendData = new ArrayList<byte[]>();
    private DatagramSocket androidApp;
    public void run() {
        try {
            if(androidApp==null) {
                androidApp = new DatagramSocket(23242);
            }
            while (bKeepRunning) {
                send(androidApp);
                Thread.sleep(33);
            }
            if (androidApp != null) {
                if(!androidApp.isClosed())
                    androidApp.close();
            }
        } catch (Throwable e) {
            Log.e("UDP Sender", e.getMessage());
            run();
        }
    }

    private void send(DatagramSocket socket) {
        try {
            for (int i = 0; i < sendData.size(); i++) {
                socket.send(new DatagramPacket(sendData.get(i), sendData.get(i).length, InetAddress.getByName(Prefs.get().getServerAddress()), 23242));
                sendData.set(i,null);
                sendData.remove(i);
            }
        } catch (Exception e) {
            Log.w("",DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }

    public void addSendData(byte[] data) {
        sendData.add(data);
    }

    public void kill() {
        bKeepRunning = false;
        if(!androidApp.isClosed())
            androidApp.close();
    }
}