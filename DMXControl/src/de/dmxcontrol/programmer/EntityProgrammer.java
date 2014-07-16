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

package de.dmxcontrol.programmer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.network.ServiceFrontend;

public class EntityProgrammer extends Entity {
    public final static String NetworkID = "Programmer";
    private String[] propertyValueTypes;

    private static EntityProgrammer INSTANCE = null;

    public static EntityProgrammer get() {
        if(INSTANCE == null) {
            INSTANCE = new EntityProgrammer();
        }
        return INSTANCE;
    }

    @Override
    public String getNetworkID() {
        return NetworkID;
    }

    public int getGroupCount() {
        return 0;
    }

    public int getDeviceCount(int group) {
        return 0;
    }

    public Object getGroup(int group) {
        return null;
    }

    public Object getDevice(int group, int device) {
        return null;
    }

    public static void SendRequest(String request) {
        SendRequest(EntityProgrammer.class, request);
    }

    private EntityProgrammer() {
    }


    public static EntityProgrammer Receive(JSONObject o) {
        EntityProgrammer entity = get();
        try {
            if(o.getString("Type").equals(NetworkID)) {
                entity.setId(o.getInt("Number"));
                entity.setName(o.getString("Name"), true);
                entity.guid = o.getString("GUID");
                JSONArray a = o.getJSONArray("PropertyTypes");
                entity.propertyValueTypes = new String[a.length()];
                for(int i = 0; i < entity.propertyValueTypes.length; i++) {
                    entity.propertyValueTypes[i] = a.getString(i);
                }
                a = null;
                if(a == null) {
                    ;
                }
            }
        }
        catch(Exception e) {
            Log.e("UDP Listener: ", e.getMessage());
            DMXControlApplication.SaveLog();
        }
        o = null;
        if(o == null) {
            ;
        }
        return entity;
    }

    public void Send() {
        try {
            JSONObject o = new JSONObject();

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

    public String[] getPropertyValueTypes() {
        return propertyValueTypes;
    }

    public void setPropertyValueTypes(String[] propertyValueTypes) {
        this.propertyValueTypes = propertyValueTypes;
    }

    public static void Clear() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("Type", NetworkID);
        o.put("Clear", true);

        ServiceFrontend.get().sendMessage(o.toString().getBytes());
        o = null;
        if(o == null) {
            ;
        }
        return;
    }

    public static void Undo() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("Type", NetworkID);
        o.put("Undo", true);

        ServiceFrontend.get().sendMessage(o.toString().getBytes());
        o = null;
        if(o == null) {
            ;
        }
        return;
    }
}
