package de.dmxcontrol.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.executor.EntityExecutor;

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
    private int pointerID = Integer.MIN_VALUE;
    private Button breakbtn;
    private Button gobtn;
    private Button stopbtn;
    private EntityExecutor mExecutor;
    private Context Context;
    private boolean flashToggled = false;
    public Activity parentActivity;
    private Timer timer = new Timer();

    public ExecutorView(Context context, EntityExecutor executor) {
        super(context);
        Context = context;
        mExecutor = executor;
        this.Initialize();
        instance = this;
    }

    private void Initialize() {
        this.setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        int height = Context.getResources().getDisplayMetrics().heightPixels;
        int max = Math.max(Context.getResources().getDisplayMetrics().widthPixels, height) / 8;
        textView = new TextView(Context);
        textView.setMaxWidth(max);
        textView.setMinWidth(max);
        textView.setMaxLines(1);
        textView.setSingleLine();
        //textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(getResources().getColor(R.color.dark_white_smoke));
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setText(mExecutor.getName());
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Edit Executor");
                final View view = View.inflate(v.getContext(), R.layout.executor_setting_dialog, null);
                final EditText input = (EditText) view.findViewById(R.id.editName);
                input.setText(textView.getText());
                final RadioGroup dropdown = (RadioGroup) view.findViewById(R.id.radioButtonGroup);
                final RadioButton intensity_btn = (RadioButton) view.findViewById(R.id.radioButton_Intensity);
                final RadioButton timing_btn = (RadioButton) view.findViewById(R.id.radioButton_Timing);
                final RadioButton speed_btn = (RadioButton) view.findViewById(R.id.radioButton_EffectSpeed);
                switch(mExecutor.getFaderMode()) {
                    case 0:
                        intensity_btn.setChecked(true);
                        break;
                    case 1:
                        timing_btn.setChecked(true);
                        break;
                    case 2:
                        speed_btn.setChecked(true);
                        break;
                }
                final ToggleButton toggle_btn = (ToggleButton) view.findViewById(R.id.toggleButton_flash);
                toggle_btn.setChecked(mExecutor.getToggle());
                alert.setView(view);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = input.getEditableText().toString();
                        mExecutor.setName(str, false);
                        mExecutor.setToggle(toggle_btn.isChecked(), false);
                        if(intensity_btn.isChecked()) {
                            mExecutor.setFaderMode(0, false);
                        }
                        else if(timing_btn.isChecked()) {
                            mExecutor.setFaderMode(1, false);
                        }
                        else if(speed_btn.isChecked()) {
                            mExecutor.setFaderMode(2, false);
                        }
                        textView.setText(mExecutor.getName());
                        Toast.makeText(alert.getContext(), str, Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });
        mExecutor.setNameChangedListener(new Entity.NameChangedListener() {
            @Override
            public void onNameChanged(String name) {
                if(parentActivity != null) {
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(mExecutor.getName());
                            instance.Resize();
                            instance.invalidate();
                        }
                    });
                }
            }
        });
        int margin = 10;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        layoutParams.leftMargin = margin;
        layoutParams.rightMargin = margin;
        fader = new FaderVerticalControl(Context);
        fader.setValue(mExecutor.getValue(), 0);
        mExecutor.setValueChangedListener(new EntityExecutor.ValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                if(parentActivity != null) {
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fader.setValue(mExecutor.getValue(), 0);
                        }
                    });
                }
            }
        });
        fader.setValueChangedListener(new FaderVerticalControl.ValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                mExecutor.setValue(value, false);
            }

        });
        fader.setLayoutParams(layoutParams);


        faderLayout = new FrameLayout(Context);
        faderLayout.addView(fader);
        layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                (height / 2) + (height / 16)));
        layoutParams.leftMargin = margin;
        layoutParams.rightMargin = margin;
        faderLayout.setLayoutParams(layoutParams);

        flashbtn = new Button(Context);
        flashbtn.setEnabled(false);
        flashbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        flashbtn.setTextColor(getResources().getColor(R.color.dark_white_smoke));
        flashbtn.setTextSize(18);
        flashbtn.setTypeface(Typeface.createFromAsset(Context.getAssets(), "octicons.ttf"));
        flashbtn.setText("\u26A1");
        flashbtn.setTag(flashbtn.getText() + " " + mExecutor.getName());
        flashbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height / 8));
        flashbtn.setEnabled(false);
        flashbtn.setClickable(false);
        mExecutor.setFlashChangedListener(new EntityExecutor.FlashChangedListener() {
            @Override
            public void onFlashChanged(float value) {
                if(parentActivity != null) {
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!lockflash) {
                                flashbtn.setPressed(mExecutor.getFlash());
                            }
                        }
                    });
                }
            }
        });
        breakbtn = new Button(Context);
        breakbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        breakbtn.setTextColor(getResources().getColor(R.color.dark_white_smoke));
        breakbtn.setTypeface(Typeface.createFromAsset(Context.getAssets(), "octicons.ttf"));
        breakbtn.setText("\uf0bb / \uf0bc");
        breakbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height / 16));
        breakbtn.setTag(breakbtn.getText() + " " + mExecutor.getName());
        breakbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.BreakBack();
            }
        });

        gobtn = new Button(Context);
        gobtn.setBackgroundResource(R.drawable.btn_normal_selector);
        gobtn.setTextColor(getResources().getColor(R.color.dark_white_smoke));
        gobtn.setTextSize(18);
        gobtn.setTypeface(Typeface.createFromAsset(Context.getAssets(), "octicons.ttf"));
        gobtn.setText("\uf0bf");
        gobtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height / 8));
        gobtn.setTag(gobtn.getText() + " " + mExecutor.getName());
        gobtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.GO();
            }
        });

        stopbtn = new Button(Context);
        stopbtn.setBackgroundResource(R.drawable.btn_normal_selector);
        stopbtn.setTextColor(getResources().getColor(R.color.dark_white_smoke));
        stopbtn.setTypeface(Typeface.createFromAsset(Context.getAssets(), "octicons.ttf"));
        stopbtn.setText("\uf053");
        stopbtn.setTag(stopbtn.getText() + " " + mExecutor.getName());
        stopbtn.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                height / 16));
        stopbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.Stop();
            }
        });

        Resize();
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
                if(isPointInsideView(event.getX(), event.getY(), flashbtn)) {
                    ButtonDown();
                    return true;
                }
                if(isPointInsideView(event.getX(), event.getY(), stopbtn)) {
                    stopbtn.callOnClick();
                    return true;
                }
                if(isPointInsideView(event.getX(), event.getY(), gobtn)) {
                    gobtn.callOnClick();
                    return true;
                }
                if(isPointInsideView(event.getX(), event.getY(), breakbtn)) {
                    breakbtn.callOnClick();
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if(isPointInsideView(event.getX(), event.getY(), flashbtn)) {
                    ButtonUp();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        if((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        }
        else {
            return false;
        }
    }

    private void ButtonUp() {/**
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
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            boolean first = true;

            @Override
            public void run() {
                mExecutor.setFlash(false, false);
                if(!flashbtn.isPressed() && !first) {
                    lockflash = false;
                    this.cancel();
                }
                first = false;
            }
        }, 50, 50);
    }

    private void ButtonDown() {
        //flashbtn.setClickable(true);
        flashToggled = mExecutor.getToggle() ? !flashToggled : false;
        lockflash = true;
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
        @Override public void run() {
        if(!flashbtn.isPressed()) {
        lockflash=false;
        this.cancel();
        }
        }
        },250,250);**/
    }

    public void Resize() {
        int height = Context.getResources().getDisplayMetrics().heightPixels;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) faderLayout.getLayoutParams();

        params.height = (height / 2) + (height / 16);
        final int rotation = ((WindowManager) Context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch(rotation) {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                params.height = (height / 2) + (height / 28);
        }
        faderLayout.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) flashbtn.getLayoutParams();
        params.height = height / 8;
        flashbtn.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) breakbtn.getLayoutParams();
        params.height = height / 16;
        breakbtn.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) gobtn.getLayoutParams();
        params.height = height / 8;
        gobtn.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) stopbtn.getLayoutParams();
        params.height = height / 16;
        stopbtn.setLayoutParams(params);
    }

    @Override
    public Object getTag() {
        return mExecutor.getName();
    }

    public EntityExecutor getEntityExecutor() {
        return mExecutor;
    }
}
