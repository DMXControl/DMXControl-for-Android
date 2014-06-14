/*
 * PositionModel.java
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

import de.dmxcontrol.widget.IValueListener;

public class PositionModel extends BaseModel implements IValueListener {
    private Float position[] = new Float[2];

    private final static float MAX_VALUE = 100f;
    private final static float MIN_VALUE = -100f;
    private final static float TRANS_VALUE = MAX_VALUE - MIN_VALUE;

    public PositionModel(ModelManager manager) {
        super(manager, ModelManager.Type.Position, "position");
        init();
    }

    private void init() {
        position[0] = 0f;
        position[1] = 0f;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {
        position[0] = (x * TRANS_VALUE - MAX_VALUE);
        position[1] = (y * TRANS_VALUE - MAX_VALUE);
        notifyListener();
    }

    public Float[] getValue() {
        return position;
    }

    public Float[] getWidgetValue() {
        return new Float[]{(position[0] + MAX_VALUE) / TRANS_VALUE,
                (position[1] + MAX_VALUE) / TRANS_VALUE};
    }

    @Override
    public Object[] getOSCAttributes() {
        return position;
    }
}
