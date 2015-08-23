/*
 * OutputListener.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2011 DMXControl-For-Android. All rights reserved.
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

package de.dmxcontrol.widget;

import android.util.Log;
import android.view.View;

import java.util.Date;

public class OutputListener implements IValueListener {
    private final static String TAG = "widget";
    private long millisecondsTime = 0l;
    private int lastSecond, second;
    private int eventCount = 0;

    public void onValueChanged(View v, float x, float y) {
        Date date = new Date();
        lastSecond = second;
        second = date.getSeconds();
        eventCount++;
        millisecondsTime = date.getTime() % 1000;
        Log.d(TAG, "Time in s: " + second + ":" + millisecondsTime + " Value: " + x);

        if(second - lastSecond == 1) {
            Log.d(TAG, "Eventcount = " + eventCount);
            eventCount = 0;
        }
    }
}
