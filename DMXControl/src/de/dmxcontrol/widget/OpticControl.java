package de.dmxcontrol.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

import de.dmxcontrol.android.R;

/**
 * Created by Qasi on 18.07.2014.
 */
public class OpticControl extends View implements View.OnTouchListener {
    private float mRadiusLens1, radius1, radius2, radius3, radius4, radius5;
    private float size1, size2, size3, size4;
    private int mGestureMode = GESTURE_MODE_ZOOM;
    private Paint paint;
    private Shader shader;

    public int getGestureMode() {
        return this.mGestureMode;
    }

    public void setGestureMode(int mode) {
        this.mGestureMode = mode;
    }

    private ArrayList<ValueChangedListener> ZoomChangedListeners = new ArrayList<ValueChangedListener>();

    public void setZoomChangedListener(ValueChangedListener listener) {
        this.ZoomChangedListeners.add(listener);
    }

    private void runZoomChangedListener(float value) {
        for(ValueChangedListener listener : ZoomChangedListeners) {
            listener.onValueChanged(value);
        }
    }

    private ArrayList<ValueChangedListener> FocusChangedListeners = new ArrayList<ValueChangedListener>();

    public void setFocusChangedListener(ValueChangedListener listener) {
        this.FocusChangedListeners.add(listener);
    }

    private void runFocusChangedListener(float value) {
        for(ValueChangedListener listener : FocusChangedListeners) {
            listener.onValueChanged(value);
        }
    }

    private ArrayList<ValueChangedListener> IrisChangedListeners = new ArrayList<ValueChangedListener>();

    public void setIrisChangedListener(ValueChangedListener listener) {
        this.IrisChangedListeners.add(listener);
    }

    private void runIrisChangedListener(float value) {
        for(ValueChangedListener listener : IrisChangedListeners) {
            listener.onValueChanged(value);
        }
    }

    private ArrayList<ValueChangedListener> FrostChangedListeners = new ArrayList<ValueChangedListener>();

    public void setFrostChangedListener(ValueChangedListener listener) {
        this.FrostChangedListeners.add(listener);
    }

    private void runFrostChangedListener(float value) {
        for(ValueChangedListener listener : FrostChangedListeners) {
            listener.onValueChanged(value);
        }
    }

    public interface ValueChangedListener {
        void onValueChanged(float value);
    }

    public static final int GESTURE_MODE_ZOOM = 0;
    private static final float MIN_INTERNAL_ZOOM = 0.6f;
    private ScaleGestureDetector detectorZoom;
    private ValueAnimator animatorZoom;
    private float mZoom = 0f;
    private float mCountDownZoom = mZoom;

    public float getZoom() {
        return this.mZoom;
    }

    public void setZoom(final float zoom) {
        this.mZoom = zoom;
        animatorZoom = ValueAnimator.ofFloat(this.mCountDownZoom, this.mZoom);
        animatorZoom.setDuration((long) (200 * Math.abs(this.mCountDownZoom - this.mZoom)));
        animatorZoom.setInterpolator(new LinearInterpolator());
        animatorZoom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCountDownZoom = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animatorZoom.start();
    }

    private void setZoomDirect(float zoom) {
        this.mZoom = zoom;
        this.mCountDownZoom = this.mZoom;
        runZoomChangedListener(this.mZoom);
        invalidate();
    }

    public static final int GESTURE_MODE_FOCUS = 1;
    private static final float MIN_INTERNAL_FOCUS = 0.9f;
    private ValueAnimator animatorFocus;
    private float mFocus = 0f;
    private float mCountDownFocus = mFocus;

    public float getFocus() {
        return this.mFocus;
    }

    public void setFocus(final float focus) {
        this.mFocus = focus;
        animatorFocus = ValueAnimator.ofFloat(this.mCountDownFocus, this.mFocus);
        animatorFocus.setDuration((long) (200 * Math.abs(this.mCountDownFocus - this.mFocus)));
        animatorFocus.setInterpolator(new LinearInterpolator());
        animatorFocus.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCountDownFocus = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animatorFocus.start();
    }

    private void setFocusDirect(float focus) {
        this.mFocus = focus;
        this.mCountDownFocus = this.mFocus;
        runFocusChangedListener(this.mFocus);
        invalidate();
    }

    public static final int GESTURE_MODE_IRIS = 2;
    private static final float MIN_INTERNAL_IRIS = 0.1f;
    private static int IRIS_FRAMES = 16;
    private ValueAnimator animatorIris;
    private float mIris = 1f;
    private float mCountDownIris = mIris;

    public float getIris() {
        return this.mIris;
    }

    public void setIris(final float iris) {
        this.mIris = iris;
        animatorIris = ValueAnimator.ofFloat(this.mCountDownIris, this.mIris);
        animatorIris.setDuration((long) (200 * Math.abs(this.mCountDownIris - this.mIris)));
        animatorIris.setInterpolator(new LinearInterpolator());
        animatorIris.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCountDownIris = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animatorIris.start();
    }

    private void setIrisDirect(float iris) {
        this.mIris = iris;
        this.mCountDownIris = this.mIris;
        runIrisChangedListener(this.mIris);
        invalidate();
    }

    public static final int GESTURE_MODE_FROST = 3;
    private static final float MIN_INTERNAL_FROST = 0f;
    private ValueAnimator animatorFrost;
    private float mFrost = 0f;
    private float mCountDownFrost = mFrost;

    public float getFrost() {
        return this.mFrost;
    }

    public void setFrost(final float frost) {
        this.mFrost = frost;
        animatorFrost = ValueAnimator.ofFloat(this.mCountDownFrost, this.mFrost);
        animatorFrost.setDuration((long) (200 * Math.abs(this.mCountDownFrost - this.mFrost)));
        animatorFrost.setInterpolator(new LinearInterpolator());
        animatorFrost.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCountDownFrost = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animatorFrost.start();
    }

    private void setFrostDirect(float frost) {
        this.mFrost = frost;
        this.mCountDownFrost = this.mFrost;
        runFrostChangedListener(this.mFrost);
        invalidate();
    }

    private Bitmap
            mFocusWheel,
            mLens1,
            mLens2,
            mLens3,
            mFrostFilter;
    private int
            mFrameShaderColor0,
            mFrameShaderColor1,
            mFrameShaderColor2,
            mFrameShaderColor3,
            mFrameShaderColor4;

    public OpticControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpticControl(Context context) {
        super(context);
        init();
    }

    public OpticControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.mLens1 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.optic_lens_1);
        this.mLens2 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.optic_lens_2);
        this.mLens3 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.optic_lens_3);
        this.mFrostFilter = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.optic_frost);
        this.mFocusWheel = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.optic_focus_wheel);
        this.mFrameShaderColor0 = getContext().getResources().getColor(R.color.white_smoke);
        this.mFrameShaderColor1 = getContext().getResources().getColor(R.color.light_grey);
        this.mFrameShaderColor2 = getContext().getResources().getColor(R.color.black);
        this.mFrameShaderColor3 = getContext().getResources().getColor(R.color.mid_grey);
        this.mFrameShaderColor4 = getContext().getResources().getColor(R.color.light_black);

        detectorZoom = new ScaleGestureDetector(getContext(), new ZoomListener());
        this.setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(Math.min(widthMeasureSpec, heightMeasureSpec), Math.min(widthMeasureSpec, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.calculateValues();
        this.drawLenses(canvas);
        this.drawFrame(canvas);
        this.drawText(canvas);
    }

    private int[] calculateValues() {
        int height = getHeight();
        if(height < getWidth()) {
            height = getWidth();
        }
        int width = getWidth();
        if(width > getHeight()) {
            width = getHeight();
        }
        size1 = height / 30;
        size2 = (size1 / 3) + 1;
        size3 = size1 + (size2 / 2);
        size4 = size2 / 2;
        radius1 = (width / 2) - size1;
        radius2 = radius1 - size1 + size2;
        radius3 = radius2 - size3 + size2;
        radius4 = (radius3 - size3) + size2;
        radius5 = radius4 - size4;
        mRadiusLens1 = radius5;
        return new int[]{width, height};
    }

    private void drawFrame(Canvas canvas) {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(255);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        shader = new LinearGradient(
                0, 0, 0,
                getHeight() / 2,
                mFrameShaderColor1,
                mFrameShaderColor2,
                Shader.TileMode.CLAMP);

        paint.setStrokeWidth(size1);
        paint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius1, paint);

        shader = new RadialGradient(
                getWidth() - (getWidth() / 4.5f),
                getHeight() - (getHeight() / 4.5f),
                radius1,
                mFrameShaderColor1,
                mFrameShaderColor2,
                Shader.TileMode.CLAMP);

        paint.setStrokeWidth(size3 + size2 + size4 + size4);
        paint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius3, paint);


        shader = new LinearGradient(
                0, 0, 0,
                getHeight() / 2,
                mFrameShaderColor3,
                mFrameShaderColor4,
                Shader.TileMode.CLAMP);

        paint.setStrokeWidth(size2);
        paint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius2, paint);

        shader = new LinearGradient(
                0, 0, 0,
                getHeight() / 3,
                0xffffffff,
                0x44000000,
                Shader.TileMode.CLAMP);

        paint.setStrokeWidth(size4);
        paint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius4, paint);

        shader = new LinearGradient(
                0, 0, 0,
                getHeight() / 2,
                mFrameShaderColor2,
                mFrameShaderColor3,
                Shader.TileMode.MIRROR);

        paint.setStrokeWidth(size4);
        paint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius5, paint);
    }

    private void drawLenses(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //Draw Zoom
        float scale = 1 / (mLens3.getWidth() / (mRadiusLens1 * 2));
        scale *= (((1f - MIN_INTERNAL_ZOOM) * this.mCountDownZoom) + MIN_INTERNAL_ZOOM);
        float
                x = (getWidth() / 2) - ((mLens3.getWidth() / 2) * scale),
                y = (getHeight() / 2) - ((mLens3.getHeight() / 2) * scale);

        Matrix m = new Matrix();
        m.postScale(scale, scale);
        m.postTranslate(x, y);
        canvas.drawBitmap(mLens3, m, paint);

        drawIris(canvas);

        //Draw Frost
        scale = 1 / (mFrostFilter.getWidth() / (mRadiusLens1 * 2));
        float frost = (((1f - MIN_INTERNAL_FROST) * this.mCountDownFrost) + MIN_INTERNAL_FROST);

        x = (getWidth() / 2) - ((mFrostFilter.getWidth() / 2) * scale);
        y = (getHeight() / 2) - ((mFrostFilter.getHeight() / 2) * scale);

        //m= new Matrix();
        m.reset();
        paint.setAlpha((int) (frost * 255));
        m.postScale(scale, scale);
        m.postTranslate(x, y);
        canvas.drawBitmap(mFrostFilter, m, paint);

        //Draw Focus
        scale = 1 / (mLens2.getWidth() / (mRadiusLens1 * 2));
        scale *= (((1f - MIN_INTERNAL_FOCUS) * this.mCountDownFocus) + MIN_INTERNAL_FOCUS);

        x = (getWidth() / 2) - ((mLens2.getWidth() / 2) * scale);
        y = (getHeight() / 2) - ((mLens2.getHeight() / 2) * scale);

        //m= new Matrix();
        m.reset();
        paint.setAlpha(255);
        m.postScale(scale, scale);
        m.postTranslate(x, y);
        canvas.drawBitmap(mLens2, m, paint);

        //Draw FrontLens
        scale = 1 / (mLens1.getWidth() / (mRadiusLens1 * 2));
        x = (getWidth() / 2) - ((mLens1.getWidth() / 2) * scale);
        y = (getHeight() / 2) - ((mLens1.getHeight() / 2) * scale);

        //m= new Matrix();
        m.reset();
        m.postScale(scale, scale);
        m.postTranslate(x, y);
        canvas.drawBitmap(mLens1, m, paint);

        scale = 1 / (mFocusWheel.getWidth() / ((radius1 + size1 - size4) * 2));
        x = (getWidth() / 2) - ((mFocusWheel.getWidth() / 2) * scale);
        y = (getHeight() / 2) - ((mFocusWheel.getHeight() / 2) * scale);


        //m= new Matrix();
        m.reset();
        m.postScale(scale, scale);
        m.postTranslate(x, y);
        m.preRotate(-(mCountDownZoom * 60f), mFocusWheel.getWidth() / 2, mFocusWheel.getHeight() / 2);
        canvas.drawBitmap(mFocusWheel, m, paint);
        m.reset();
    }

    private void drawIris(Canvas canvas) {
        int counter = 0;
        Path path = new Path();
        for(int i = 0; i < IRIS_FRAMES; i++) {

            getIrisPolygonPath(path, counter, counter + 1);
            counter++;
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getContext().getResources().getColor(R.color.white_smoke));
        shader = new RadialGradient(
                getWidth() - (getWidth() / 4.5f),
                getHeight() - (getHeight() / 4.5f),
                radius1,
                mFrameShaderColor3,
                mFrameShaderColor4,
                Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawPath(path, paint);
    }

    private void getIrisPolygonPath(Path path, float deg1, float deg2) {
        float degree1 = (float) ((deg1 / IRIS_FRAMES) * 2 * Math.PI), degree2 = (float) ((deg2 / IRIS_FRAMES) * 2 * Math.PI);
        float centerX = (getWidth() / 2);
        float centerY = (getHeight() / 2);
        float iris = (((1f - MIN_INTERNAL_IRIS) * this.mCountDownIris) + MIN_INTERNAL_IRIS);

        path.moveTo((float) (centerX + radius1 * Math.sin(degree1)), (float) (centerY + radius1 * -Math.cos(degree1)));
        path.lineTo((float) (centerX + ((radius4 + (size4 * 1.5f)) * iris) * Math.sin(degree1)), (float) (centerY + ((radius4 + (size4 * 1.5f)) * iris) * -Math.cos(degree1)));
        path.lineTo((float) (centerX + ((radius4 + (size4 * 1.5f)) * iris) * Math.sin(degree2)), (float) (centerY + ((radius4 + (size4 * 1.5f)) * iris) * -Math.cos(degree2)));
        path.lineTo((float) (centerX + radius1 * Math.sin(degree2)), (float) (centerY + radius1 * -Math.cos(degree2)));
        path.lineTo((float) (centerX + radius1 * Math.sin(degree1)), (float) (centerY + radius1 * -Math.cos(degree1)));
    }

    private void drawText(Canvas canvas) {
        Path mArc = new Path();
        float range = (float) ((radius1 + size4) * 2 * Math.PI);
        float degreePerPixel = 360f / range;
        RectF oval = new RectF(
                -(radius1 + 3) + (getWidth() / 2),
                -(radius1 + 3) + (getHeight() / 2),
                (radius1 + 3) + (getWidth() / 2),
                (radius1 + 3) + (getHeight() / 2));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(this.mFrameShaderColor0);
        paint.setTextSize(size1 - (size2 / 2));

        String text = getContext().getResources().getString(R.string.about_title);
        float width = paint.measureText(text, 0, text.length());
        mArc.addArc(oval, 180 - (degreePerPixel * width / 2), 200);
        canvas.drawTextOnPath(text, mArc, 0, 20, paint);

        text = getContext().getResources().getString(R.string.about_version);
        mArc = new Path();
        width = paint.measureText(text, 0, text.length());
        mArc.addArc(oval, -(degreePerPixel * width / 2), 200);
        canvas.drawTextOnPath(text, mArc, 0, 20, paint);
    }

    private boolean isPointInRectangle(
            double x, double y) {
        return x >= (getWidth() / 2) - mRadiusLens1 && x <= (getWidth() / 2) + mRadiusLens1 &&
                y >= (getHeight() / 2) - mRadiusLens1 && y <= (getHeight() / 2) + mRadiusLens1;
    }

    private boolean isPointOnLens(float x, float y) {
        if(isPointInRectangle(x, y)) {
            double dx = (getWidth() / 2) - x;
            double dy = (getHeight() / 2) - y;
            dx *= dx;
            dy *= dy;
            double distanceSquared = dx + dy;
            double radiusSquared = mRadiusLens1 * mRadiusLens1;
            return distanceSquared <= radiusSquared;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return detectorZoom.onTouchEvent(event);
    }

    private class ZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor;
            switch(mGestureMode) {
                case GESTURE_MODE_ZOOM:
                    scaleFactor = getZoom();
                    if(scaleFactor >= 0.01) {
                        scaleFactor *= detector.getScaleFactor();
                    }
                    else {
                        scaleFactor = 0.01f * detector.getScaleFactor();
                    }
                    scaleFactor = Math.max(0, Math.min(scaleFactor, 1));
                    setZoomDirect(scaleFactor);
                    return true;
                case GESTURE_MODE_FOCUS:
                    scaleFactor = getFocus();
                    if(scaleFactor >= 0.01) {
                        scaleFactor *= detector.getScaleFactor();
                    }
                    else {
                        scaleFactor = 0.01f * detector.getScaleFactor();
                    }
                    scaleFactor = Math.max(0, Math.min(scaleFactor, 1));
                    setFocusDirect(scaleFactor);
                    return true;
                case GESTURE_MODE_IRIS:
                    scaleFactor = getIris();
                    if(scaleFactor >= 0.01) {
                        scaleFactor *= detector.getScaleFactor();
                    }
                    else {
                        scaleFactor = 0.01f * detector.getScaleFactor();
                    }
                    scaleFactor = Math.max(0, Math.min(scaleFactor, 1));
                    setIrisDirect(scaleFactor);
                    return true;
                case GESTURE_MODE_FROST:
                    scaleFactor = getFrost();
                    if(scaleFactor >= 0.01) {
                        scaleFactor *= detector.getScaleFactor();
                    }
                    else {
                        scaleFactor += 0.01;
                    }
                    scaleFactor = Math.max(0, Math.min(scaleFactor, 1));
                    setFrostDirect(scaleFactor);
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            float scaleFactor;
            switch(mGestureMode) {
                case GESTURE_MODE_ZOOM:
                    if(getZoom() <= 0.01) {
                        setZoomDirect(0);
                    }
                    break;
                case GESTURE_MODE_FOCUS:
                    if(getFocus() <= 0.01) {
                        setFocusDirect(0);
                    }
                    break;
                case GESTURE_MODE_IRIS:
                    if(getIris() <= 0.01) {
                        setIrisDirect(0);
                    }
                    break;
                case GESTURE_MODE_FROST:
                    if(getFrost() <= 0.01) {
                        setFrostDirect(0);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
