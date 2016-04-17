package de.dmxcontrol.network.TCP;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import java.net.UnknownHostException;
import java.util.ArrayList;

import de.dmxcontrol.cuelist.EntityCuelist;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.executor.EntityExecutor;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.network.IMessageListener;
import de.dmxcontrol.preset.EntityPreset;
import de.dmxcontrol.programmer.EntityProgrammer;

/**
 * Created by Qasi on 01.07.2014.
 */

public class TCPSender implements Runnable {

    private final static String TAG = "Network-Sender";
    private boolean bKeepRunning;
    private ArrayList<byte[]> sendData = new ArrayList<byte[]>();
    private InetAddress mServerAddress;
    private Socket client;
    private int mServerPort;

    private IMessageListener mTCPListener;

    public void setTCPListener(IMessageListener listener) {
        mTCPListener = listener;
    }
    
    public Socket getSocket() {
        return this.client;
    }

    private void writeMessage(java.net.Socket socket, String message) throws IOException {

        if(socket == null) {
            return;
        }

        OutputStreamWriter printWriter = new OutputStreamWriter(socket.getOutputStream()); // getOutputStream throws IOException
        printWriter.write(message); // throws IOException
        printWriter.flush(); // throws IOException
    }

    private void sendDataOut() {

        byte[] out;

        for(int i = 0; i < sendData.size(); i++) {

            try {
                out = sendData.get(i);
                if(out != null) {
                    if(out.length > 0) {
                        writeMessage(client, new String(out, 0, out.length));
                    }
                }
                sendData.remove(out);
            }
            catch(UnknownHostException e) {
                Log.w("TCPSender", "UnknownHostException:" + e.getMessage());
                e.printStackTrace();
            }
            catch(IOException e) {
                Log.w("TCPSender", "IOException: " + e.getMessage());
                e.printStackTrace();
            }
            catch(NullPointerException e) {
                Log.w("TCPSender", "NullPointerException: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public TCPSender(String serverAddress, int serverPort) throws UnknownHostException {
        super();

        mServerPort = serverPort;

        mServerAddress = InetAddress.getByName(serverAddress); // throws UnknownHostException
    }

    public void addSendData(byte[] data) {
        sendData.add(data);
    }

    public void run() {
        bKeepRunning = true;

        // counter for GUID requests
        byte count = 0;

        try {
            // run till bKeepRunning is set to false via kill().
            while(bKeepRunning) {

                // if socket isn't null
                if(client != null) {

                    // if socket is closed just set to null
                    if(client.isClosed()) {
                        client = null;
                    }

                    // if socket isn't connected and is open (not closed) -> close and null socket
                    if(!client.isConnected() && !client.isClosed()) {
                        try {
                            client.close();
                        }
                        catch(IOException e) {
                            // port can't be closed. Shouldn't be that bad.
                            Log.d(TAG , e.getMessage());
                        }
                        finally {
                            client = null;
                        }
                    }
                }

                // connect socket if it's null
                if(client == null) {
                    try {
                        // create new socket
                        client = new Socket(mServerAddress, mServerPort); // throws IOException & ConnectException
                        // use keepAlive packet to check if connection is still valid
                        client.setKeepAlive(true); // throws SocketException
                        // wait 1 sec so that socket can init correctly
                        Thread.sleep(1000); // throws InterruptedException
                    }
                    catch(ConnectException e) {
                        throw e;
                    }
                    catch(IOException e) {
                        if(e.getMessage().contains("(Connection timed out)")) {
                            // No EXCEPTION!!! What function throws this exception?
                        }
                        else {
                            // throw IOException of new Socket (use other outer catch block).
                            throw  e;
                        }
                    }
                    catch(InterruptedException e) {
                        // We didn't slept the full time but this shouldn't be that bad here
                        Log.d(TAG + "-InitSock", e.getMessage());
                    }
                }


                if(count == 0) {
                    EntityDevice.SendRequest(EntityDevice.class, Entity.Request_All_GUIDs);
                }
                else if(count == 8) {
                    EntityGroup.SendRequest(EntityGroup.class, Entity.Request_All_GUIDs);
                }
                else if(count == 16) {
                    EntityExecutor.SendRequest(EntityExecutor.class, Entity.Request_All_GUIDs);
                }
                else if(count == 24) {
                    EntityExecutorPage.SendRequest(EntityExecutorPage.class, Entity.Request_All_GUIDs);
                }
                else if(count == 32) {
                    EntityCuelist.SendRequest(EntityCuelist.class, Entity.Request_All_GUIDs);
                }
                else if(count == 40) {
                    EntityCuelist.SendRequest(EntityPreset.class, Entity.Request_All_GUIDs);
                }
                else if(count == 48) {
                    EntityCuelist.SendRequest(EntityProgrammer.class, Entity.Request_All_GUIDs);
                }

                count++;
                if(count > 55) {
                    count = 0;
                }

                sendDataOut();

                try {
                    Thread.sleep(33);
                }
                catch(InterruptedException e) {
                    // We didn't slept the full time but this shouldn't be that bad here
                    Log.d(TAG + "-RunLoop", e.getMessage());
                }
            }
        }
        catch(ConnectException e) { // catch for new Socket
            if(mTCPListener != null) {
                mTCPListener.notifyNetworkError("Failed to connect to " + mServerAddress.getHostAddress());
            }
        }
        catch(IOException e) { // catch for sendDataOut and new Socket
            if(mTCPListener != null) {
                mTCPListener.notifyNetworkError("I/O error. Can't access tcp port." + e.getMessage());
            }
        }
        finally {
            // set it to false if we got stopped by exception
            bKeepRunning = false;

            // Clear data
            sendData.clear();
 
            // close socket if open
            if(client != null) {
                if(!client.isClosed()) {
                    try {
                        client.close();
                    }
                    catch(IOException e) {
                        // Shouldn't be that bad
                        e.printStackTrace();
                    }
                    finally {
                        client = null;
                    }
                }
            }
        }
    }

    public void kill() {
        bKeepRunning = false;
        // don't need to close socket here because it's closed in finally of run loop
    }
}