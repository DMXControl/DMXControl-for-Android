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

import java.util.HashMap;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class RelativeLayoutWithMultitouch extends RelativeLayout {
    private final static String TAG = "widget";
    private IMotionEventWrapper mMew;

    private HashMap<Integer, View> mMultitouchTargets;
    private Rect mTempRect;

    public RelativeLayoutWithMultitouch(Context context) {
        super(context);
        init();
    }

    public RelativeLayoutWithMultitouch(Context context, AttributeSet attrs,
                                        int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RelativeLayoutWithMultitouch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTempRect = new Rect();
        mMultitouchTargets = new HashMap<Integer, View>();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Log.d(TAG, "Layout:TouchEvent");

        // dumpEvent(event);
        mMew = MotionEventWrapper.get(event);

        int action = event.getAction();
        int actionMasked = action & mMew.getActionMaskCONST();
        int pid = mMew.getPointerIdByAction(action);
        Rect frame = mTempRect;
        boolean result = false;

        // Log.d(TAG, "dispatchTouchEvent: received event: pid = " + pid
        // + " count = " + mMew.getPointerCount());

        if(actionMasked == MotionEvent.ACTION_DOWN
                || actionMasked == mMew.getActionPointerDownCONST()) {
            // Log.d(TAG, "dispatchTouchEvent: ACTION_DOWN");
            int pointerIndex = mMew.findPointerIndex(pid);
            int x = (int) mMew.getX(pointerIndex);
            int y = (int) mMew.getY(pointerIndex);
            int idxChild = 0;
            int numChild = getChildCount();
            for(idxChild = 0; idxChild < numChild; idxChild++) {
                View child = getChildAt(idxChild);
                if(child.getVisibility() == View.INVISIBLE
                        && child.getAnimation() == null) {
                    // Log.d(TAG, "dispatchTouchEvent: child invisible.");
                    continue;
                }
                child.getHitRect(frame);
                if(!frame.contains(x, y)) {
                    // Log.d(TAG, "dispatchTouchEvent: child outside. ");
                    continue;
                }

                int xMoved = x - child.getLeft();
                int yMoved = y - child.getTop();

                event.setLocation(xMoved, yMoved);

                if(child.dispatchTouchEvent(event)) {
                    // Log.d(TAG, "Sending event to " + child.getId());
                    mMultitouchTargets.put(pid, child);
                    result = true;
                }
            }
        }
        else if(actionMasked == MotionEvent.ACTION_MOVE) {
            // Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE");

            for(int key : mMultitouchTargets.keySet()) {
                View child = mMultitouchTargets.get(key);
                int idx = mMew.findPointerIndex(key);
                if(idx >= mMew.getPointerCount() || idx < 0) {
                    continue;
                }
                int xMoved = (int) mMew.getX(idx) - child.getLeft();
                int yMoved = (int) mMew.getY(idx) - child.getTop();

                event.setLocation(xMoved, yMoved);

                result = mMultitouchTargets.get(key).dispatchTouchEvent(event);
            }
        }
        else if(actionMasked == MotionEvent.ACTION_UP
                || actionMasked == MotionEvent.ACTION_CANCEL
                || actionMasked == mMew.getActionPointerUpCONST()) {
            // Log.d(TAG, "dispatchTouchEvent: ACTION_UP");
            View child = mMultitouchTargets.get(pid);
            if(child != null) {

                int idx = mMew.findPointerIndex(pid);
                int xMoved = (int) mMew.getX(idx) - child.getLeft();
                int yMoved = (int) mMew.getY(idx) - child.getTop();
                event.setLocation(xMoved, yMoved);

                result = mMultitouchTargets.get(pid).dispatchTouchEvent(event);
                mMultitouchTargets.remove(pid);
            }
        }

        return result;
    }
}