/**
 * Created by Qasi on 24.07.2014.
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

public class FaderHorizontalControl extends BaseValueWidget {
    private final static String TAG = "widget";

    private int mFaderNullPosition = NULL_RIGHT;
    public static final int NULL_LEFT = 0;
    public static final int NULL_RIGHT = 1;
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
    private Paint mPaintBorderBlack;
    private Paint mPaintInside;
    private Paint mPaintMarker;
    private Paint mPaintMarked;

    private int markerSizeX;

    public FaderHorizontalControl(Context context) {
        super(context);
        init();
    }

    public FaderHorizontalControl(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        init();
        setDefaultValues(attrs);
    }

    public FaderHorizontalControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setDefaultValues(attrs);
    }

    private void init() {
        int height = getContext().getResources().getDisplayMetrics().heightPixels;
        if(height < getContext().getResources().getDisplayMetrics().widthPixels) {
            height = getContext().getResources().getDisplayMetrics().widthPixels;
        }
        markerSizeX = height / 24;
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
                    "faderNullPosition", NULL_CENTER));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch(mFaderNullPosition) {
            case NULL_LEFT:
                drawFaderFromLeft(canvas);
                break;
            case NULL_CENTER:
                drawFaderFromCenter(canvas);
                break;
            case NULL_RIGHT:
                drawFaderFromRight(canvas);
                break;
        }
        super.onDraw(canvas);
    }

    private void drawFaderFromLeft(Canvas canvas) {
        float percentValue = getValueX();

        RectF rectMarker = new RectF(
                percentValue * (getWidth() - markerSizeX),
                dpToPx(1.5f) - 1,
                percentValue * (getWidth() - markerSizeX) + markerSizeX,
                (getHeight() - dpToPx(1.5f)) + 1);

        RectF rectInside = new RectF(rectMarker.left, rectMarker.top, getWidth(), getHeight());

        canvas.drawRoundRect(rectInside, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintInside);

        RectF rectMarked = new RectF(0, rectMarker.top, rectMarker.right,
                getBottom());
        canvas.drawRoundRect(rectMarked, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarked);

        RectF rectBorder = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectBorder, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintBorder);

        // Log.d(TAG, "id = " + getId() + " rectMarked = " + rectMarked);
        canvas.drawRoundRect(rectMarker, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarker);
        // Log.d(TAG, "id = " + getId() + " rectMarker = " + rectMarker);
    }

    private void drawFaderFromRight(Canvas canvas) {
        float percentValue = getValueX();

        RectF rectMarker = new RectF(
                percentValue * (getWidth() - markerSizeX),
                dpToPx(1.5f) - 1,
                percentValue * (getWidth() - markerSizeX) + markerSizeX,
                (getHeight() - dpToPx(1.5f)) + 1);

        RectF rectMarked = new RectF(rectMarker.left, rectMarker.top, getWidth(), getHeight());

        canvas.drawRoundRect(rectMarked, dpToPx(ROUND_EDGE_X), ROUND_EDGE_Y,
                mPaintMarked);

        RectF rectInside = new RectF(0, rectMarker.top, rectMarker.right,
                getBottom());
        canvas.drawRoundRect(rectInside, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintInside);

        RectF rectBorder = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectBorder, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintBorder);

        canvas.drawRoundRect(rectMarker, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarker);
    }

    private void drawFaderFromCenter(Canvas canvas) {
        float percentValue = getValueX();

        RectF rectMarker = new RectF(
                percentValue * (getWidth() - markerSizeX),
                dpToPx(1.5f) - 1,
                percentValue * (getWidth() - markerSizeX) + markerSizeX,
                (getHeight() - dpToPx(1.5f)) + 1);

        RectF rectMarked, rectInside, rectInside2;
        rectInside = new RectF(0, rectMarker.top, Math.min(rectMarker.centerX(), getWidth() / 2), getHeight());
        rectInside2 = new RectF(Math.max(rectMarker.centerX(), getWidth() / 2), rectMarker.top, getWidth(), getHeight());
        if(percentValue > 0.5) {
            rectMarked = new RectF(getWidth() / 2, rectMarker.top, rectMarker.centerX(), rectMarker.bottom);
        }
        else {
            rectMarked = new RectF(rectMarker.centerX(), rectMarker.top, getWidth() / 2, rectMarker.bottom);
        }
        canvas.drawRect(rectInside, mPaintInside);
        canvas.drawRect(rectInside2, mPaintInside);
        canvas.drawRect(rectMarked, mPaintMarked);

        RectF rectBorder = new RectF(0, 0, getWidth(), getHeight());
        int bordercolor = mPaintBorder.getColor();
        mPaintBorder.setARGB(255, 0, 0, 0);
        canvas.drawRect(rectBorder, mPaintBorder);

        canvas.drawRoundRect(rectMarker, dpToPx(ROUND_EDGE_X), dpToPx(ROUND_EDGE_Y),
                mPaintMarker);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(heightMeasureSpec != 0) {
            int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
            int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

            if(sizeHeight < sizeWidth && false) {

                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        MeasureSpec.getMode(heightMeasureSpec), sizeHeight);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void pointerPosition(float x, float y, boolean isMoving) {
        float percentValue = ((x - (markerSizeX / 2)) / (getWidth() - markerSizeX));
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