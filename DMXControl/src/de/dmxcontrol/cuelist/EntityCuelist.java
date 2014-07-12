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

package de.dmxcontrol.cuelist;

import android.util.Log;

import org.json.JSONObject;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.network.ServiceFrontend;

//This is One Executor
public class EntityCuelist extends Entity {
    public final static String defaultCuelistIcon = "device_new";
    public final static String NetworkID = "Cuelist";

    @Override
    public String getNetworkID() {
        return NetworkID;
    }

    public EntityCuelist() {
    }

    public EntityCuelist(int id) {
        super(id, NetworkID + ": " + id, null);
        mImage = defaultCuelistIcon;
    }

    public EntityCuelist(int id, String name) {
        super(id, name, null);
        mImage = defaultCuelistIcon;
    }

    public EntityCuelist(int id, String name, String image) {
        super(id, name, null);
        mImage = image;
    }


    public static EntityCuelist Receive(JSONObject o) {
        EntityCuelist entity = null;
        try {
            if(o.getString("Type").equals(NetworkID)) {
                entity = new EntityCuelist(0, o.getString("Name"));
                entity.guid = o.getString("GUID");
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

    public static void SendRequest(String request) {
        SendRequest(EntityCuelist.class, request);
    }

    public void Send() {
        try {
            JSONObject o = new JSONObject();
            o.put("Type", NetworkID);
            o.put("GUID", this.guid);
            o.put("Name", this.getName());

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

}
