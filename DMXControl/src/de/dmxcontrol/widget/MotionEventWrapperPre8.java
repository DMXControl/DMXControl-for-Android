/*
 * MotionEventWrapperPre8.java
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

public class MotionEventWrapperPre8 implements IMotionEventWrapper {
    private MotionEvent event;

    public void setEvent(MotionEvent event) {
        this.event = event;
    }

    public int getActionMaskCONST() {
        return MotionEvent.ACTION_DOWN & MotionEvent.ACTION_UP
                & MotionEvent.ACTION_MOVE & MotionEvent.ACTION_CANCEL;
    }

    public int getActionPointerUpCONST() {
        return MotionEvent.ACTION_UP;
    }

    public int getActionPointerDownCONST() {
        return MotionEvent.ACTION_DOWN;
    }

    public int getActionPointerIndexMaskCONST() {
        return 0;
    }

    public int getActionPointerIndexShiftCONST() {
        return 0;
    }

    public int getPointerIdByAction(int action) {
        return 0;
    }

    public int getPointerId(int idx) {
        return 0;
    }

    public int findPointerIndex(int id) {
        return 0;
    }

    public int getPointerCount() {
        return 1;
    }

    public float getX(int idx) {
        return event.getX();
    }

    public float getY(int idx) {
        return event.getY();
    }

    public float getX() {
        return event.getX();
    }

    public float getY() {
        return event.getY();
    }

}
