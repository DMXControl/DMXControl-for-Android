/*
 * CommunicationService.java
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

package de.dmxcontrol.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class ServiceFrontend {

    private final static String TAG = "network - Frontend";

    private static ServiceFrontend INSTANCE;
    private Context mContext;

    private NetworkService mService;

    // name for register and unregister process
    private static String mDeviceName = "";


    private IMessageListener mNetworkListener;

    public void setNetworkListener(IMessageListener listener) {
        mNetworkListener = listener;
    }


    public interface OnServiceListener {
        public void onServiceConnected();
        public void onServiceDisconnected();
    }

    private ArrayList<OnServiceListener> mServiceListeners = new ArrayList<OnServiceListener>();

    public void addServiceListener(OnServiceListener listener) {
        this.mServiceListeners.add(listener);
    }

    public void removeServiceListener(OnServiceListener listener) {
        this.mServiceListeners.remove(listener);
    }

    public void clearServiceListeners(OnServiceListener listener) {
        mServiceListeners.clear();
    }


    public static void setDeviceName(String name) {
        mDeviceName = name;
    }


    public static void initOnce(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new ServiceFrontend(context);
        }
    }

    private ServiceFrontend(Context context) {
        mContext = context;
        bindService();
    }

    public static ServiceFrontend get() {
        return INSTANCE;
    }


    public void bindService() {
        Intent intent = new Intent(mContext, NetworkService.class);

        boolean result = mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE | Context.BIND_DEBUG_UNBIND);

        if(result) {
            Log.d(TAG, "bindService bind successful.");
        }
        else {
            Log.d(TAG, "bindService bind unsuccessful.");
        }
    }

    public void unbindService() {
        Log.d(TAG, "unbindService: connection=" + connection.toString());
        if(mService != null) {
            mContext.unbindService(connection);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((NetworkService.LocalBinder) service).getService();
            connect();
            Log.d(TAG, "ServiceConnection: mService = " + mService);

        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ServiceConnection: nas is disconnected");
            mService = null;
        }
    };

    public void connect() {
        if(mService == null) {
            return;
        }

        // Set self as listener before connecting so we get all errors of network connection.
        mService.setSenderListener(mNetworkListener);

        // Start network service
        mService.connect();

        try {
            Thread.sleep(0); //Change if needed
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
        //register();

        if(isConnected()) {
            // We connected so let's notify our service listeners
            for(OnServiceListener listener : mServiceListeners) {
                listener.onServiceConnected();
            }
        }
        else {
            // error while trying to connect so notify our service listeners
            for(OnServiceListener listener : mServiceListeners) {
                listener.onServiceDisconnected();
            }
        }
    }

    public void disconnect(boolean silent) {
        if(!silent) {

            //unregister();
            try {
                // we need time to unregister
                Thread.sleep(0); //Change if needed
            }
            catch(InterruptedException e) {
                // Ignore
            }
        }

        if(mService != null) {
            mService.disconnect();
        }

        // We disconnected so notify our service listeners
        for(OnServiceListener listener : mServiceListeners) {
            listener.onServiceDisconnected();
        }

    }

    public boolean isConnected() {
        if(mService != null) {
            return mService.isConnected();
        }
        return false;
    }

    public void sendMessage(byte[] data) {
        if(mService != null) {
            mService.sendMessage(data);
        }
    }

    {
    /*
    maybe use such a thing later
    private void register() {
        OSCMessage msg = new OSCMessage(DMXCONTROL_OSC_ROOT + OSC_DELIMITER + OSC_REGISTER, new Object[]{DEVICENAME});
        if(mService != null) {
            mService.sendMessage(msg);
        }
    }

    private void unregister() {
        OSCMessage msg = new OSCMessage(DMXCONTROL_OSC_ROOT + OSC_DELIMITER + OSC_UNREGISTER, new Object[]{DEVICENAME});
        if(mService != null) {
            mService.sendMessage(msg);
        }
    }
    */
    }
}
