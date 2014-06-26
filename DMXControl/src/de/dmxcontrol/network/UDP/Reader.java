package de.dmxcontrol.network.UDP;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

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

public class Reader extends Thread {
    private boolean bKeepRunning = true;

    private DatagramSocket androidApp;

    public enum Type {
        DEVICE,
        DEVICECOUNT,
        GROUP,
        GROUPCOUNT,
        PRESET,
        PRESETCOUNT,
        EXECUTOR,
        EXECUTORCOUNT,
        EXECUTORPAGE,
        EXECUTORPAGECOUNT;

        public static Type convert(byte value) {
            return Type.values()[value];
        }
    }

    public interface NewsUpdateListener {
        void onNewsUpdate();
    }

    private ArrayList<NewsUpdateListener> listeners = new ArrayList<NewsUpdateListener>();

    public void setOnNewsUpdateListener(NewsUpdateListener listener) {
        // Store the listener object
        this.listeners.add(listener);
    }

    public void run() {
        this.setPriority(MIN_PRIORITY);
        String message = "";
        byte[] lmessage = new byte[0x000fff];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

        try {
            if (androidApp == null) {
                androidApp = new DatagramSocket(13141);
            }

            while (bKeepRunning) {
                try {
                    receiveAndroidAppPluginEntity(message, lmessage, androidApp, packet);
                }
                catch (Exception e){
                    Log.w("",DMXControlApplication.stackTraceToString(e));
                    DMXControlApplication.SaveLog();
                }
                finally {
                    Thread.sleep(2);
                }
            }

            if (androidApp != null) {
                if(!androidApp.isClosed()){
                    androidApp.close();
                }
            }

        }
        catch (Throwable e) {
            Log.e("UDP Listener", e.getMessage());
            DMXControlApplication.SaveLog();
            run();
        }
    }

    private void receiveAndroidAppPluginEntity(String message, byte[] lmessage, DatagramSocket socket, DatagramPacket packet) {
        try {
            socket.receive(packet);
            message = new String(lmessage, 0, packet.getLength());

            if (message.length() > 0) {
                //Type t=Type.convert(lmessage[0]);

                JSONObject o = new JSONObject(message);
                String type=o.getString("Type");
                if (type.equals("Device")) {
                    ReceivedData.get().Devices.add(EntityDevice.Receive(o));
                }
                else if (type.equals("DeviceGroup")) {
                    ReceivedData.get().Groups.add(EntityGroup.Receive(o));
                }
                else if (type.equals("Preset")) {
                    ReceivedData.get().Presets.add(EntityPreset.Receive(o));
                }
                else if (type.equals("Executor")) {
                    ReceivedData.get().Executors.add(EntityExecutor.Receive(o));
                }
                else if (type.equals("ExecutorPage")) {
                    ReceivedData.get().ExecutorPages.add(EntityExecutorPage.Receive(o));
                }
                else if (type.equals("Cuelist")) {
                    ReceivedData.get().Cuelists.add(EntityCuelist.Receive(o));
                }

                for (NewsUpdateListener listener : listeners) {
                    listener.onNewsUpdate();
                }
            }

            message=null;
            if(message!=null) {
                message = null;
            }

            lmessage=null;
            if(lmessage!=null) {
                lmessage = null;
            }

        }
        catch (Throwable e) {
            Log.e("Can't receive KernelPing", e.getMessage());
            run();
        }
    }

    public void kill() {
        bKeepRunning = false;
        if(!androidApp.isClosed())
            androidApp.close();
    }
//}

//private Runnable updateTextMessage = new Runnable() {
//    public void run() {
//        if (myDatagramReceiver == null) return;
//        textMessage.setText(myDatagramReceiver.getLastMessage());
//    }
};