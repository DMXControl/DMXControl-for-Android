package de.dmxcontrol.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.Executor;

import de.dmxcontrol.activity.ServerConnection;
import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.executor.EntityExecutor;

import static android.content.DialogInterface.*;

/**
 * Created by Qasi on 14.06.2014.
 */
public class ExecutorView extends LinearLayout {
    private TextView textView;
    private FrameLayout faderLayout;
    private FaderVerticalControl fader;
    private Button flashbtn;
    private Button breakbtn;
    private Button gobtn;
    private EntityExecutor mExecutor;
    private Context Context;
    public Activity parentActivity;

    public ExecutorView(Context context,EntityExecutor executor) {
        super(context);
        Context=context;
        mExecutor=executor;
        this.Initialize();
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
        flashbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        flashbtn.setTextColor(Color.WHITE);
        //flashbtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        flashbtn.setText("FLASH");
        flashbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height/8));

        breakbtn = new Button(Context);
        breakbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        breakbtn.setTextColor(Color.WHITE);
        //breakbtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        breakbtn.setText("Break/Back");
        breakbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height/8));
        //breakbtn.callOnClick();


        gobtn = new Button(Context);
        gobtn.setBackgroundResource(R.drawable.btn_normal_selector);
        gobtn.setTextColor(Color.WHITE);
        //gobtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        gobtn.setText("GO");
        gobtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height/8));


        this.addView(textView);
        this.addView(breakbtn);
        this.addView(gobtn);
        this.addView(faderLayout);
        this.addView(flashbtn);

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
        params.height = height/8;
        breakbtn.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) gobtn.getLayoutParams();
        params.height = height/8;
        gobtn.setLayoutParams(params);
    }
}
