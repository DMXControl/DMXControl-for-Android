/*
 * IMotionEventWrapper.java
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

public interface IMotionEventWrapper {

    public void setEvent(MotionEvent event);

    public int getActionMaskCONST();

    public int getActionPointerUpCONST();

    public int getActionPointerDownCONST();

    public int getActionPointerIndexMaskCONST();

    public int getActionPointerIndexShiftCONST();

    public int getPointerIdByAction(int action);

    public int getPointerId(int idx);

    public int findPointerIndex(int id);

    public int getPointerCount();

    public float getX(int idx);

    public float getY(int idx);

    public float getX();

    public float getY();
}
