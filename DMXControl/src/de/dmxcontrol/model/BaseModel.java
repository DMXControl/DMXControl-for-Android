/*
 * BaseModel.java
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

package de.dmxcontrol.model;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.device.EntitySelection;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.network.ServiceFrontend;

public abstract class BaseModel {
    public interface OnModelListener {
        public void onModelChanged(BaseModel model);
    }

    private ModelManager.Type mType;
    private String mOSCAttributeName;
    private Map<OnModelListener, Boolean> mListeners = new WeakHashMap<OnModelListener, Boolean>();
    private ModelManager mManager;

    public BaseModel(ModelManager manager, ModelManager.Type type, String oscAttributeName) {
        mManager = manager;
        mType = type;
        mOSCAttributeName = oscAttributeName;
    }

    public ModelManager.Type getType() {
        return mType;
    }

    public EntitySelection getEntitySelection() {
        return mManager.getEntitySelection();
    }

    public abstract void onValueChanged(View v, float x, float y);

    public abstract Object[] getOSCAttributes();

    public String getOSCAttributeName() {
        return mOSCAttributeName;
    }

    public void addDefaultListener(Map<OnModelListener, Boolean> listeners) {
        mListeners = listeners;
    }

    public void addListener(OnModelListener listener) {
        mListeners.put(listener, true);
    }

    public void removeListener(OnModelListener listener) {
        mListeners.remove(listener.hashCode());
    }

    public void notifyListener() {
        Iterator<OnModelListener> iter = mListeners.keySet().iterator();
        while(iter.hasNext()) {
            OnModelListener listener = iter.next();
            listener.onModelChanged(this);
        }
    }

    protected void SendData(String propertyType, Object valueType, Object value) throws JSONException {

        JSONArray array = new JSONArray();
        for(int i = 0; i < ReceivedData.get().Devices.size(); i++) {
            if(getEntitySelection().contains(EntityManager.Type.DEVICE, ReceivedData.get().Devices.get(i).getId())) {
                array.put(ReceivedData.get().Devices.get(i).guid);
            }
        }
        for(int i = 0; i < ReceivedData.get().Groups.size(); i++) {
            if(getEntitySelection().contains(EntityManager.Type.GROUP, ReceivedData.get().Groups.get(i).getId())) {
                array.put(ReceivedData.get().Groups.get(i).guid);
            }
        }


        JSONObject o = new JSONObject();
        o.put("Type", "PropertyValue");
        o.put("GUIDs", array);
        o.put("PropertyType", propertyType);
        o.put("ValueType", valueType);
        o.put("Value", value);

        ServiceFrontend.get().sendMessage(o.toString().getBytes());
    }
}
