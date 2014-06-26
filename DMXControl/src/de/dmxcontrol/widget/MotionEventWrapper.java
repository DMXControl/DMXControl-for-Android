/*
 * MotionEventWrapper.java
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

import android.view.MotionEvent;

public class MotionEventWrapper {
    private final static String TAG = "widget";

    private static IMotionEventWrapper wrappedInstance = null;

    public static IMotionEventWrapper get(MotionEvent event) {
        if(wrappedInstance != null) {
            wrappedInstance.setEvent(event);
            return wrappedInstance;
        }

        Class clazz;

        try {
            clazz = Class.forName(MotionEventWrapper.class.getPackage()
                    .getName() + ".MotionEventWrapper8");
        }
        catch(Exception ex) {
            // Log.d( TAG, "MotionEventWrapper exception: " +
            // ex.getClass().getName() + " " + ex.getMessage());
            try {
                clazz = Class.forName(MotionEventWrapper.class.getPackage()
                        .getName() + ".MotionEventWrapperPre8");
            }
            catch(ClassNotFoundException e) {
                return null;
            }
        }

        try {
            wrappedInstance = (IMotionEventWrapper) clazz.newInstance();
            wrappedInstance.setEvent(event);
        }
        catch(Exception e) {
            return null;
        }

        return wrappedInstance;
    }

}
