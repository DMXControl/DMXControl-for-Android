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


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.lang.String;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.EntityManager.Type;

public abstract class Entity implements IPropertyContainer {
    HashMap<String, Object[]> properties;
    private final static String StoragePath = Environment.getExternalStorageDirectory() + File.separator + "DMXControl";
    private final static String IconStorageName = StoragePath + File.separator + "Icons";
    private int mId;
    private Type mType;
    private String mName;
    protected int mImage;
    protected String lImage;
    public String guid;

    private ArrayList<NameChangedListener> NameChangedListeners = new ArrayList<NameChangedListener>();

    public void setNameChangedListener(NameChangedListener listener) {
        this.NameChangedListeners.add(listener);
    }

    public interface NameChangedListener {
        void onNameChanged(String name);
    }

    public Entity(int id, String name, Type type) {
        mId = id;
        mName = name;
        properties = new HashMap<String, Object[]>();
        mType = type;
        mImage = R.drawable.icon;
        lImage = "";
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
    public void setName(String name) {
        boolean isEqual= mName.equals(name);
        mName=name;
        if(!isEqual) {
            for (NameChangedListener listener : NameChangedListeners) {
                listener.onNameChanged(name);
            }
            return;
        }
    }

    public int getImage() {
        return mImage;
    }
    public void setImage(String image) {
        lImage=image;
    }

    public String getBitmapFileName() {
        return lImage;
    }

    public Bitmap getBitmap() {
        File imgFile = new File(IconStorageName + File.separator + this.getBitmapFileName());
        if(imgFile.isFile()) {
            if (imgFile.exists()) {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        }
        return null;
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

    public static Entity Receive(byte[] message) {
        return receive(message);
    }
    protected static Entity receive(byte[] message){
        return null;
    }

}
