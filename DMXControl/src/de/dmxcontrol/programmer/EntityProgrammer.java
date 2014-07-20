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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.network.ServiceFrontend;

public class EntityProgrammer extends Entity {
    public final static String NetworkID = "Programmer";
    private ArrayList<State> states = new ArrayList<State>();
    private ArrayList<ChangedListener> ChangedListeners = new ArrayList<ChangedListener>();

    public void setChangedListener(ChangedListener listener) {
        this.ChangedListeners.add(listener);
    }

    public void removeChangedListeners() {
        this.ChangedListeners.clear();
    }

    public interface ChangedListener {
        void onChanged();
    }

    private void runChangeListener() {
        for(ChangedListener listener : ChangedListeners) {
            listener.onChanged();
        }
    }

    @Override
    public String getNetworkID() {
        return NetworkID;
    }

    public int getGroupCount() {
        return this.getStatesSize();
    }

    public int getDeviceCount(int group) {
        return 0;
    }

    public Object getGroup(int group) {
        return states.get(group);
    }

    public Object getDevice(int group, int device) {
        return null;
    }

    public static void SendRequest(String request) {
        SendRequest(EntityProgrammer.class, request);
    }

    public EntityProgrammer() {
    }

    public static EntityProgrammer Receive(JSONObject o) {
        EntityProgrammer entity = new EntityProgrammer();
        try {
            if(o.getString("Type").equals(NetworkID)) {
                int number = o.getInt("Number");
                String name = o.getString("Name");
                entity.setId(number);
                entity.setName(name.replace(NetworkID + ": ", ""), true);
                entity.guid = o.getString("GUID");
            }
        }
        catch(Exception e) {
            Log.e("UDP Listener: ", e.getMessage());
            DMXControlApplication.SaveLog();
        }
        o = null;
        if(o == null) {
            entity.runChangeListener();
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

    public Object[] getStates() {
        return states.toArray();
    }

    public int getStatesSize() {
        return states.size();
    }

    public static void Clear(EntityProgrammer programmer) throws JSONException {
        if(programmer == null) {
            return;
        }
        JSONObject o = new JSONObject();
        o.put("Type", NetworkID);
        o.put("GUID", programmer.guid);
        o.put("Clear", true);

        ServiceFrontend.get().sendMessage(o.toString().getBytes());
        o = null;
        if(o == null) {
            programmer.states.clear();
            programmer.runChangeListener();
        }
        return;
    }

    public static void Undo(EntityProgrammer programmer) throws JSONException {
        if(programmer == null) {
            return;
        }
        JSONObject o = new JSONObject();
        o.put("Type", NetworkID);
        o.put("GUID", programmer.guid);
        o.put("Undo", true);

        ServiceFrontend.get().sendMessage(o.toString().getBytes());
        o = null;
        if(o == null) {
            ;
        }
        return;
    }

    public static void ClearSelection(EntityProgrammer programmer) throws JSONException {
        if(programmer == null) {
            return;
        }
        JSONObject o = new JSONObject();
        o.put("Type", NetworkID);
        o.put("GUID", programmer.guid);
        o.put("ClearSelection", true);

        ServiceFrontend.get().sendMessage(o.toString().getBytes());
        o = null;
        if(o == null) {
            ;
        }
        return;
    }

    public void LoadStates(JSONObject o) throws JSONException {
        if(states == null) {
            states = new ArrayList<State>();
        }
        for(State state : states) {
            if(state.Compare(o)) {
                this.runChangeListener();
                return;
            }
        }
        states.add(new State(o));
        o = null;
        if(o == null) {
            this.runChangeListener();
        }
    }

    public class State {
        private int id;
        private String name, value, valueName, valueSource;

        public State() {
        }

        public State(JSONObject o) throws JSONException {
            this.id = o.getInt("Number");
            this.name = o.getString("Name");
            this.valueName = o.getString("ValueName");
            this.valueSource = o.getString("ValueSource");
            this.Update(o);

            o = null;
            if(o == null) {
                ;
            }
        }

        public void Update(JSONObject o) throws JSONException {
            this.value = o.getString("Value");

            o = null;
            if(o == null) {
                ;
            }
        }

        public boolean Compare(JSONObject o) throws JSONException {
            if(this.id != o.getInt("Number")) {
                return false;
            }

            this.name = o.getString("Name");
            this.valueName = o.getString("ValueName");
            this.valueSource = o.getString("ValueSource");

            this.Update(o);

            o = null;
            if(o == null) {
                ;
            }

            return true;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getValueName() {
            return valueName;
        }

        public String getValueSource() {
            return valueSource;
        }
    }
}
