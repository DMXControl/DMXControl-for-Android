package de.dmxcontrol.network.TCP;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.regex.Pattern;

import de.dmxcontrol.cuelist.EntityCuelist;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.executor.EntityExecutor;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.network.IMessageListener;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.preset.EntityPreset;
import de.dmxcontrol.programmer.EntityProgrammer;

/**
 * Created by Qasi on 12.06.2014.
 */
public class TCPReader implements Runnable {

    private final static String TAG = "Network-Reader";

    // 0x007d is "}" and 0x007b is "{" -> so we split at "}{" pattern
    private final String splitter = new String(new byte[]{0x007d, 0x007b});

    private boolean bKeepRunning;

    // must be class vars because of memory leaks!
    private char[] buffer = new char[1024 * 128];
    private BufferedReader bufferedReader;
    private String message = "";

    private IMessageListener mTCPListener;

    public void setTCPListener(IMessageListener listener) {
        mTCPListener = listener;
    }

    private TCPSender mSender;

    private Socket GetSocket() {
        return mSender.getSocket();
    }

    public TCPReader(TCPSender sender) {
        super();
        this.mSender = sender;
    }

    private String readMessage(java.net.Socket socket) throws IOException {

        // Create buffered reader for input stream
        bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream())
                );

        // read data from input stream into buffer
        int count = bufferedReader.read(buffer, 0, buffer.length);

        // Delete buffered reader
        bufferedReader = null;


        // TODO Do some checking for count < 0 and message = null

        // Create string
        message = new String(buffer, 0, count);

        // return string up to last accurance of "}"
        return message.substring(0, message.lastIndexOf("}") + 1);
    }


    public void run() {

        bKeepRunning = true;
        String message = "";
        JSONObject o;
        String type;
        String[] split;

        boolean guidList;

        try {

            Socket s = null;

            while(bKeepRunning) {

                try {

                    while(s == null) {

                        s = GetSocket();

                        try {
                            Thread.sleep(1000); // throws interruptedException
                        }
                        catch(InterruptedException e) {
                            // We didn't slept the full time but this shouldn't be that bad here
                            Log.d(TAG + "-RunLoop", e.getMessage());
                        }

                        // Set timeout to 10 secs
                        s.setSoTimeout(10000); // throws SocketException
                    }

                    message = readMessage(s); // throws IOException

                    if(message.contains(splitter)) {

                        split = message.split(Pattern.quote(splitter)); // throws PatternSyntaxException

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

                        // ################## TODO Why do we do this here?? ###########################
                        split.hashCode(); //Arrays.hashCode(split)
                    }
                    else {
                        split = new String[]{message};
                    }

                    for(String received : split) {

                        if(received.length() > 3 && received.contains("Type")) {

                            try {

                                o = new JSONObject(received);

                                // Get type
                                type = o.getString("Type");

                                // check if type ist GUID list
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
                                else if(type.contains("Programmer")) {

                                    if(guidList) {
                                        ReceivedData.get().Programmers.setGUIDsList(o.getJSONArray("GUIDs"));
                                    }
                                    else {

                                        if(type.contains("State")) {
                                            ReceivedData.get().Programmers.get(o.getString("GUID")).LoadStates(o);
                                        }
                                        else {
                                            ReceivedData.get().Programmers.add(EntityProgrammer.Receive(o));
                                        }
                                    }
                                }
                            }
                            catch(JSONException e) {
                                // Something went wrong while parsing json.
                                e.printStackTrace();
                            }

                        } // end of length > 3 and contains Type if-statement
                    } // end of for each split

                    // Reset variables
                    message = null;
                    split = null;
                    type = null;
                    o = null;

                    if(message == null && split == null && type == null && o == null) {
                        // All is cleared
                    }
                }
                catch(SocketTimeoutException e) {
                    // read was stuck (over 10 secs)
                    Log.i(TAG, "TimeOut");
                    // Say system to run garbage collector in near future
                    System.gc();
                }
                catch(Exception e) {
                    // Say system to run garbage collector in near future
                    System.gc();
                    //e.printStackTrace(); //For Debug
                }
                finally {
                    // ################ TODO Maybe move "System.gc();" call to this position
                }
            }
        }
        catch(Exception e) {

            e.printStackTrace();

            mTCPListener.notifyNetworkError(e.getMessage());
        }
        finally {

            // Say system to run garbage collector in near future
            System.gc();
        }
    }

    public void kill() {
        bKeepRunning = false;
    }
}
