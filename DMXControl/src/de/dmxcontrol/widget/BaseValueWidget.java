/*
 * BaseValueWidget.java
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

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseValueWidget extends View {
    private final static String TAG = "widget";

    private IMotionEventWrapper mew;
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private final static String KEY_VALUE_X = "key_value_x";
    private final static String KEY_VALUE_Y = "key_value_y";

    private float mValueX;
    private float mValueY;

    private IValueListener vl;

    public BaseValueWidget(Context context) {
        super(context);
        init();
    }

    public BaseValueWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BaseValueWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }

    public void setValueListener(IValueListener listener) {
        this.vl = listener;
    }

    public void setValue(float x, float y) {
        mValueX = x;
        mValueY = y;
        invalidate();
    }

    public float[] getValue() {
        return new float[]{mValueX, mValueY};
    }

    public float getValueX() {
        return mValueX;
    }

    public float getValueY() {
        return mValueY;
    }

    public void notifyListener() {
        if (vl != null) {
            vl.onValueChanged(this, mValueX, mValueY);
        }
    }

    public abstract void pointerPosition(float xPointer, float yPointer);

    public abstract void pointerCancelled();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mew = MotionEventWrapper.get(event);

        if (mew == null) {
            Log.e(TAG, "MotionEventWrapper not available. Can't do anything.");
            return false;
        }

        final int action = event.getAction();
        final int actionMasked = action & mew.getActionMaskCONST();

        if (actionMasked == MotionEvent.ACTION_DOWN || actionMasked == mew.getActionPointerDownCONST()) {
            int pid = mew.getPointerIdByAction(action);
            int idx = mew.findPointerIndex(pid);
            final float x = mew.getX(idx);
            final float y = mew.getY(idx);
            pointerPosition(x, y);

            // Save the ID of this pointer
            mActivePointerId = pid;
            // Log.d(TAG, "MotionEvent.ACTION_DOWN: id = " + getId()
            // + " pointerId = " + mActivePointerId + " x = " + x
            // + " y = " + y);

        }
        else if (actionMasked == MotionEvent.ACTION_MOVE) {
            // Find the index of the active pointer and fetch its position
            final int idx = mew.findPointerIndex(mActivePointerId);

            if (idx < 0) {
                return false;
            }

            // Log.d(TAG, "MotionEvent.ACTION_MOVE: id = " + getId()
            // + " index = " + idx);
            final float x = mew.getX(idx);
            final float y = mew.getY(idx);

            pointerPosition(x, y);
        }
        else if (actionMasked == MotionEvent.ACTION_UP || actionMasked == mew.getActionPointerUpCONST()) {
            // Log.d(TAG, "MotionEvent.ACTION_UP: id = " + getId());
            mActivePointerId = INVALID_POINTER_ID;
            pointerCancelled();
        }
        else if (actionMasked == MotionEvent.ACTION_CANCEL) {
            mActivePointerId = INVALID_POINTER_ID;
            pointerCancelled();
        }

        return true;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("superState"));
        setValue(bundle.getFloat(KEY_VALUE_X), bundle.getFloat(KEY_VALUE_Y));

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", superState);
        bundle.putFloat(KEY_VALUE_X, getValueX());
        bundle.putFloat(KEY_VALUE_Y, getValueY());

        return bundle;
    }
}