/*
 * RelativeLayoutWithMultitouch.java
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;

import de.dmxcontrol.executor.ExecutorPageView;

public class ExecuterPageMultitouchLayout extends LinearLayout {
    private final static String TAG = "widget";
    private IMotionEventWrapper mMew;

    private HashMap<Integer, View> mMultitouchTargets;
    private Rect mTempRect;
    public ExecutorPageView executorPage;

    public ExecuterPageMultitouchLayout(Context context) {
        super(context);
        init();
    }

    public ExecuterPageMultitouchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTempRect = new Rect();
        mMultitouchTargets = new HashMap<Integer, View>();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            default:
                mMew = MotionEventWrapper.get(event);
                int action = event.getAction();
                int actionMasked = action & mMew.getActionMaskCONST();
                int pid = mMew.getPointerIdByAction(action);
                    float x = event.getX(pid)-executorPage.getExecuterPageSliderView().getScrollX();
                    Log.i(executorPage.getExecuterPageSliderView().getScrollX()+"","");
                    float y = event.getY(pid);
                    if (executorPage != null) {
                        for (ExecutorView ev : executorPage.getExecutors())
                            if (isPointInsideView(x, y, ev)) {
                                switch (actionMasked) {
                                    case MotionEvent.ACTION_DOWN:
                                    case MotionEvent.ACTION_POINTER_DOWN:
                                    case MotionEvent.ACTION_CANCEL:
                                        Log.i(ev.getTag().toString(), "Pointer " + "ID " + pid + "   X: " + x + "   Y: " + y + "    Action: " + "DOWN");
                                        return ev.TouchEvent(MotionEvent.obtain(
                                                event.getDownTime(),
                                                event.getEventTime(),
                                                MotionEvent.ACTION_DOWN,
                                                x,
                                                y,
                                                event.getPressure(pid),
                                                event.getSize(pid),
                                                event.getMetaState(),
                                                event.getXPrecision(),
                                                event.getYPrecision(),
                                                event.getDeviceId(),
                                                event.getEdgeFlags()));
                                    case MotionEvent.ACTION_UP:
                                    case MotionEvent.ACTION_POINTER_UP:
                                        Log.i(ev.getTag().toString(), "Pointer " + "ID " + pid + "   X: " + x + "   Y: " + y + "    Action: " + "UP");
                                        return ev.TouchEvent(MotionEvent.obtain(
                                                event.getDownTime(),
                                                event.getEventTime(),
                                                MotionEvent.ACTION_UP,
                                                x,
                                                y,
                                                event.getPressure(pid),
                                                event.getSize(pid),
                                                event.getMetaState(),
                                                event.getXPrecision(),
                                                event.getYPrecision(),
                                                event.getDeviceId(),
                                                event.getEdgeFlags()));
                                    case MotionEvent.ACTION_MOVE:
                                        Log.i(ev.getTag().toString(), "Pointer " + "ID " + pid + "   X: " + x + "   Y: " + y + "    Action: " + "MOVING");
                                        return ev.TouchEvent(MotionEvent.obtain(
                                                event.getDownTime(),
                                                event.getEventTime(),
                                                MotionEvent.ACTION_MOVE,
                                                x,
                                                y,
                                                event.getPressure(pid),
                                                event.getSize(pid),
                                                event.getMetaState(),
                                                event.getXPrecision(),
                                                event.getYPrecision(),
                                                event.getDeviceId(),
                                                event.getEdgeFlags()));
                                }
                                Log.i(ev.getTag().toString(), "Pointer " + "ID " + pid + "   X: " + x + "   Y: " + y + "    Action: " + actionMasked);

                            }
                    }
        }
        return false;
    }

    private boolean isPointInsideView(float x, float y, View view){
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        if(( x > viewX && x < (viewX + view.getWidth())) &&
                ( y > viewY && y < (viewY + view.getHeight()))){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result=super.dispatchTouchEvent(event);
        return result;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        event.getAction();
        return super.dispatchKeyEvent(event);
    }
}