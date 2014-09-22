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

package de.dmxcontrol.device;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.file.FileManager;
import de.dmxcontrol.network.ServiceFrontend;

//This is One Device
public class EntityDevice extends Entity {
    public final static String defaultDeviceIcon = "device_new.png";
    public static String NetworkID = "Device";
    private int channel, channelCount, color;
    private String model, vendor, author, image;
    private boolean enabled;
    private DevicePropertyCollection Propertys;
    private DeviceProcedureCollection Procedures;

    @Override
    public String getNetworkID() {
        return NetworkID;
    }

    public static void SendRequest(String request) {
        SendRequest(EntityDevice.class, request);
    }

    @Override
    public void Send() {
        try {
            JSONObject o = new JSONObject();
            o.put("Type", NetworkID);
            o.put("GUID", this.guid);
            o.put("Name", this.getName());
            o.put("Number", this.getId());
            o.put("Channel", this.getChannel());
            o.put("Enabled", this.getEnabled());

            ServiceFrontend.get().sendMessage(o.toString().getBytes());
            o = null;
            if(o == null) {
                ;
            }
            return;
        }
        catch(Exception e) {
            Log.e("UDP Send: ", e.getMessage());
            DMXControlApplication.SaveLog();
        }
    }

    public void ExecuteProcedure(DeviceProcedure procedure) {
        try {
            JSONObject o = new JSONObject();
            o.put("Type", NetworkID);
            o.put("GUID", this.guid);
            o.put("Procedure", procedure.getName());

            ServiceFrontend.get().sendMessage(o.toString().getBytes());
            o = null;
            if(o == null) {
                ;
            }
            return;
        }
        catch(Exception e) {
            Log.e("UDP Send: ", e.getMessage());
            DMXControlApplication.SaveLog();
        }
    }

    public EntityDevice() {
    }

    public EntityDevice(int id) {
        super(id, NetworkID + ": " + id, Type.DEVICE);
        mImage = defaultDeviceIcon;
    }

    public EntityDevice(int id, String name) {
        super(id, name, Type.DEVICE);
        mImage = defaultDeviceIcon;
    }

    public EntityDevice(int id, String name, String image) {
        super(id, name, Type.DEVICE);
        mImage = image;
    }

    public static Bitmap getDefaultIcon(Context context) {

        File imgFile = new File(FileManager.ImageStorageName + File.separator + defaultDeviceIcon);
        if(imgFile.isFile()) {
            if(imgFile.exists()) {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        }

        // Replace this icon with something else
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
    }

    public static EntityDevice Receive(JSONObject o) {
        EntityDevice entity = null;
        try {
            if(o.getString("Type").equals(NetworkID)) {
                entity = new EntityDevice(o.getInt("Number"), o.getString("Name"), o.getString("Image"));
                entity.guid = o.getString("GUID");
                if(o.has("Channel")) {
                    if(!o.getString("Channel").equals("null")) {
                        entity.channel = o.getInt("Channel");
                    }
                    else {
                        entity.channel = Integer.MIN_VALUE;
                    }
                }
                if(o.has("ChannelCount")) {
                    entity.channelCount = o.getInt("ChannelCount");
                }
                if(o.has("Color")) {
                    entity.color = o.getInt("Color");
                }
                if(o.has("Model")) {
                    entity.model = o.getString("Model");
                }
                if(o.has("Vendor")) {
                    entity.vendor = o.getString("Vendor");
                }
                if(o.has("Author")) {
                    entity.author = o.getString("Author");
                }
                if(o.has("Enabled")) {
                    entity.enabled = o.getBoolean("Enabled");
                }
                if(o.has("Image")) {
                    if(!o.getString("Image").equals("null")) {
                        entity.image = o.getString("Image");
                    }
                    else {
                        entity.setImage(defaultDeviceIcon);
                    }
                }

                entity.Propertys = new DevicePropertyCollection(o);
                entity.Procedures = new DeviceProcedureCollection(o);
            }
            o = null;
        }
        catch(Exception e) {
            Log.e("Entity device", e.getMessage());
            DMXControlApplication.SaveLog();
        }
        o = null;
        if(o == null) {
            ;
        }
        return entity;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel, boolean fromReader) {
        boolean isEqual = this.channel == channel;
        this.channel = channel;

        if(!isEqual && !fromReader) {
            Send();
        }
    }

    public int getChannelCount() {
        return channelCount;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public String getModel() {
        return model;
    }

    public String getVendor() {
        return vendor;
    }

    public String getAuthor() {
        return author;
    }

    public void setEnabled(boolean enabled, boolean fromReader) {
        boolean isEqual = this.enabled == enabled;
        this.enabled = enabled;

        if(!isEqual && !fromReader) {
            Send();
        }
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }
}
