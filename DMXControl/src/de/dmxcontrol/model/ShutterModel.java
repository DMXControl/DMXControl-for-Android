/*
 * ShutterModel.java
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

public class ShutterModel extends BaseModel {
    public final static int SHUTTER_OPEN = 1;
    public final static int SHUTTER_CLOSE = 0;

    private Integer[] shutter = new Integer[1];

    public ShutterModel(ModelManager manager) {
        super(manager, ModelManager.Type.Shutter, "shutter");
        init();
    }

    private void init() {
        shutter[0] = SHUTTER_CLOSE;
    }

    public void setValue(int value) {
        shutter[0] = value;
        notifyListener();
    }

    public int getValue() {
        return shutter[0];
    }

    @Override
    public Object[] getOSCAttributes() {
        return shutter;
    }

}