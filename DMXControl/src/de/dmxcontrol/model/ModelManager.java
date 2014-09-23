/*
 * ModelManager.java
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

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import de.dmxcontrol.device.EntitySelection;
import de.dmxcontrol.model.BaseModel.OnModelListener;

public class ModelManager {
    private final static String TAG = "modelmanager";

    public enum Type {
        Color,
        Gobo,
        Position,
        Dimmer,
        Strobe,
        Shutter,
        Zoom,
        Focus,
        Iris,
        Frost,
        Effect
    }

    private Map<Type, BaseModel> mModels = new HashMap<Type, BaseModel>();
    private static Map<Type, Class<? extends BaseModel>> mTypeLookup = new HashMap<Type, Class<? extends BaseModel>>();
    private Map<OnModelListener, Boolean> mDefaultModelListeners = new HashMap<OnModelListener, Boolean>();
    private EntitySelection mEntitySelection;

    public ModelManager(EntitySelection es) {

        addDefaultModelListener(es);
        mEntitySelection = es;
    }

    public EntitySelection getEntitySelection() {
        return mEntitySelection;
    }

    public void addDefaultModelListener(OnModelListener listener) {
        mDefaultModelListeners.put(listener, true);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseModel> T getModel(Type type) {

        if(mModels.containsKey(type)) {
            return (T) mModels.get(type);
        }
        else {
            T model;
            model = create(type);
            mModels.put(type, (BaseModel) model);
            return model;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseModel> T create(Type type) {
        Class<? extends BaseModel> clazz = mTypeLookup.get(type);
        T model;
        try {
            model = (T) clazz.getDeclaredConstructors()[0].newInstance(this);
        }
        catch(Exception e) {
            Log.e(TAG, "error creation of model with class " + clazz.getName());
            Log.e(TAG, "exception: ", e);
            return null;
        }

        model.addDefaultListener(mDefaultModelListeners);
        return model;
    }

    static {
        mTypeLookup.put(Type.Color, ColorModel.class);
        mTypeLookup.put(Type.Gobo, GoboModel.class);
        mTypeLookup.put(Type.Position, PositionModel.class);
        mTypeLookup.put(Type.Dimmer, DimmerModel.class);
        mTypeLookup.put(Type.Strobe, StrobeModel.class);
        mTypeLookup.put(Type.Shutter, ShutterModel.class);
        mTypeLookup.put(Type.Zoom, ZoomModel.class);
        mTypeLookup.put(Type.Focus, FocusModel.class);
        mTypeLookup.put(Type.Iris, IrisModel.class);
        mTypeLookup.put(Type.Frost, FrostModel.class);
        mTypeLookup.put(Type.Effect, EffectModel.class);
    }

}