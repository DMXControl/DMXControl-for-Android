/*
 * MotionEventWrapper8.java
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

public class MotionEventWrapper8 implements IMotionEventWrapper {
    private MotionEvent event;

    public void setEvent(MotionEvent event) {
        this.event = event;
    }

    public int getActionMaskCONST() {
        return MotionEvent.ACTION_MASK;
    }

    public int getActionPointerUpCONST() {
        return MotionEvent.ACTION_POINTER_UP;
    }

    public int getActionPointerDownCONST() {
        return MotionEvent.ACTION_POINTER_DOWN;
    }

    public int getActionPointerIndexMaskCONST() {
        return MotionEvent.ACTION_POINTER_INDEX_MASK;
    }

    public int getActionPointerIndexShiftCONST() {
        return MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    public int getPointerIdByAction(int action) {
        return action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
    }

    public int getPointerId(int idx) {
        return event.getPointerId(idx);
    }

    public int findPointerIndex(int id) {
        return event.findPointerIndex(id);
    }

    public int getPointerCount() {
        return event.getPointerCount();
    }

    public float getX(int idx) {
        return event.getX(idx);
    }

    public float getY(int idx) {
        return event.getY(idx);
    }

    public float getX() {
        return event.getX();
    }

    public float getY() {
        return event.getY();
    }
}
