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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.network.TCP.TCPReader;
import de.dmxcontrol.network.TCP.TCPSender;

public class NetworkService extends Service {

    private static final Pattern IP_ADDRESS = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))"
    );

    // Tag for log messages
    private final static String TAG = "NetworkService";

    // Port of communication
    private final int serverPrt = 13141;

    // Runnable instance of sender
    private TCPSender mSender;

    // Runnable Instance of reader
    private TCPReader mReader;

    // Thread that sends the data out
    private volatile Thread mSenderThread;

    // Thread that reads the incoming data
    private volatile Thread mReaderThread;

    // network service status
    private boolean mNetworkServiceIsConnected = false;

    // Listener for
    private IMessageListener mServiceListener;

    public class LocalBinder extends Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
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


    public void connect() {
        try {
            // Stop sender and reader
            disconnect();

            // get address and port from prefs
            String serverAddress = Prefs.get().getServerAddress();

            Matcher matcher = IP_ADDRESS.matcher(serverAddress);

            // TODO: 11.10.15 Hostname also possible here??
            
            if(matcher.matches()) {
                Log.d(TAG, "Network Target is " + serverAddress + ":" + serverPrt);

                Log.d(TAG, "Starting sender thread");
                // create new sender
                mSender = new TCPSender(serverAddress, serverPrt); // throws UnknwonHostException

                // set listener
                if(mServiceListener != null) {
                    mSender.setTCPListener(mServiceListener);
                }

                // create and start thread of sender
                mSenderThread = new Thread(mSender, "NetworkSender");
                mSenderThread.start();

                Log.d(TAG, "Starting reader thread");
                // create new reader
                mReader = new TCPReader(mSender);

                // set listener
                if(mServiceListener != null) {
                    mReader.setTCPListener(mServiceListener);
                }

                // create, set priority to min and start thread of reader
                mReaderThread = new Thread(mReader, "NetworkReader");

                mReaderThread.setPriority(Thread.MIN_PRIORITY);
                mReaderThread.start();

                mNetworkServiceIsConnected = true;

                // We are online now
                Prefs.get().setOffline(false);
            }
            else {
                throw new IOException("No or wrong server address given. Consult settings.");
            }
        }
        catch(Exception e) {

            disconnect();

            if(mServiceListener != null) {
                mServiceListener.notifyNetworkError(e.getMessage());
            }
        }
    }

    public void disconnect() {
        Log.d(TAG, "Stopping sender thread");

        if(mSender != null && mSenderThread != null) {
            // stop run loop and close socket
            mSender.kill();

            // we have to wait until the sockets are closed
            try {
                // maybe a little bit too long but want to close socket properly
                mSenderThread.join(1000);
            }
            catch(InterruptedException e) {
                // join was interrupted
            }
        }

        mSender = null;

        mSenderThread = null;

        Log.d(TAG, "Stopping reader thread");

        if(mReader != null && mReaderThread != null) {
            // stop run loop and close socket
            mReader.kill();

            try {
                // maybe a little bit too long but want to close socket properly
                mReaderThread.join(1000);
            }
            catch(InterruptedException e) {
                // join was interrupted
            }
        }

        mReader = null;

        mReaderThread = null;

        mNetworkServiceIsConnected = false;

        // We got disconnected so lets go offline in settings
        Prefs.get().setOffline(true);
    }

    public boolean isConnected() {
        return mNetworkServiceIsConnected;
    }

    public void sendMessage(byte[] data) {
        // only add data if we're connected
        if(mNetworkServiceIsConnected) {
            mSender.addSendData(data);
        }
    }


    public void setSenderListener(IMessageListener listener) {
        mServiceListener = listener;
    }

}