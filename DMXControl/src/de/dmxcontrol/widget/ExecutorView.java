package de.dmxcontrol.widget;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;

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

    public ExecutorView(Context context,String name) {
        super(context);
        this.Initialize(context,name);
    }

    private void Initialize(Context context,String name) {


        this.setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        //LinearLayout.LayoutParams params= (LayoutParams) this.getLayoutParams();
        //params.height=100;
        //this.setLayoutParams(params);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Prefs.get().Height=height;

        textView = new TextView(context);
        textView.setText(name);
        int margin=10;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        layoutParams.leftMargin=margin;
        layoutParams.rightMargin=margin;
        fader = new FaderVerticalControl(context);
        fader.setLayoutParams(layoutParams);


        layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                (Prefs.get().Height/2)+(Prefs.get().Height/16)));
        layoutParams.leftMargin=margin;
        layoutParams.rightMargin=margin;
        faderLayout=new FrameLayout(context);
        faderLayout.addView(fader);
        faderLayout.setLayoutParams(layoutParams);

        flashbtn = new Button(context);
        flashbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        flashbtn.setTextColor(Color.WHITE);
        //flashbtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        flashbtn.setText("FLASH");
        flashbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                Prefs.get().Height/8));

        breakbtn = new Button(context);
        breakbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        breakbtn.setTextColor(Color.WHITE);
        //breakbtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        breakbtn.setText("Break/Back");
        breakbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                Prefs.get().Height/8));
        //breakbtn.callOnClick();


        gobtn = new Button(context);
        gobtn.setBackgroundResource(R.drawable.btn_normal_selector);
        gobtn.setTextColor(Color.WHITE);
        //gobtn.setBackgroundColor(Integer.parseInt("@drawable/btn_normal_selector"));
        gobtn.setText("GO");
        gobtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                Prefs.get().Height/8));


        this.addView(textView);
        this.addView(faderLayout);
        this.addView(flashbtn);
        this.addView(breakbtn);
        this.addView(gobtn);

    }
    public void Resice() {

        fader.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        flashbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        faderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        breakbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        gobtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
    }
}
