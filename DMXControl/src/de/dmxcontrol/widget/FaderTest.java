/*
 * FaderTest.java
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
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class FaderTest extends View {
    private final static String TAG = "widget";

    private final static int ROUND_EDGE_X = 10;
    private final static int ROUND_EDGE_Y = 10;

    private Paint paintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintInside = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintMarker = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintMarked = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float markerRelativeSizeX = 1.3f;
    private int markerSizeY = 20;

    private float percentValue;

    static class Size {
        int left;
        int top;
        int width;
        int height;

    }

    public FaderTest(Context context) {
        super(context);
        init();
    }

    public FaderTest(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FaderTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        paintBorder.setARGB(50, 255, 255, 255);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(2);

        // paintInside.setARGB(122, 70, 70, 70);
        // paintInside.setStyle( Paint.Style.FILL );
        //
        // paintMarked.setARGB(122, 160, 160, 160);
        // paintMarked.setStyle( Paint.Style.FILL );
        //
        // paintMarker.setARGB(255, 100, 50, 50);
        // paintMarker.setStyle( Paint.Style.FILL );

        setPercentValue(0.7f);
    }

    public void setPercentValue(float percentValue) {
        this.percentValue = percentValue;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");

        Size s = getFaderSize();

        RectF rectBorder = new RectF(s.left, s.top, s.left + s.width, s.top
                + s.height);
        canvas.drawRoundRect(rectBorder, ROUND_EDGE_X, ROUND_EDGE_Y,
                paintBorder);

        // RectF rectInside = new RectF(s.left, s.top, s.left + s.width,
        // s.top + ( s.height * percentValue));
        // canvas.drawRect(rectInside, paintInside);
        //
        // RectF rectMarked = new RectF(s.left, s.top + ( s.height *
        // percentValue ), s.left
        // + s.width, s.top + s.height);
        // canvas.drawRect(rectMarked, paintMarked);

        // RectF rectMarker = new RectF(getLeft(), s.top + ( s.height *
        // percentValue) - markerSizeY / 2,
        // getRight(), s.top + ( s.height * percentValue ) + markerSizeY / 2);
        // canvas.drawRoundRect(rectMarker, ROUND_EDGE_X, ROUND_EDGE_Y,
        // paintMarker);

        super.onDraw(canvas);
    }

    public Size getFaderSize() {
        Size s = new Size();
        s.height = getHeight();
        s.top = getTop();

        s.width = (int) (getWidth() / markerRelativeSizeX);
        s.left = (getWidth() / 2 - s.width / 2);
        return s;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (heightMeasureSpec != 0) {
            int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
            int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

            if (sizeHeight < sizeWidth) {
                sizeHeight = sizeWidth * 3 / 2;

                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        MeasureSpec.getMode(heightMeasureSpec), sizeHeight);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        Log.d(TAG, "onTouchEvent: action = " + action);
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE) {
            float y = event.getY();
            Log.d(TAG, "onTouchEvent: y = " + y);
            percentValue = y / getHeight();
            if (percentValue < 0 || percentValue > 1)
                return false;

            setPercentValue(percentValue);
            return true;
        }

        return super.onTouchEvent(event);
    }

}
