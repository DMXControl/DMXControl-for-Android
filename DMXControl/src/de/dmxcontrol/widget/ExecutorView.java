package de.dmxcontrol.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import de.dmxcontrol.activity.ServerConnection;
import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.executor.EntityExecutor;

import static android.content.DialogInterface.*;

/**
 * Created by Qasi on 14.06.2014.
 */
public class ExecutorView extends LinearLayout {
    private ExecutorView instance;
    private TextView textView;
    private FrameLayout faderLayout;
    private FaderVerticalControl fader;
    private Button flashbtn;
    private boolean lockflash;
    private  int pointerID =Integer.MIN_VALUE;
    private Button breakbtn;
    private Button gobtn;
    private Button stopbtn;
    private EntityExecutor mExecutor;
    private Context Context;
    private boolean flashToggled=false;
    public Activity parentActivity;
    private Timer timer =new Timer();

    public ExecutorView(Context context,EntityExecutor executor) {
        super(context);
        Context=context;
        mExecutor=executor;
        this.Initialize();
        instance=this;
    }

    private void Initialize() {


        this.setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        //LinearLayout.LayoutParams params= (LayoutParams) this.getLayoutParams();
        //params.height=100;
        //this.setLayoutParams(params);

        DisplayMetrics metrics = Context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        //Prefs.get().Height=height;

        textView = new TextView(Context);
        textView.setText(mExecutor.getName());
        mExecutor.setNameChangedListener(new Entity.NameChangedListener() {
            @Override
            public void onNameChanged(String name) {
                if (parentActivity != null)
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(mExecutor.getName());
                            instance.Resize();
                            instance.invalidate();
                        }
                    });
            }
        });
        int margin=10;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        layoutParams.leftMargin=margin;
        layoutParams.rightMargin=margin;
        fader = new FaderVerticalControl(Context);
        fader.setValue(mExecutor.getValue(),0);
        mExecutor.setValueChangedListener(new EntityExecutor.ValueChangedListener()
        {
            @Override
            public void onValueChanged(float value) {
                if (parentActivity != null)
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fader.setValue(mExecutor.getValue(), 0);
                        }
                    });
            }
        });
        fader.setValueChangedListener(new FaderVerticalControl.ValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                mExecutor.setValue(value, false);
            }

        });
        fader.setLayoutParams(layoutParams);


        layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                (height/2)+(height/16)));
        layoutParams.leftMargin=margin;
        layoutParams.rightMargin=margin;
        faderLayout=new FrameLayout(Context);
        faderLayout.addView(fader);
        faderLayout.setLayoutParams(layoutParams);

        flashbtn = new Button(Context);
        flashbtn.setEnabled(false);
        flashbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        flashbtn.setTextColor(Color.WHITE);
        //flashbtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        flashbtn.setText("FLASH");
        flashbtn.setTag("Flash "+mExecutor.getName());
        flashbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height / 8));
        flashbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //ButtonUp();
                // lockflash=false;
            }
        });
        flashbtn.setEnabled(false);
        flashbtn.setClickable(false);
        mExecutor.setFlashChangedListener(new EntityExecutor.FlashChangedListener() {
            @Override
            public void onFlashChanged(float value) {
                if (parentActivity != null)
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!lockflash) {
                                flashbtn.setPressed(mExecutor.getFlash());
                            }
                        }
                    });
            }
        });
        /**flashbtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()!= MotionEvent.ACTION_MOVE) {
                    int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                    int pointerId = event.getPointerId(pointerIndex);
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            lockflash=true;
                            pointerID=pointerIndex;
                            ButtonDown();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if(pointerID==pointerIndex) {
                                ButtonUp();
                                lockflash=false;
                            }
                            break;
                        default:
                            break;
                    }
                }
                return true;

            }
        });
**/

        breakbtn = new Button(Context);
        breakbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        breakbtn.setTextColor(Color.WHITE);
        //breakbtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        breakbtn.setText("Break/Back");
        breakbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height / 16));
        breakbtn.setTag("Break/Back " + mExecutor.getName());
        breakbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.BreakBack();
            }
        });
        //breakbtn.callOnClick();


        gobtn = new Button(Context);
        gobtn.setBackgroundResource(R.drawable.btn_normal_selector);
        gobtn.setTextColor(Color.WHITE);
        //gobtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        gobtn.setText("GO");
        gobtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height/8));

        gobtn.setTag("GO "+mExecutor.getName());
        gobtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.GO();
            }
        });

        stopbtn = new Button(Context);
        stopbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        stopbtn.setTextColor(Color.WHITE);
        //stopbtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        stopbtn.setText("STOP");

        stopbtn.setTag("Stop "+mExecutor.getName());
        stopbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height/16));
        stopbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.Stop();
            }
        });


        this.addView(textView);
        this.addView(stopbtn);
        this.addView(breakbtn);
        this.addView(gobtn);
        this.addView(faderLayout);
        this.addView(flashbtn);

    }

    private IMotionEventWrapper mMew;
    public boolean TouchEvent(MotionEvent event) {
        mMew = MotionEventWrapper.get(event);
        int action = event.getAction();
        int actionMasked = action & mMew.getActionMaskCONST();
        int pid = mMew.getPointerIdByAction(action);
        switch(actionMasked) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_CANCEL:
                if (isPointInsideView(event.getX(), event.getY(), flashbtn)) {
                    ButtonDown();
                    return true;
                }
                if (isPointInsideView(event.getX(), event.getY(), stopbtn)) {
                    stopbtn.callOnClick();
                    return true;
                }
                if (isPointInsideView(event.getX(), event.getY(), gobtn)) {
                    gobtn.callOnClick();
                    return true;
                }
                if (isPointInsideView(event.getX(), event.getY(), breakbtn)) {
                    breakbtn.callOnClick();
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (isPointInsideView(event.getX(), event.getY(), flashbtn)) {
                    ButtonUp();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }
    private void ButtonUp(){/**
        if (!flashToggled) {
            if (mExecutor.getFlash()) {
                mExecutor.setFlash(false, false);
                gobtn.setTextColor(Color.RED);
            }
            else
                mExecutor.setFlash(true, false);
                mExecutor.setFlash(true, false);
                mExecutor.setFlash(true, false);
                mExecutor.setFlash(false, false);
            if(flashbtn.isPressed())
                flashbtn.setPressed(false);
        }
        else
            flashbtn.setPressed(true);**/
            //if(mExecutor.getFlash()) {
                mExecutor.setFlash(false, false);
            //}
            //if(flashbtn.isPressed()) {
                flashbtn.setPressed(false);
            //}
        timer= new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            boolean first=true;
            @Override
            public void run() {
                mExecutor.setFlash(false, false);
                if(!flashbtn.isPressed()&&!first) {
                    lockflash=false;
                    this.cancel();
                }
                first=false;
            }
        },50,50);
    }

    private void ButtonDown(){
        //flashbtn.setClickable(true);
        flashToggled =mExecutor.getToggle()? !flashToggled:false;
        lockflash=true;
        /**if (!mExecutor.getFlash()) {
            mExecutor.setFlash(true, false);
            gobtn.setTextColor(Color.GREEN);
        }

        //if(!flashbtn.isPressed())
            flashbtn.setPressed(true);**/
        if(!mExecutor.getFlash()) {
            mExecutor.setFlash(true, false);
        }
        if(!flashbtn.isPressed()) {
            flashbtn.setPressed(true);
        }
        /**timer= new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!flashbtn.isPressed()) {
                    lockflash=false;
                    this.cancel();
                }
            }
        },250,250);**/
    }

    public void Resize() {
        DisplayMetrics metrics = Context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) faderLayout.getLayoutParams();
        params.height = (height/2)+(height/16);
        faderLayout.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) flashbtn.getLayoutParams();
        params.height = height/8;
        flashbtn.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) breakbtn.getLayoutParams();
        params.height = height/16;
        breakbtn.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) gobtn.getLayoutParams();
        params.height = height/8;
        gobtn.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) stopbtn.getLayoutParams();
        params.height = height/16;
        stopbtn.setLayoutParams(params);
    }

    public void RaiseMultiTouch(MotionEvent.PointerCoords pointerCoords ,MotionEvent event) {
        pointerCoords.toString();
        int[] lfader = new int[2];
        fader.getLocationOnScreen(lfader);
        Rect rectfader = new Rect(lfader[0], lfader[1], lfader[0] + fader.getWidth(), lfader[1] + fader.getHeight());


        //fader.get
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        int pointerId = event.getPointerId(pointerIndex);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (isPointInsideView(pointerCoords.x, pointerCoords.y, flashbtn)) {
                    pointerID = pointerIndex;
                    ButtonDown();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isPointInsideView(pointerCoords.x, pointerCoords.y, flashbtn)) {
                    if (pointerID == pointerIndex) {
                        pointerID = Integer.MIN_VALUE;
                        ButtonUp();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isPointInsideView(pointerCoords.x, pointerCoords.y, fader)) {
                    fader.pointerPosition(pointerCoords.x, pointerCoords.y);
                }

                break;
        }

        (action + "").toString();
    }

    @Override
    public Object getTag(){
        return mExecutor.getName();
    }

    private boolean isPointInsideView(float x, float y, View view){
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if(( x > viewX && x < (viewX + view.getWidth())) &&
                ( y > viewY && y < (viewY + view.getHeight()))){
            return true;
        } else {
            return false;
        }
    }
/**
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        int pointerId = ev.getPointerId(pointerIndex);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (isPointInsideView(ev.getX(), ev.getY(), flashbtn)) {
                    pointerID = pointerIndex;
                    ButtonDown();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isPointInsideView(ev.getX(),ev.getY(), flashbtn)) {
                    if (pointerID == pointerIndex) {
                        pointerID = Integer.MIN_VALUE;
                        ButtonUp();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isPointInsideView(ev.getX(), ev.getY(), fader)) {
                    //fader.pointerPosition(ev.getX(), ev.getY());
                }

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }**/
}
