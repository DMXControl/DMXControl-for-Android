package de.dmxcontrol.network.UDP;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import de.dmxcontrol.android.R;

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

public class Reader extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage = "";
    private KernelPingDeserielizer lastKernelPing;


    public KernelPingDeserielizer GetLastKernelPing() {
        return lastKernelPing;
    }

    private ArrayList<KernelPingDeserielizer> KernelPings;

    public ArrayList<KernelPingDeserielizer> GetKernelPings() {
        return KernelPings;
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
        String message;
        byte[] lmessage = new byte[4096];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
        KernelPings = new ArrayList<KernelPingDeserielizer>();
        try {
            DatagramSocket kernelsocket = new DatagramSocket(12352);
            //R.id.preferences
            while (bKeepRunning) {
                kernelsocket.receive(packet);
                message = new String(lmessage, 0, packet.getLength());
                lastMessage = message;
                if (message.length() > 0) {
                    Log.d("UDP", "Message Rescived: " + message);
                    lastKernelPing = KernelPingDeserielizer.Get(message);

                    boolean add = true;
                    if (KernelPings.size() > 0) {
                        for (int i = 0; i < KernelPings.size(); i++) {
                            if (KernelPings.get(i).GetHostName().equals(lastKernelPing.GetHostName())) {
                                add = false;
                                KernelPings.remove(i);
                                KernelPings.add(i, lastKernelPing);
                            }
                        }
                    }
                    if (add) {
                        KernelPings.add(lastKernelPing);
                    }
                    for (NewsUpdateListener listener : listeners) {
                        listener.onNewsUpdate();
                    }
                }
                Thread.sleep(500);
                //runOnUiThrea (updateTextMessage);
            }
            if (kernelsocket != null) {
                kernelsocket.close();
            }
        } catch (Throwable e) {
            Log.e("UDP Listener", e.getMessage());
        }

    }

    public void kill() {
        bKeepRunning = false;
    }

    public String getLastMessage() {
        return lastMessage;
    }
//}

//private Runnable updateTextMessage = new Runnable() {
//    public void run() {
//        if (myDatagramReceiver == null) return;
//        textMessage.setText(myDatagramReceiver.getLastMessage());
//    }
};