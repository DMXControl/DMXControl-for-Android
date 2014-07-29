package de.dmxcontrol.network.UDP;

import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;

/**
 * Created by Qasi on 12.06.2014.
 */
public class KernelPingDeserializer {
    public final static String AndroidAppPlugin = "AndroidApp-Plugin";
    public String GetDatagramType() {
        return datagramType;
    }

    public String GetHostName() {
        return hostName;
    }

    public String[] GetIPAdresses() {
        return iPAdresses;
    }

    public String GetVersion() {
        return version;
    }

    public String GetProject() {
        return project;
    }

    public boolean GetCompatible() {
        return compatible;
    }

    private String datagramType = "";
    private String hostName = "";
    private String[] iPAdresses = new String[0];
    private String version = "";
    private String project = "";
    private boolean compatible = false;

    public static KernelPingDeserializer Get(String str) {
        KernelPingDeserializer out = new KernelPingDeserializer();
        try {
            for(int i = 0; i < 0xffff; i++) {
                if((i < 0x0021 || i > 0x007e) && i != 0x0020) {
                    str = str.replace((char) i, (char) 0x00);
                }
            }

            char ch = (char) 0x00;
            String[] strArray = str.split(String.valueOf(ch));
            String[] strArrayOut = new String[strArray.length];
            int removeCount = 0;

            for(int i = 0; i < strArray.length; i++) {
                byte[] strig = strArray[i].getBytes();

                if(strig.length == 0) {
                    removeCount++;
                }
                else if(strig.length > 4) {
                    String org = (String.valueOf((char) strig[1]) + String.valueOf((char) strig[2]) + String.valueOf((char) strig[3]) + String.valueOf((char) strig[4]));

                    if(strArray[i].contains(AndroidAppPlugin)) {
                        out.compatible = true;
                    }
                    if(org.equals("org.")) {
                        strArray[i] = null;
                        removeCount++;
                    }
                    else {
                        strArrayOut[i - removeCount] = strArray[i];
                    }
                }
                else {
                    strArrayOut[i - removeCount] = strArray[i];
                }
            }

            out.datagramType = strArrayOut[4];
            out.version = strArrayOut[26];
            out.hostName = strArrayOut[19];
            out.iPAdresses = FindIPs(strArrayOut);
            out.project = strArrayOut[27];
        }
        catch(Exception e) {
            Log.e("KernelPingDeserializer", e.getMessage());
            DMXControlApplication.SaveLog();
        }
        return out;
    }

    private static String[] FindIPs(String[] strgs) {
        ArrayList<String> IPs = new ArrayList<String>();

        for(int i = 0; i < strgs.length; i++) {
            if(strgs[i] != null) {
                int l = strgs[i].toCharArray().length;
                String st = strgs[i];

                if(l <= 15 && l >= 7 && st.contains(".")) {
                    try {
                        InetAddress.getByName(st);
                        IPs.add(st.toString());
                    }
                    catch(Exception e) {

                    }
                }
            }
        }

        String[] out = new String[IPs.size()];

        for(int i = 0; i < out.length; i++) {
            out[i] = IPs.get(i);
        }
        return out;
    }
}
