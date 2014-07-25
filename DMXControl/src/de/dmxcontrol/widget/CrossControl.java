/*
 * CrossControl.java
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import de.dmxcontrol.android.R;
import de.dmxcontrol.compatibility.CompatibilityWrapper8;

public class CrossControl extends BaseValueWidget {
    private final static String TAG = "widget";

    private final static int MARKER_SIZE_X = 11;
    private final static int MARKER_SIZE_Y = 11;

    private final static float FOLLOW_MARKER_SIZE_X = 13;
    private final static float FOLLOW_MARKER_SIZE_Y = 13;

    private Paint mPaintBorder;
    private Paint mPaintInnerCross;

    private Paint mPaintCross;
    private Paint mPaintMarker;

    private Paint mPaintFollowMarker;
    private Paint mPaintFollowLine;

    private boolean mEnableLockXDirection;
    private boolean mEnableLockYDirection;

    private float mXFollowValue;
    private float mYFollowValue;
    private int mSpeed = 50;

    private final static int POINTER_FOLLOW_INTERVAL = 15;
    private final static float POINTER_FOLLOW_SPEED_FACTOR = 6f;
    private final static float POINTER_SENSOR_SPEED_FACTOR = 0.000060f;

    private PointerSensorDriver mSensorDriver;

    private final static String KEY_MODE = "key_mode";
    private final static String KEY_VALUE_FOLLOW_X = "key_value_follow_x";
    private final static String KEY_VALUE_FOLLOW_Y = "key_value_follow_y";
    private final static String KEY_VALUE_SPEED = "key_value_speed";
    private final static String KEY_LOCK_X = "key_lock_x";
    private final static String KEY_LOCK_Y = "key_lock_y";

    public final static int MODE_POINTER_PLAIN = 0;
    public final static int MODE_POINTER_FOLLOW = 1;
    public final static int MODE_POINTER_SENSOR = 2;

    private int mMode;

    private CompatibilityWrapper8 compat8;

    public CrossControl(Context context) {
        super(context);
        init();
    }

    public CrossControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CrossControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        compat8 = CompatibilityWrapper8.wrap(getContext());
        int highlightColor = getResources().getColor(R.color.btn_background_highlight);

        int red = Color.red(highlightColor);
        int green = Color.green(highlightColor);
        int blue = Color.blue(highlightColor);

        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder.setARGB(255, red, green, blue);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setStrokeWidth(5);

        mPaintCross = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCross.setARGB(180, red, green, blue);
        mPaintCross.setStyle(Paint.Style.STROKE);
        mPaintCross.setStrokeWidth(dpToPx(1));

        mPaintInnerCross = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInnerCross.setARGB(255, red, green, blue);
        mPaintInnerCross.setStyle(Paint.Style.STROKE);
        mPaintInnerCross.setStrokeWidth(dpToPx(1));

        mPaintMarker = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMarker.setARGB(255, red, green, blue);
        // mPaintMarker.setARGB(180, 220, 20, 60);
        mPaintMarker.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintMarker.setStrokeWidth(dpToPx(1));

        mPaintFollowMarker = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFollowMarker.setARGB(255, 160, 160, 160);
        mPaintFollowMarker.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaintFollowLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFollowLine.setARGB(255, 160, 160, 160);
        mPaintFollowLine.setStyle(Paint.Style.STROKE);
        mPaintFollowLine.setStrokeWidth(dpToPx(1));

        mXFollowValue = getValueX();
        mYFollowValue = getValueY();

        setMode(MODE_POINTER_PLAIN);
    }

    protected void finalize() {
        stopAllThings();
    }

    public void stopAllThings() {
        if(mSensorDriver != null) {
            mSensorDriver.unregister();
            mSensorDriver = null;
        }
    }

    public void setMode(int mode) {
        stopAllThings();

        switch(mode) {
            case MODE_POINTER_PLAIN:
                break;
            case MODE_POINTER_FOLLOW:
                mXFollowValue = getValueX();
                mYFollowValue = getValueY();
                break;
            case MODE_POINTER_SENSOR:
                mSensorDriver = new PointerSensorDriver();
                mSensorDriver.register();
                break;
            default:
                ;
        }
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void reset() {
        setValue(0.5f, 0.5f);
        mXFollowValue = 0.5f;
        mYFollowValue = 0.5f;
        notifyListener();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float percentXValue = getValueX();
        float percentYValue = 1 - getValueY();

        int xPosition = (int) (percentXValue * getWidth());
        int yPosition = (int) (percentYValue * getHeight());

        drawBorder(canvas);

        if(mMode == MODE_POINTER_FOLLOW) {
            drawFollowMarks(canvas, xPosition, yPosition);
        }

        drawMarker(canvas, xPosition, yPosition);
        super.onDraw(canvas);

        if(mMode == MODE_POINTER_FOLLOW) {
            driveFollowMode();
        }

        super.onDraw(canvas);
    }

    private void drawBorder(Canvas canvas) {

        RectF rectBorder = new RectF(0, 0, getWidth(), getHeight());

        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaintInnerCross);
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mPaintInnerCross);
    }

    private void drawFollowMarks(Canvas canvas, int xPosition, int yPosition) {

        float xDrivenPosition = (mXFollowValue * getWidth());
        float yDrivenPosition = ((1 - mYFollowValue) * getHeight());

        int fractions = 15;
        int numPoints = 4;
        float[] points = new float[fractions * numPoints];

        float deltaX = (xDrivenPosition - xPosition) / fractions;
        float deltaY = (yDrivenPosition - yPosition) / fractions;

        float distX = deltaX * 0.2f;
        float distY = deltaY * 0.2f;

        int idx;
        for(idx = 0; idx < fractions; idx++) {
            points[numPoints * idx] = xPosition + (idx * deltaX) + distX;
            points[numPoints * idx + 1] = yPosition + (idx * deltaY) + distY;
            points[numPoints * idx + 2] = xPosition + ((idx + 1) * deltaX)
                    - distX;
            points[numPoints * idx + 3] = yPosition + ((idx + 1) * deltaY)
                    - distY;
        }

        canvas.drawLines(points, mPaintFollowLine);

        RectF rectDrivenMarked = new RectF(xDrivenPosition
                - dpToPx(FOLLOW_MARKER_SIZE_X) / 2, yDrivenPosition
                - dpToPx(FOLLOW_MARKER_SIZE_Y) / 2, xDrivenPosition
                + dpToPx(FOLLOW_MARKER_SIZE_X) / 2, yDrivenPosition
                + dpToPx(FOLLOW_MARKER_SIZE_Y) / 2);

        canvas.drawRoundRect(rectDrivenMarked, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_X), mPaintFollowMarker);
    }

    private void drawMarker(Canvas canvas, int xPosition, int yPosition) {

        canvas.drawLine(0, yPosition, getWidth(), yPosition, mPaintCross);
        canvas.drawLine(xPosition, 0, xPosition, getHeight(), mPaintCross);

        RectF rectMarked = new RectF(xPosition - dpToPx(MARKER_SIZE_X) / 2, yPosition
                - dpToPx(MARKER_SIZE_Y) / 2, xPosition + dpToPx(MARKER_SIZE_X) / 2, yPosition
                + dpToPx(MARKER_SIZE_Y) / 2);

        canvas.drawRoundRect(rectMarked, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_X), mPaintMarker);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("superState"));
        mXFollowValue = bundle.getFloat(KEY_VALUE_FOLLOW_X);
        mYFollowValue = bundle.getFloat(KEY_VALUE_FOLLOW_Y);
        mEnableLockXDirection = bundle.getBoolean(KEY_LOCK_X);
        mEnableLockYDirection = bundle.getBoolean(KEY_LOCK_Y);
        setSpeed(bundle.getInt(KEY_VALUE_SPEED));
        setMode(bundle.getInt(KEY_MODE));
        Log.d(TAG, "mSpeed = " + mSpeed + " mMode = " + mMode);
        invalidate();

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        stopAllThings();

        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", superState);
        bundle.putInt(KEY_MODE, mMode);
        bundle.putFloat(KEY_VALUE_FOLLOW_X, mXFollowValue);
        bundle.putFloat(KEY_VALUE_FOLLOW_Y, mXFollowValue);
        bundle.putBoolean(KEY_LOCK_X, mEnableLockXDirection);
        bundle.putBoolean(KEY_LOCK_Y, mEnableLockYDirection);
        bundle.putInt(KEY_VALUE_SPEED, mSpeed);

        return bundle;
    }

    @Override
    public void pointerPosition(float x, float y, boolean isMoving) {
        if(isMoving) {
            final float percentXValue = x / getWidth();
            final float percentYValue = 1 - (y / getHeight());

            if(percentXValue < 0 || percentYValue > 1) {
                return;
            }

            if(percentYValue < 0 || percentYValue > 1) {
                return;
            }

            if(mMode == MODE_POINTER_PLAIN) {
                pointerPlainPosition(percentXValue, percentYValue);
            }
            else if(mMode == MODE_POINTER_FOLLOW) {
                pointerFollowPosition(percentXValue, percentYValue);
            }
            else {
                pointerSensorPosition(percentXValue, percentYValue);
            }
        }
    }

    private boolean mFingerIsDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            mFingerIsDown = true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            mFingerIsDown = false;
        }
        return super.onTouchEvent(event);
    }

    private void pointerPlainPosition(float x, float y) {

        if(!mEnableLockXDirection) {
            setValue(x, getValueY());
        }

        if(!mEnableLockYDirection) {
            setValue(getValueX(), y);
        }

        notifyListener();
        // Log.d(TAG, "pointerPlainPosition: x = " + x + " y = " + y);
    }

    private void pointerFollowPosition(float x, float y) {

        if(!mEnableLockXDirection) {
            mXFollowValue = x;
        }

        if(!mEnableLockYDirection) {
            mYFollowValue = y;
        }

        invalidate();
    }

    private void pointerSensorPosition(float x, float y) {
        if((x >= 0 && x <= 1) && !mEnableLockXDirection) {
            setValue(x, getValueY());
        }

        if((y >= 0 && y <= 1) && !mEnableLockYDirection) {
            setValue(getValueX(), y);
        }

        Log.d(TAG, "pointerPlainPosition: x = " + x + " y = " + y);
        notifyListener();
    }

    @Override
    public void pointerCancelled() {
        mXFollowValue = getValueX();
        mYFollowValue = getValueY();
        invalidate();
    }

    public void enableLockXDirection(boolean enableLock) {
        mEnableLockXDirection = enableLock;
    }

    public boolean getLockXDirection() {
        return mEnableLockXDirection;
    }

    public void enableLockYDirection(boolean enableLock) {
        mEnableLockYDirection = enableLock;
    }

    public boolean getLockYDirection() {
        return mEnableLockYDirection;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public int getSpeed() {
        return mSpeed;
    }

    private long lastTime;

    private void driveFollowMode() {
        long now = System.currentTimeMillis();

        if(now - lastTime >= POINTER_FOLLOW_INTERVAL) {
            computeFollowDriver();
            lastTime = now;
        }
    }

    private void computeFollowDriver() {
        float deltaX = -getValueX() + mXFollowValue;
        float deltaY = -getValueY() + mYFollowValue;

        final float MIN_DELTA = 0.0005f;
        boolean noXDelta = (Math.abs(deltaX) <= MIN_DELTA);
        boolean noYDelta = (Math.abs(deltaY) <= MIN_DELTA);

        if(noXDelta && noYDelta) {
            return;
        }

        // Log.d(TAG, "deltaX = " + deltaX + " deltaY = "
        // + deltaY);

        float normDiv = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if(Float.isInfinite(normDiv)) {
            normDiv = (float) Math.sqrt(2d);
        }
        else if(Float.isNaN(normDiv) || normDiv <= 0.01f) {
            normDiv = 0.01f;
        }
        // Log.d(TAG, "normDiv = " + normDiv);

        float deltaXNorm = deltaX / (normDiv * (100 - mSpeed) * POINTER_FOLLOW_SPEED_FACTOR);
        float deltaYNorm = deltaY / (normDiv * (100 - mSpeed) * POINTER_FOLLOW_SPEED_FACTOR);
        // Log.d(TAG, "deltaXNorm = " + deltaXNorm
        // + " deltaYNorm = " + deltaYNorm);

        final float newX = getValueX() + deltaXNorm;
        final float newY = getValueY() + deltaYNorm;
        // Log.d(TAG, "deltaX = " + (deltaXNorm)
        // + " deltaY = " + (deltaYNorm));
        //
        // Log.d(TAG, "newX = " + newX + " newY = " + newY);

        CrossControl.this.post(new Runnable() {
            public void run() {
                pointerPlainPosition(newX, newY);
            }
        });

    }

    private class PointerSensorDriver implements SensorEventListener {
        DigitalFilter[] filter = {new DigitalFilter(), new DigitalFilter(),
                new DigitalFilter(), new DigitalFilter(), new DigitalFilter(),
                new DigitalFilter()};

        final int matrixSize = 16;
        float[] R = new float[matrixSize];
        float[] outR = new float[matrixSize];
        float[] I = new float[matrixSize];
        float[] values = new float[3];
        boolean isReady = false;

        private float[] mags;
        private float[] accels;

        public void register() {
            SensorManager sensorManager = (SensorManager) CrossControl.this
                    .getContext().getSystemService(Context.SENSOR_SERVICE);

            Sensor sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_FASTEST);
            Sensor sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_FASTEST);
        }

        public void unregister() {
            SensorManager sensorManager = (SensorManager) CrossControl.this
                    .getContext().getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;

            int type = sensor.getType();

            switch(type) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = sensorEvent.values;
                    isReady = true;
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accels = sensorEvent.values;
                    break;

            }

            if(mags != null && accels != null && isReady) {
                isReady = false;

                SensorManager.getRotationMatrix(R, I, accels, mags);
                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
                SensorManager.getOrientation(outR, values);
                int[] v = new int[3];

                v[0] = filter[0].average(values[0] * 100);
                v[1] = filter[1].average(values[1] * 100);
                v[2] = filter[2].average(values[2] * 100);

                final float xFollowValue, yFollowValue;
                if(compat8.isDisplayPortrait()) {
                    xFollowValue = v[1];
                    yFollowValue = v[2];
                }
                else {
                    xFollowValue = v[2];
                    yFollowValue = -v[1];
                }

                final float newX = getValueX() + (Math.signum(xFollowValue) * POINTER_SENSOR_SPEED_FACTOR * mSpeed);
                final float newY = getValueY() + (-1 * Math.signum(yFollowValue) * POINTER_SENSOR_SPEED_FACTOR * mSpeed);
                // Log.d(TAG, " newX = " + newX + " newY = " + newY);

                CrossControl.this.post(new Runnable() {
                    public void run() {
                        pointerSensorPosition(newX, newY);
                    }
                });
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    private class DigitalFilter {

        final int history_len = 4;
        double[] mLocHistory = new double[history_len];
        int mLocPos = 0;

        // ------------------------------------------------------------------------------------------------------------
        int average(double d) {
            float avg = 0;

            mLocHistory[mLocPos] = d;

            mLocPos++;
            if(mLocPos > mLocHistory.length - 1) {
                mLocPos = 0;
            }

            for(double h : mLocHistory) {
                avg += h;
            }

            avg /= mLocHistory.length;

            return (int) avg;
        }
    }

}
