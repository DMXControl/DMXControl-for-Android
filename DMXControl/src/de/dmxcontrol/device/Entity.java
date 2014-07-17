/*
 * Entity.java
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
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.network.ServiceFrontend;

public abstract class Entity implements IPropertyContainer {
    private final static String StoragePath = Environment.getExternalStorageDirectory() + File.separator + "DMXControl";
    private final static String IconStorageName = StoragePath + File.separator + "Icons";
    public static String NetworkID = new String();
    public static String Request_All = "ALL";
    public static String Request_All_GUIDs = "GUIDList";
    public static String Request_GUID = "GUID";

    // Replace this icon with something else
    private final static String defaultIcon = "icon";

    private int mId;
    private Type mType;
    private String mName;
    protected String mImage;
    public String guid; // maybe refactor ro mguid
    HashMap<String, Object[]> properties;

    private ArrayList<NameChangedListener> NameChangedListeners = new ArrayList<NameChangedListener>();

    public void setNameChangedListener(NameChangedListener listener) {
        this.NameChangedListeners.add(listener);
    }

    public void removeNameChangedListeners() {
        this.NameChangedListeners.clear();
    }

    public interface NameChangedListener {
        void onNameChanged(String name);
    }

    protected Entity() {
    }

    public Entity(int id, String name, Type type) {
        mId = id;
        mType = type;
        mName = name;
        mImage = defaultIcon;
        properties = new HashMap<String, Object[]>();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name, boolean fromReader) {
        boolean isEqual = (mName + "").equals(name + "");
        mName = name;

        if(!isEqual && fromReader) {
            for(NameChangedListener listener : NameChangedListeners) {
                listener.onNameChanged(name);
            }
            return;
        }
        if(!isEqual && !fromReader) {
            for(NameChangedListener listener : NameChangedListeners) {
                listener.onNameChanged(name);
            }
            Send();
        }
    }

    public Bitmap getImage(Context context) {
        try {
            File imgFile = new File(IconStorageName + File.separator + mImage);
            if(imgFile.isFile()) {
                if(imgFile.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if(bmp.getHeight() > 128 || bmp.getWidth() > 128) {
                        bmp = Bitmap.createScaledBitmap(bmp, 128, 128, false);
                    }
                    return bmp;
                }
            }
        }
        catch(Exception e) {
        }
        // Replace this icon with something else
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.device_new);
    }

    public String getImageName() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public Type getType() {
        return mType;
    }


    @Override
    public void setProperty(String name, Object[] values) {
        properties.put(name, values);
    }

    @Override
    public Object[] getProperty(String name) {
        return properties.get(name);
    }


    public abstract String getNetworkID();

    public abstract void Send();

    public static void SendRequest(Class entity, String request) {
        try {
            String type = ((Entity) entity.newInstance()).getNetworkID();
            if(request.equals(Request_All_GUIDs)) {
                type += Request_All_GUIDs;
                request = Request_All;
            }
            JSONObject o = new JSONObject();
            o.put("Type", type);
            o.put("Request", request);

            ServiceFrontend.get().sendMessage(o.toString().getBytes());
            type = null;
            o = null;
            request = null;
            if(type == null && o == null && request == null) {
                ;
            }
        }
        catch(Exception e) {
            Log.e("SendAllRequest: ", e.getMessage());
            DMXControlApplication.SaveLog();
        }
    }
}
