/*
 * OSCService.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import de.dmxcontrol.app.Prefs;
import de.sciss.net.OSCBundle;
import de.sciss.net.OSCChannel;
import de.sciss.net.OSCClient;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCPacketCodec;

public class OSCService extends Service {
    private final static String TAG = "network";

    private String mHost;
    private int mPort;

    // replace this
    private OSCClient mClient;

    private String mConnectionType;

    // replace this
    private ConcurrentHashMap<String, OSCMessage> mMessageMap;

    private volatile Thread mPacketSender;
    private int mWaitInMillis;
    private final static int TIME_WAIT_IN_MILLIS = 25;
    private final static int mLocalPort = 8001;

    // rename this. Need this?
    private final static int OSC_BUNDLE_TIME_VALID = 50;

    private INetworkSenderListener mDefaultSenderListener = new INetworkSenderListener() {
        public void notifyNetworkError(String msg) {
            Log.d(TAG, "SenderListener:notifyNetworkError: " + msg);
        }

        public void notifyError(String msg) {
            Log.d(TAG, "SenderListener:notifyError: " + msg);
        }

        public void notifyInterrupted() {
            Log.d(TAG, "SenderListener:notifyError: Interrupted.");
        }

    };

    private INetworkSenderListener mSenderListener = mDefaultSenderListener;
    private boolean mIsConnected;

    public class LocalBinder extends Binder {
        public OSCService getService() {
            return OSCService.this;
        }
    }

    ;

    @Override
    public void onCreate() {
        super.onCreate();

        // replace this
        mConnectionType = OSCClient.UDP;
        mWaitInMillis = TIME_WAIT_IN_MILLIS;

        // Ehat the hag is this used for?
        mMessageMap = new ConcurrentHashMap<String, OSCMessage>();

        //connect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void start() {
        Log.d(TAG, "Starting Thread");

        if(isConnected()) {
            // replace name here

            // create an start thread of sender
            mPacketSender = new Thread(new SenderRunnable(), "OSCSender");
            mPacketSender.start();
        }
    }

    private void stop() {
        Log.d(TAG, "Stopping Thread");

        Thread tmpThread = mPacketSender;
        mPacketSender = null;

        if(tmpThread != null) {
            tmpThread.interrupt();
        }

        try {
            mClient.stop();
        }
        catch(IOException e) {
            if(mSenderListener != null) {
                mSenderListener.notifyError(e.getMessage());
            }
        }

        mClient.dispose();
        mClient = null;
    }

    public void connect() {
        try {
            // Stop client if running
            if(mClient != null && mClient.isActive()) {
                mClient.stop();
                mClient = null;
            }

            // get adress and port from prefs
            mHost = Prefs.get().getServerAddress();
            mPort = Prefs.get().getServerPort();

            // create new client
            mClient = OSCClient.newUsing(OSCPacketCodec.getDefaultCodec(), mConnectionType, mLocalPort, false);

            if(mHost.length() > 0) {
                Log.d(TAG, "OSC Target is " + mHost + ":" + mPort);

                mClient.setTarget(new InetSocketAddress(mHost, mPort));
                mClient.start();
                mIsConnected = true;

                InetSocketAddress address = mClient.getLocalAddress();
                Log.d(TAG, "OSCManager: localAddress:" + address.toString());

            }
            else {
                throw new IOException("No server address given. Consult settings.");
            }
        }
        catch(IOException e) {

            Prefs.get().setOffline(true);

            if(mSenderListener != null) {
                mSenderListener.notifyNetworkError(e.getMessage());
            }
        }

        if(!isRunning()) {
            start();
        }
    }

    public void disconnect() {
        if(isRunning()) {
            stop();
        }

        try {
            if(mClient != null && mClient.isActive()) {
                mClient.stop();
                mClient = null;
            }
        }
        catch(IOException e) {
            Prefs.get().setOffline(true);
            if(mSenderListener != null) {
                mSenderListener.notifyNetworkError(e.getMessage());
            }
        }
        mIsConnected = false;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public boolean isRunning() {
        return mPacketSender != null;
    }

    public void setSenderListener(INetworkSenderListener listener) {
        mSenderListener = listener;
    }

    public void setWaitInMillis(int waitInMillis) {
        mWaitInMillis = waitInMillis;
    }

    // replace OSCMessage here
    public void addMessage(OSCMessage msg) {
        mMessageMap.put(msg.getName(), msg);
    }

    public void clearMessages() {
        mMessageMap.clear();
    }

    public void notifyAllMessages() {
        synchronized(mMessageMap) {
            mMessageMap.notifyAll();
        }
    }

    // replace OSCMessage
    public void sendMessage(OSCMessage msg) {
        try {
            Log.d(TAG, "OSCManager:sendMessage");
            mClient.send(msg);
        }
        catch(IOException e) {
            // Prefs.get().setOffline( true );
            if(mSenderListener != null) {
                mSenderListener.notifyNetworkError(e.getMessage());
            }
        }
    }

    // do we need this
    public void addListener(OSCListener listener) {
        mClient.addOSCListener(listener);
    }

    // dump dump function :-P
    public void startDumpOSC() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Environment
                    .getExternalStorageDirectory().getCanonicalPath()
                    + File.separator + "oscdump.txt");
        }
        catch(FileNotFoundException e) {
            if(mSenderListener != null) {
                mSenderListener.notifyError(e.getMessage());
            }
            return;
        }
        catch(IOException e) {
            if(mSenderListener != null) {
                mSenderListener.notifyError(e.getMessage());
            }
            return;
        }
        PrintStream print = new PrintStream(out);
        mClient.dumpOSC(OSCChannel.kDumpBoth, print);
    }

    // dump dump function :-P
    public void stopDumpOSC() {
        mClient.dumpOSC(OSCChannel.kDumpOff, null);
    }

    private class SenderRunnable implements Runnable {

        @Override
        public void run() {
            if(mPacketSender == null) {
                return;
            }
            Thread thisThread = Thread.currentThread();

            while(mPacketSender == thisThread) {
                try {
                    Thread.sleep((long) mWaitInMillis, 0);
                }
                catch(InterruptedException e1) {
                    continue;
                }

                int messageCount;
                while((messageCount = mMessageMap.size()) == 0) {
                    try {
                        synchronized(mMessageMap) {
                            mMessageMap.wait();
                        }
                    }
                    catch(InterruptedException e) {
                        continue;
                    }
                }

                if(messageCount == 1) {
                    String key = (String) mMessageMap.keySet().toArray()[0];
                    try {
                        OSCMessage msg = mMessageMap.remove(key);
                        if(mClient != null) {
                            Log.d(TAG, "OSCManager:SenderRunnable: sending message (" + msg.getName() + " ");
                            mClient.send(msg);
                        }
                    }
                    catch(IOException e) {
                        OSCService.this.stop();
                        Prefs.get().setOffline(true);
                        if(mSenderListener != null) {
                            mSenderListener.notifyNetworkError(e.getMessage());
                        }
                    }
                }
                else {
                    OSCBundle bundle = new OSCBundle(System.currentTimeMillis() + OSC_BUNDLE_TIME_VALID);
                    Iterator<String> iter = mMessageMap.keySet().iterator();
                    while(iter.hasNext()) {
                        bundle.addPacket(mMessageMap.remove(iter.next()));
                    }
                    try {
                        if(mClient != null) {
                            Log.d(TAG,
                                    "OSCManager:SenderRunnable: sending bundle");
                            mClient.send(bundle);
                        }
                    }
                    catch(IOException e) {
                        OSCService.this.stop();
                        Prefs.get().setOffline(true);
                        if(mSenderListener != null) {
                            mSenderListener.notifyNetworkError(e.getMessage());
                        }
                    }
                }
            }
            if(mSenderListener != null) {
                mSenderListener.notifyInterrupted();
            }
        }
    }

}