package de.dmxcontrol.network.TCP;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

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
public class TCPReader implements Runnable {

    private final static String TAG = "Network - Reader";

    private boolean bKeepRunning;

    private Socket GetSocket() {
        return mSender.getSocket();
    }

    // must public because of memory leaks!!! ???
    private char[] buffer = new char[1024 * 128];
    private BufferedReader bufferedReader;
    private String message = "";

    private TCPSender mSender;

    public TCPReader(TCPSender sender) {
        super();
        this.mSender = sender;
    }

    private String readMessage(java.net.Socket socket) throws IOException {
        bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream())
                );
        int count = bufferedReader.read(buffer, 0, buffer.length);
        bufferedReader = null;
        message = new String(buffer, 0, count);
        return message.substring(0, message.lastIndexOf("}") + 1);
    }

    public void run() {
        bKeepRunning = true;
        String message = "";
        JSONObject o;
        String type;
        String[] split;

        // 0x007d is } 0x007b is { -> so we split at }{ pattern
        String splitter = new String(new byte[]{0x007d, 0x007b});

        boolean guidList;
        try {
            Socket s = null;

            while(bKeepRunning) {
                try {
                    while(s == null) {
                        s = GetSocket();

                        Thread.sleep(1000);
                    }
                    s.setSoTimeout(10000);
                    message = readMessage(s);
                    if(message.contains(splitter)) {

                        split = message.split(Pattern.quote(splitter));

                        for(int i = 0; i < split.length; i++) {

                            if(split[i].length() > 3) {

                                if(i == 0) {
                                    split[i] = split[i] + "}";
                                }
                                else if(i == split.length - 1) {
                                    split[i] = "{" + split[i];
                                }
                                else {
                                    split[i] = "{" + split[i] + "}";
                                }
                            }
                        }

                        split.hashCode();
                    }
                    else {
                        split = new String[]{message};
                    }

                    for(String received : split) {
                        if(received.length() > 3 && received.contains("Type")) {
                            try {
                                o = new JSONObject(received);

                                type = o.getString("Type");
                                guidList = type.contains("GUIDList");
                                if(type.equals("AvailableDevices")) {
                                    ReceivedData.get().AvailableDevices.FillByJSON(o);
                                }
                                else if(type.contains("DeviceGroup")) {
                                    if(guidList) {
                                        ReceivedData.get().Groups.setGUIDsList(o.getJSONArray("GUIDs"));
                                    }
                                    else {
                                        ReceivedData.get().Groups.add(EntityGroup.Receive(o));
                                    }
                                }
                                else if(type.contains("Device")) {
                                    if(guidList) {
                                        ReceivedData.get().Devices.setGUIDsList(o.getJSONArray("GUIDs"));
                                    }
                                    else {
                                        ReceivedData.get().Devices.add(EntityDevice.Receive(o));
                                    }
                                }
                                else if(type.contains("Preset")) {
                                    if(guidList) {
                                        ReceivedData.get().Presets.setGUIDsList(o.getJSONArray("GUIDs"));
                                    }
                                    else {
                                        ReceivedData.get().Presets.add(EntityPreset.Receive(o));
                                    }
                                }
                                else if(type.contains("ExecutorPage")) {
                                    if(guidList) {
                                        ReceivedData.get().ExecutorPages.setGUIDsList(o.getJSONArray("GUIDs"));
                                    }
                                    else {
                                        ReceivedData.get().ExecutorPages.add(EntityExecutorPage.Receive(o));
                                    }
                                }
                                else if(type.contains("Executor")) {
                                    if(guidList) {
                                        ReceivedData.get().Executors.setGUIDsList(o.getJSONArray("GUIDs"));
                                    }
                                    else {
                                        ReceivedData.get().Executors.add(EntityExecutor.Receive(o));
                                    }
                                }
                                else if(type.contains("Cuelist")) {
                                    if(guidList) {
                                        ReceivedData.get().Cuelists.setGUIDsList(o.getJSONArray("GUIDs"));
                                    }
                                    else {
                                        ReceivedData.get().Cuelists.add(EntityCuelist.Receive(o));
                                    }
                                }
                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    message = null;
                    split = null;
                    type = null;
                    o = null;
                    if(message == null && split == null && type == null && o == null) {
                        //All is cleared
                    }
                }
                catch(SocketTimeoutException e) {
                    Log.i("TCPReader", "TimeOut");
                    System.gc();
                }
                catch(Exception e) {
                    System.gc();
                    //e.printStackTrace(); //For Debug
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            System.gc();
        }
    }

    public void kill() {
        bKeepRunning = false;
    }
}
