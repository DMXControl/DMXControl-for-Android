/*
 * ColorModel.java
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

import android.graphics.Color;
import android.view.View;

import org.json.JSONException;
import org.openintents.widget.OnColorChangedListener;

public class ColorModel extends BaseModel implements OnColorChangedListener {
    private Integer[] colors = new Integer[3];

    public ColorModel(ModelManager manager) {
        super(manager, ModelManager.Type.Color, "color");
        init();
    }

    private void init() {
        colors[0] = 0;
        colors[1] = 0;
        colors[2] = 0;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {

    }

    public Object[] getOSCAttributes() {
        return colors;
    }

    @Override
    public void onColorChanged(View view, int newColor) {
        setColors(newColor);
        try {
            SendData("Color", "Int", newColor);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        notifyListener();
    }

    @Override
    public void onColorPicked(View view, int newColor) {
        setColors(newColor);
        // mDeviceManager.processAttributes(CommunicationService.ATTRIBUTE_COLOR,
        // colors);
    }

    public void setColors(int color) {
        colors[0] = Color.red(color);
        colors[1] = Color.green(color);
        colors[2] = Color.blue(color);
    }

    public int getValue() {
        return Color.argb(255, colors[0], colors[1], colors[2]);
    }
}
