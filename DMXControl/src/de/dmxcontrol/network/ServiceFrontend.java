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

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.device.Range;
import de.dmxcontrol.model.BaseModel;
import de.dmxcontrol.model.BaseModel.OnModelListener;
import de.sciss.net.OSCMessage;

public class ServiceFrontend implements IMessageListener, OnModelListener {
    private final static String TAG = "network";

    private static ServiceFrontend INSTANCE;
    private Context mContext;
    private OSCService mService;
    private IMessageListener mListener;
    private Map<OnServiceListener, Boolean> mListeners;

    private static String DEVICENAME = "";

    private final static String DMXCONTROL_OSC_ROOT = "/DMXControl";
    private final static String OSC_DELIMITER = "/";
    private final static String OSC_REGISTER = "register";
    private final static String OSC_UNREGISTER = "unregister";
    private final static String OSC_BEAMNUMBER = "*";

    private final static String OSC_DMXC_FIXTURE = "fixture";
    private final static String OSC_DMXC_GROUP = "group";

    public interface OnServiceListener {
        public void onServiceChanged(ServiceFrontend dm);
    }

    public static void initOnce(Context context) {
        if (INSTANCE == null)
            INSTANCE = new ServiceFrontend(context);
    }

    private ServiceFrontend(Context context) {
        mContext = context;
        mListeners = new WeakHashMap<OnServiceListener, Boolean>();
        bindService();
    }

    public static ServiceFrontend get() {
        return INSTANCE;
    }

    public void bindService() {
        Intent intent = new Intent(mContext, OSCService.class);

        boolean result = mContext.bindService(intent, connection,
                Context.BIND_AUTO_CREATE | Context.BIND_DEBUG_UNBIND);

        if (result) {
            Log.d(TAG, "bindService bind successful.");
        } else {
            Log.d(TAG, "bindService bind unsuccessful.");
        }
    }

    public void unbindService() {
        Log.d(TAG, "unbindService: connection=" + connection.toString());
        if (mService != null)
            mContext.unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((OSCService.LocalBinder) service).getService();
            Log.d(TAG, "ServiceConnection: mService = " + mService);

        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ServiceConnection: nas is disconnectd");
            mService = null;
        }
    };

    public static void setName(String name) {
        DEVICENAME = name;
    }

    public void setListener(IMessageListener listener) {
        mListener = listener;
    }

    public void connect() {
        if (mService != null) {
            mService.connect();
            mService.setSenderListener(mListener);
        }
        register();
    }

    public void disconnect(boolean silent) {
        if (!silent) {
            unregister();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        if (mService != null)
            mService.disconnect();
    }

    public boolean isConnected() {
        if (mService != null)
            return mService.isConnected();
        return false;
    }

    private void register() {
        OSCMessage msg = new OSCMessage(DMXCONTROL_OSC_ROOT + OSC_DELIMITER
                + OSC_REGISTER, new Object[]{DEVICENAME});
        if (mService != null)
            mService.sendMessage(msg);
    }

    private void unregister() {
        OSCMessage msg = new OSCMessage(DMXCONTROL_OSC_ROOT + OSC_DELIMITER
                + OSC_UNREGISTER, new Object[]{DEVICENAME});
        if (mService != null)
            mService.sendMessage(msg);
    }

    private void processAttributes(BaseModel model) {
        processAttributes(Type.DEVICE, OSC_DMXC_FIXTURE, model);
        processAttributes(Type.GROUP, OSC_DMXC_GROUP, model);

        if (mService != null)
            mService.notifyAllMessages();
    }

    private void processAttributes(Type type, String deviceType, BaseModel model) {
        for (Range range : model.getEntitySelection().getRangesList(type)) {
            OSCMessage msg = createOSCMessage(deviceType, range, model);

            if (mService != null)
                mService.addMessage(msg);
        }
    }

    private OSCMessage createOSCMessage(String deviceType, Range range,
                                        BaseModel model) {
        String oscPath = String.format("%s%s%s%s%s%s%s%s%s",
                DMXCONTROL_OSC_ROOT, OSC_DELIMITER, deviceType, OSC_DELIMITER,
                range.getOSCRange(), OSC_DELIMITER, OSC_BEAMNUMBER,
                OSC_DELIMITER, model.getOSCAttributeName());
        // Log.d(TAG, "createOSCMessage: oscPath = " + oscPath);
        OSCMessage msg = new OSCMessage(oscPath, model.getOSCAttributes());
        return msg;
    }

    @Override
    public void notifyNetworkError(String msg) {
        notifyListener();
    }

    @Override
    public void notifyError(String msg) {
        notifyListener();
    }

    @Override
    public void notifyInterrupted() {
        notifyListener();
    }

    public void addListener(OnServiceListener listener) {
        mListeners.put(listener, true);
    }

    public void removeListener(OnServiceListener listener) {
        mListeners.remove(listener);
    }

    public void notifyListener() {
        Iterator<OnServiceListener> iter = mListeners.keySet().iterator();
        while (iter.hasNext()) {
            OnServiceListener listener = iter.next();
            listener.onServiceChanged(this);
        }
    }

    @Override
    public void onModelChanged(BaseModel model) {
        processAttributes(model);
    }
}
