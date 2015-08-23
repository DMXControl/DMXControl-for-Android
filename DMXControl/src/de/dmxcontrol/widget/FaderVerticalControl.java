/*
 * FaderVerticalControl.java
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
import android.os.Vibrator;
import android.util.AttributeSet;

import java.util.ArrayList;

import de.dmxcontrol.android.R;

public class FaderVerticalControl extends BaseValueWidget {
    private final static String TAG = "widget";

    private int mFaderNullPosition = NULL_BOTTOM;
    public static final int NULL_BOTTOM = 0;
    public static final int NULL_TOP = 1;
    public static final int NULL_CENTER = 2;

    public void setFaderNullPosition(int position) {
        this.mFaderNullPosition = position;
    }

    public int getFaderNullPosition() {
        return this.mFaderNullPosition;
    }


    private ArrayList<ValueChangedListener> listeners = new ArrayList<ValueChangedListener>();

    public void setValueChangedListener(ValueChangedListener listener) {
        this.listeners.add(listener);
    }

    public interface ValueChangedListener {
        void onValueChanged(float value, boolean isMoving);
    }

    private Paint mPaintBorder;
    private Paint mPaintInside;
    private Paint mPaintMarker;
    private Paint mPaintMarked;

    private int markerSizeY;

    public FaderVerticalControl(Context context) {
        super(context);
        init();
    }

    public FaderVerticalControl(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        init();
        setDefaultValues(attrs);
    }

    public FaderVerticalControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setDefaultValues(attrs);
    }

    private void init() {
        int height = getContext().getResources().getDisplayMetrics().heightPixels;
        if(height < getContext().getResources().getDisplayMetrics().widthPixels) {
            height = getContext().getResources().getDisplayMetrics().widthPixels;
        }
        markerSizeY = height / 24;
        int highlightColor = getResources().getColor(
                R.color.btn_background_highlight);

        int red = Color.red(highlightColor);
        int green = Color.green(highlightColor);
        int blue = Color.blue(highlightColor);

        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder.setARGB(255, red, green, blue);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setStrokeWidth(dpToPx(3));

        mPaintInside = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInside.setARGB(122, 70, 70, 70);
        mPaintInside.setStyle(Paint.Style.FILL);

        mPaintMarked = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMarked.setARGB(80, red, green, blue);
        mPaintMarked.setStyle(Paint.Style.FILL);

        mPaintMarker = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMarker.setARGB(255, red, green, blue);
        // mPaintMarker.setARGB(180, 220, 20, 60);
        mPaintMarker.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void setDefaultValues(AttributeSet attrs) {
        if(attrs != null) {
            String namespace = "de.dmxcontrol.widget";
            setFaderNullPosition(attrs.getAttributeIntValue(namespace,
                    "faderNullPosition", NULL_BOTTOM));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch(mFaderNullPosition) {
            case NULL_BOTTOM:
                drawFaderFromBottom(canvas);
                break;
            case NULL_CENTER:
                drawFaderFromCenter(canvas);
                break;
            case NULL_TOP:
                drawFaderFromTop(canvas);
                break;
        }
        super.onDraw(canvas);
    }

    private void drawFaderFromBottom(Canvas canvas) {
        float percentValue = 1 - getValueX();

        RectF rectMarker = new RectF(dpToPx(1.5f) - 1, percentValue
                * (getHeight() - markerSizeY), (getWidth() - dpToPx(1.5f)) + 1, percentValue
                * (getHeight() - markerSizeY) + markerSizeY);

        RectF rectInside = new RectF(0, 0, getWidth(), rectMarker.centerY());

        canvas.drawRoundRect(rectInside, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintInside);

        RectF rectMarked = new RectF(0, rectMarker.centerY(), getWidth(),
                getBottom());
        canvas.drawRoundRect(rectMarked, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarked);
        canvas.drawRoundRect(rectMarker, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarker);
    }

    private void drawFaderFromTop(Canvas canvas) {
        float percentValue = 1 - getValueX();

        RectF rectMarker = new RectF(dpToPx(1.5f) - 1, percentValue
                * (getHeight() - markerSizeY), (getWidth() - dpToPx(1.5f)) + 1, percentValue
                * (getHeight() - markerSizeY) + markerSizeY);

        RectF rectInside = new RectF(0, rectMarker.centerY(), getWidth(), getBottom());

        canvas.drawRoundRect(rectInside, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintInside);

        RectF rectMarked = new RectF(0, getTop(), getWidth(), rectMarker.centerY());
        canvas.drawRoundRect(rectMarked, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarked);

        canvas.drawRoundRect(rectMarker, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarker);
    }

    private void drawFaderFromCenter(Canvas canvas) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(heightMeasureSpec != 0) {
            int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
            int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

            // TODO: 23.08.15 What is this meant for?
            if(sizeHeight < sizeWidth && false) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(sizeHeight,
                        MeasureSpec.getMode(heightMeasureSpec));
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void pointerPosition(float x, float y, boolean isMoving) {
        float percentValue = 1 - ((y - (markerSizeY / 2)) / (getHeight() - markerSizeY));
        percentValue = Math.max(0, Math.min(percentValue, 1));
        percentValue = Math.round(percentValue * 1000f) / 1000f;
        if(percentValue < 0 || percentValue > 1) {
            return;
        }
        if(this.mFaderNullPosition == NULL_CENTER) {
            if((percentValue > 0.492 && 0.508 > percentValue)) {
                percentValue = 0.5f;
                if(getValueX() != percentValue) {
                    Vibrator v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(22);
                }
            }
        }
        else if(percentValue < 0.01) {
            percentValue = 0;
        }
        setValue(percentValue, 0f);
        notifyListener();
        for(ValueChangedListener listener : listeners) {
            listener.onValueChanged(percentValue, isMoving);
        }
    }

    public void pointerCancelled() {

    }
}