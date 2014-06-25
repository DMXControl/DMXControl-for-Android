/*
 * EntityDevice.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2012 DMXControl-For-Android. All rights reserved.
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

package de.dmxcontrol.executor;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.network.UDP.Reader;

//This is One Executor
public class EntityExecutor extends Entity {
    public final static String defaultExecuterIcon = "device_new";
    public final static String NetworkID = "Executor";

    private float value;
    private boolean toggle;
    private int faderMode = 0;
    private boolean doGO;
    private boolean doStop;
    private boolean doBreakBack;
    private boolean flash;
    @Override
    public String getNetworkID() {
        return NetworkID;
    }

    private ArrayList<ValueChangedListener> ValueChangedListeners = new ArrayList<ValueChangedListener>();

    public void setValueChangedListener(ValueChangedListener listener) {
        this.ValueChangedListeners.add(listener);
    }

    public int getFaderMode() {
        return faderMode;
    }

    public void setToggle(boolean toggle, boolean fromReader) {
        this.toggle=toggle;
        if(!fromReader)
            Send();
    }

    public void setFaderMode(int faderMode, boolean fromReader) {
        this.faderMode=faderMode;
        if(!fromReader)
            Send();
    }

    public interface ValueChangedListener {
        void onValueChanged(float value);
    }

    private ArrayList<FlashChangedListener> FlashChangedListeners = new ArrayList<FlashChangedListener>();

    public void setFlashChangedListener(FlashChangedListener listener) {
        this.FlashChangedListeners.add(listener);
    }

    public interface FlashChangedListener {
        void onFlashChanged(float value);
    }

    public void BreakBack() {
        doBreakBack = true;
        Send();
    }
    public void GO() {
        doGO = true;
        Send();
    }
    public void Stop(){
        doStop = true;
        Send();
    }

    public boolean getToggle() {
        return toggle;
    }

    public void setValue(float value,boolean fromReader) {
        int comp = Float.compare(this.value, value);
        boolean isEqual = 0 == comp;
        this.value = value;
        if(!isEqual&&fromReader) {
            for (ValueChangedListener listener : ValueChangedListeners) {
                listener.onValueChanged(value);
            }
            return;
        }
        else if(!isEqual&&!fromReader) {
            Send();
        }
    }
    public float getValue() {
        return value;
    }

    public void setFlash(boolean flash,boolean fromReader) {
        boolean isEqual=this.flash==flash;
        this.flash = flash;

        if(!isEqual&&fromReader) {
            for (FlashChangedListener listener : FlashChangedListeners) {
                listener.onFlashChanged(value);
            }
            return;
        }
        if(!isEqual&&!fromReader) {
            Send();
        }
        //Prefs.get().getUDPSender().addSendData(new byte[]{(byte)0xff});
        //Send();
    }
    public boolean getFlash() {
        return flash;
    }


    public EntityExecutor(int id) {
        super(id, NetworkID + ": " + id, Type.EXECUTOR);
        mImage = defaultExecuterIcon;
    }

    public EntityExecutor(int id, String name) {
        super(id, name, Type.EXECUTOR);
        mImage = defaultExecuterIcon;
    }

    public EntityExecutor(int id, String name, String image) {
        super(id, name, Type.EXECUTOR);
        mImage = image;
    }

    public EntityExecutor(JSONObject o) {
        super(0,"",Type.EXECUTOR);
        Receive(o);
    }

    public static EntityExecutor Receive(JSONObject o) {
        EntityExecutor entity = null;
        try {
            if (o.getString("Type").equals(NetworkID)) {
                entity = new EntityExecutor(o.getInt("Number"), o.getString("Name"));
                entity.guid = o.getString("GUID");
                entity.value = Float.parseFloat(o.getString("Value").replace(",", "."));//Float.parseFloat(svalue.replace(",", "."));
                entity.flash = o.getBoolean("Flash");
                entity.toggle = o.getBoolean("Toggle");
                entity.faderMode = o.getInt("FaderMode");
            }
        }
        catch(Exception e)
        {
            Log.e("UDP Listener", e.getMessage());
            DMXControlApplication.SaveLog();
        }
        return entity;
    }

    public void Send() {
        try {
            JSONObject o = new JSONObject();
            o.put("Type", NetworkID);
            o.put("GUID", this.guid);
            o.put("Name", this.getName());
            o.put("Value", this.value);
            o.put("Flash", this.flash);
            o.put("Number", this.getId());
            if(this.doGO){o.put("GO", true);}
            if(this.doBreakBack){o.put("BreakBack", true);}
            if(this.doStop){o.put("Stop", true);}

            Prefs.get().getUDPSender().addSendData(o.toString().getBytes());
            return;
        }
        catch(Exception e) {
            Log.e("UDP Send: ", e.getMessage());
            DMXControlApplication.SaveLog();
        }
    }

    public static void SendAllRequest(){
        byte[] output = new byte[4];
        output[0] = (byte) Reader.Type.EXECUTOR.ordinal();
        output[1] = 'A';
        output[2] = 'L';
        output[3] = 'L';
        Prefs.get().getUDPSender().addSendData(output);
    }

}
