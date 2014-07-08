/*
 * StrobeModel.java
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

import org.json.JSONException;

import de.dmxcontrol.widget.IValueListener;

public class StrobeModel extends BaseModel implements IValueListener {
    private Float strobe[] = new Float[1];

    private final static float MAX_VALUE = 100f;

    public StrobeModel(ModelManager manager) {
        super(manager, ModelManager.Type.Strobe, "strobe");
        init();
    }

    private void init() {
        strobe[0] = 0f;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {
        strobe[0] = x * MAX_VALUE;
        try {
            SendData("Strobe", "double", x);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        notifyListener();
    }

    public Float getValue() {
        return strobe[0];
    }

    public Float getWidgetValue() {
        return strobe[0] / MAX_VALUE;
    }

    @Override
    public Object[] getOSCAttributes() {
        return strobe;
    }
}
