package de.dmxcontrol.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Qasi on 18.06.2014.
 */
public class ExecuterPageSliderView extends HorizontalScrollView {
    private Context Context;

    public ExecuterPageSliderView(Context context) {
        super(context);
        Context = context;
    }

    public ExecuterPageSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Context = context;
    }

    public ExecuterPageSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int height = Context.getResources().getDisplayMetrics().heightPixels;
        if(ev.getY() < height - (height / 2) - (height / 4) - 10) {
            super.onTouchEvent(ev);
        }
        return false;
    }
}
