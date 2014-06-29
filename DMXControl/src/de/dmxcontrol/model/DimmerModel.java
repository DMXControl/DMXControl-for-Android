/*
 * DimmerModel.java
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

import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.widget.IValueListener;

public class DimmerModel extends BaseModel implements IValueListener {
    private Integer dimmer[] = new Integer[1];

    private final static float MAX_VALUE = 100f;

    public DimmerModel(ModelManager manager) {
        super(manager, ModelManager.Type.Dimmer, "dimmer");
        init();
    }

    private void init() {
        dimmer[0] = 0;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {
        dimmer[0] = (int) (x * MAX_VALUE);
        notifyListener();
    }

    public Integer getValue() {
        return dimmer[0];
    }

    public Float getWidgetValue() {
        return (float) dimmer[0] / MAX_VALUE;
    }

    @Override
    public Object[] getOSCAttributes() {
        return dimmer;
    }
}
