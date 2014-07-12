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
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.network.ServiceFrontend;

//This is One Executor
public class EntityExecutorPage extends Entity {
    public final static String defaultExecutorPageIcon = "device_new";
    public final static String NetworkID = "ExecutorPage";

    @Override
    public String getNetworkID() {
        return NetworkID;
    }


    private ArrayList<String> ExecutorGUIDs;

    public ExecutorCollection getExecutors() {
        ExecutorCollection Executors = new ExecutorCollection();

        for(int i = 0; i < ExecutorGUIDs.size(); i++) {
            Executors.add(ReceivedData.get().Executors.get(ExecutorGUIDs.get(i)));
        }
        return Executors;
    }

    public void setExecutorGUIDs(ArrayList<String> executorGUIDs) {
        this.ExecutorGUIDs = executorGUIDs;
    }

    public ArrayList<String> getExecutorGUIDs() {
        return ExecutorGUIDs;
    }

    public EntityExecutorPage() {
    }

    public EntityExecutorPage(int id) {
        super(id, NetworkID + ": " + id, null);
        mImage = defaultExecutorPageIcon;
    }

    public EntityExecutorPage(int id, String name) {
        super(id, name, null);
        mImage = defaultExecutorPageIcon;
    }

    public EntityExecutorPage(int id, String name, String image) {
        super(id, name, null);
        mImage = image;
    }


    public static EntityExecutorPage Receive(JSONObject o) {
        EntityExecutorPage entity = null;
        try {
            if(o.getString("Type").equals(NetworkID)) {
                entity = new EntityExecutorPage(o.getInt("Number"), o.getString("Name"));
                entity.guid = o.getString("GUID");

                ArrayList<String> executors = new ArrayList<String>();

                for(int i = 0; i < o.getJSONArray("Executors").length(); i++) {
                    executors.add(o.getJSONArray("Executors").getString(i));
                }

                entity.setExecutorGUIDs(executors);
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
        SendRequest(EntityExecutorPage.class, request);
    }

    public void Send() {
        try {
            JSONObject o = new JSONObject();
            o.put("Type", NetworkID);
            o.put("GUID", this.guid);
            o.put("Name", this.getName());
            o.put("Number", this.getId());

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
