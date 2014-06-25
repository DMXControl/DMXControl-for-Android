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

import android.content.res.Resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.EntityManager.Type;

public abstract class Entity implements IPropertyContainer {
    private final static String StoragePath = Environment.getExternalStorageDirectory() + File.separator + "DMXControl";
    private final static String IconStorageName = StoragePath + File.separator + "Icons";

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
    public abstract String getNetworkID();

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
        mId=id;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name, boolean fromReader) {
        boolean isEqual= mName.equals(name);
        mName=name;
        
        if(!isEqual&&fromReader) {
            for (NameChangedListener listener : NameChangedListeners) {
                listener.onNameChanged(name);
            }
            return;
        }
        if(!isEqual&&!fromReader) {
            Send();
        }
    }

    public Bitmap getImage() {

        File imgFile = new File(IconStorageName + File.separator + mImage);

        if(imgFile.isFile())
        {
            if (imgFile.exists())
            {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        }

        // Replace this icon with something else
        return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.icon);
    }
    public String getImageName(){
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
    
    public abstract void Send();
}
