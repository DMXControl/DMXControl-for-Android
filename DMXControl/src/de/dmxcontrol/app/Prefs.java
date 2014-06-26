/*
 * Prefs.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2011 DMXControl-For-Android. All rights reserved.
 *
 *      This software is free software; you can redistribute it and/or
 *      modify it under the terms of the GNU General Public License
 *      as published by the Free Software Foundation; either
 *      version 3, june 2007 of the License, or (at your option) any later version.
 *
 *      This software is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *      General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public
 *      License (gpl.txt) along with this software; if not, write to the Free Software
 *      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *      For further information, please contact info [(at)] dmxcontrol.de
 *
 * 
 */

package de.dmxcontrol.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import de.dmxcontrol.activity.ControlActivity;
import de.dmxcontrol.network.ServiceFrontend;
import de.dmxcontrol.network.UDP.KernelPingDeserializer;
import de.dmxcontrol.network.UDP.Reader;
import de.dmxcontrol.network.UDP.ReaderKernelPing;
import de.dmxcontrol.network.UDP.Sender;

public class Prefs {
    private final static String TAG = "dmxcontrol";

    private static Prefs INSTANCE = null;
    private boolean viewConfigChanged;
    private boolean connectConfigChanged;
    private boolean versionChanged;

    private String mDeviceName = "";
    private String mServerAddress = "";
    private String mServerHost = "";
    private int mServerPort;
    private boolean mOffline;
    private boolean mDisableSplash;
    private boolean mDisableAnimations;
    private int mScreenMode;

    public static final int SCREEN_MODE_AUTOMATIC = 0;
    public static final int SCREEN_MODE_PORTRAIT = 1;
    public static final int SCREEN_MODE_LANDSCAPE = 2;

    //private Thread reader1;
    private ReaderKernelPing Pingreader;
    private Reader reader;
    private Sender sender;

    public Reader getUDPReader() {
        return reader;
    }

    public ReaderKernelPing getUDPReaderKernelPing() {
        return Pingreader;
    }

    public Sender getUDPSender() {
        return sender;
    }

    private Context ctx;

    public Prefs() {
        StartNetwork();
    }

    public void StartNetwork() {
        CloseNetwork();
        Pingreader = new ReaderKernelPing();
        Thread Pingreader1 = new Thread(Pingreader);
        Pingreader1.start();

        reader = new Reader();
        Thread reader1 = new Thread(reader);
        reader1.start();

        sender = new Sender();
        Thread sender1 = new Thread(sender);
        sender1.start();
    }

    public void CloseNetwork() {
        if(Pingreader != null) {
            Pingreader.kill();
        }

        if(reader != null) {
            reader.kill();
        }

        if(sender != null) {
            sender.kill();
        }
    }

    public static Prefs get() {
        if(INSTANCE == null) {
            INSTANCE = new Prefs();
        }

        return INSTANCE;
    }

    public void setPreferences(Context ctx) {
        this.ctx = ctx;
        Log.d(TAG, "Prefs.setPreferences");

        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        //Set<String> seter= new String[3];
        try {
            //if( reader.GetLastKernelPing().GetHostName()!=null){
            //prefs.edit().putString("@array/founded_servers_values", "sdsfsdfsd" /**reader.GetLastKernelPing().GetHostName()**/);
            //}
        }
        catch(Exception e) {
            e.toString();
        }
        viewConfigChanged = false;
        connectConfigChanged = false;
        versionChanged = false;

        String deviceName = prefs.getString("pref_connect_device_name", "My Android Phone");

        if(!mDeviceName.equals(deviceName)) {
            connectConfigChanged = true;
        }
        mDeviceName = deviceName;
        ServiceFrontend.setName(mDeviceName);

        String serverAddress = prefs.getString("pref_connect_address", "");

        if(!mServerAddress.equals(serverAddress)) {
            connectConfigChanged = true;
        }
        mServerAddress = serverAddress;
        String serverHost = prefs.getString("pref_selected_servers", "");

        if(!mServerHost.equals(serverHost)) {
            connectConfigChanged = true;
        }
        mServerHost = serverHost;

        int serverPort = Integer.valueOf(prefs.getString("pref_connect_port", "8001"));
        if(mServerPort != serverPort) {
            connectConfigChanged = true;
        }
        mServerPort = serverPort;

        boolean offline = prefs.getBoolean("pref_connect_offline", false);
        if(mOffline != offline) {
            connectConfigChanged = true;
        }
        mOffline = offline;

        int screenMode = Integer.valueOf(prefs.getString("pref_screen_mode", "0"));
        mScreenMode = screenMode;
        ControlActivity.setScreenMode(mScreenMode);

        boolean disableSplash = prefs.getBoolean("pref_disable_splash", false);
        mDisableSplash = disableSplash;
        ControlActivity.setDisableSplash(mDisableSplash);

        mDisableAnimations = prefs.getBoolean("pref_disable_animations", true);

        String versionValue = prefs.getString("version_def", "v0.0");
        String versionPackageValue;

        try {
            versionPackageValue = ctx.getPackageManager().getPackageInfo("de.dmxcontrol", 0).versionName;
        }
        catch(NameNotFoundException e) {
            versionPackageValue = "v0.0";
        }

        Log.d(TAG, "versionValue = " + versionValue + " versionPackageValue = " + versionPackageValue);

        if(versionValue.equals("v0.0") || !versionValue.equals(versionPackageValue)) {
            versionChanged = true;
            prefs.edit().putString("version_def", versionPackageValue).commit();
        }
    }

    public boolean viewConfigChanged() {
        boolean result = viewConfigChanged;
        viewConfigChanged = false;
        return result;
    }

    public boolean versionChanged() {
        boolean result = versionChanged;
        versionChanged = false;
        return result;
    }

    public boolean connectConfigChanged() {
        boolean result = connectConfigChanged;
        connectConfigChanged = false;
        return result;
    }

    public void setOffline(boolean offline) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mOffline = prefs.getBoolean("pref_connect_offline", false);

        if(offline != mOffline) {
            prefs.edit().putBoolean("pref_connect_offline", offline).commit();
        }

        mOffline = offline;
    }

    public boolean getOffline() {
        return mOffline;
    }

    public String getServerAddress() {
        return mServerAddress;
    }

    public int getServerPort() {
        return mServerPort;
    }

    public boolean getDisableAnimations() {
        return mDisableAnimations;
    }

    public ArrayList<KernelPingDeserializer> getKernelPing() {
        return Pingreader.GetKernelPings();
    }

    public void setServerAddress(String serverAddress) {
        this.mServerAddress = serverAddress;
    }
}