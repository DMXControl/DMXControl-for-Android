package com.mobileanarchy.android.widgets.dockpanel;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;

public class DockPanel extends LinearLayout {

    // =========================================
    // Private members
    // =========================================

    private static final String TAG = "DockPanel";
    private DockPosition position;
    private int contentLayoutId;
    private int handleToggleButtonDrawableId;
    private int handleMenuButtonDrawableId;
    private Boolean isOpen;
    private Boolean animationRunning;
    private FrameLayout contentPlaceHolder;
    private ImageButton toggleButton;
    private ImageButton menuButton;
    private int animationDuration;
    private Context ctx;

    public ImageView getImageView() {
        return imageView;
    }

    public final static String IMAGEVIEW_TAG = "connectionImage";

    // Hacked mod for DMXControl
    private OnDockOpenListener onDockOpenListener;
    private ImageView imageView;

    // =========================================
    // Constructors
    // =========================================

    public DockPanel(Context context, int contentLayoutId,
                     int handleButtonDrawableId, Boolean isOpen) {
        super(context);
        this.ctx = context;
        this.contentLayoutId = contentLayoutId;
        this.handleToggleButtonDrawableId = handleButtonDrawableId;
        this.handleMenuButtonDrawableId = handleButtonDrawableId;
        this.isOpen = isOpen;

        Init(null);
    }

    public DockPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        // to prevent from crashing the designer
        try {
            Init(attrs);
        }
        catch(Exception e) {
            Log.w("", e.getStackTrace().toString());
            DMXControlApplication.SaveLog();
        }
    }

    // =========================================
    // Initialization
    // =========================================

    private void Init(AttributeSet attrs) {
        setDefaultValues(attrs);

        createHandleToggleButton();
        createHandleMenuButton();
        createImageView();

        // create the handle container
        FrameLayout handleContainer = new FrameLayout(getContext());
        handleContainer.addView(toggleButton);
        if(!ViewConfiguration.get(getContext()).hasPermanentMenuKey()) {
            handleContainer.addView(menuButton);
        }
        handleContainer.addView(imageView);

        // create and populate the panel's container, and inflate it
        contentPlaceHolder = new FrameLayout(getContext());
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                infService);
        li.inflate(contentLayoutId, contentPlaceHolder, true);

        // setting the layout of the panel parameters according to the docking
        // position
        if(position == DockPosition.LEFT || position == DockPosition.RIGHT) {
            handleContainer.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.FILL_PARENT, 1));
            contentPlaceHolder.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.FILL_PARENT, 1));
        }
        else {
            handleContainer.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            contentPlaceHolder.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }

        // adding the view to the parent layout according to docking position
        if(position == DockPosition.RIGHT || position == DockPosition.BOTTOM) {
            this.addView(handleContainer);
            this.addView(contentPlaceHolder);
        }
        else {
            this.addView(contentPlaceHolder);
            this.addView(handleContainer);
        }

        if(!isOpen) {
            contentPlaceHolder.setVisibility(View.GONE);
        }
    }

    private void setDefaultValues(AttributeSet attrs) {
        // set default values
        isOpen = true;
        animationRunning = false;
        animationDuration = 500;
        setPosition(DockPosition.RIGHT);

        // Try to load values set by xml markup
        if(attrs != null) {
            String namespace = "http://com.MobileAnarchy.Android.Widgets";

            animationDuration = attrs.getAttributeIntValue(namespace,
                    "animationDuration", 500);
            contentLayoutId = attrs.getAttributeResourceValue(namespace,
                    "contentLayoutId", 0);
            handleToggleButtonDrawableId = attrs.getAttributeResourceValue(
                    namespace, "handleToggleButtonDrawableResourceId", 0);
            handleMenuButtonDrawableId = attrs.getAttributeResourceValue(
                    namespace, "handleMenuButtonDrawableResourceId", 0);
            isOpen = attrs.getAttributeBooleanValue(namespace, "isOpen", true);

            // Enums are a bit trickier (needs to be parsed)
            try {
                position = DockPosition.valueOf(DockPosition.class, attrs.getAttributeValue(
                        namespace, "dockPosition").toUpperCase());
                setPosition(position);
            }
            catch(Exception e) {
                Log.w("", DMXControlApplication.stackTraceToString(e));
                DMXControlApplication.SaveLog();
                // Docking to the left is the default behavior
                setPosition(DockPosition.LEFT);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("superState"));
        boolean isOpenSaved = bundle.getBoolean(DockPanel.class.getSimpleName() + ".open");

        int animationDurationSaved = getAnimationDuration();
        setAnimationDuration(0);
        if(isOpenSaved && !isOpen) {
            open();
        }
        else if(!isOpenSaved && isOpen) {
            close();
        }
        setAnimationDuration(animationDurationSaved);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", superState);
        bundle.putBoolean(DockPanel.class.getSimpleName() + ".open", getIsOpen());

        return bundle;
    }

    public void setOnDockOpenListener(OnDockOpenListener listener) {
        onDockOpenListener = listener;
    }


    private void createHandleToggleButton() {
        toggleButton = new ImageButton(getContext());
        toggleButton.setPadding(0, 0, 0, 0);
        toggleButton.setLayoutParams(new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        toggleButton.setBackgroundColor(Color.TRANSPARENT);
        toggleButton.setImageResource(handleToggleButtonDrawableId);
        toggleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    private void createHandleMenuButton() {
        menuButton = new ImageButton(getContext());
        menuButton.setPadding(0, 0, 0, 0);
        menuButton.setLayoutParams(new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT));
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setImageResource(handleMenuButtonDrawableId);
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) getContext();
                activity.openOptionsMenu();
            }
        });
    }

    private void createImageView() {
        imageView = new ImageView(getContext());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.RIGHT));
        imageView.setTag(IMAGEVIEW_TAG);
    }

    private void setPosition(DockPosition position) {
        this.position = position;
        switch(position) {
            case TOP:
                setOrientation(LinearLayout.VERTICAL);
                setGravity(Gravity.TOP);
                break;
            case RIGHT:
                setOrientation(LinearLayout.HORIZONTAL);
                setGravity(Gravity.RIGHT);
                break;
            case BOTTOM:
                setOrientation(LinearLayout.VERTICAL);
                setGravity(Gravity.BOTTOM);
                break;
            case LEFT:
                setOrientation(LinearLayout.HORIZONTAL);
                setGravity(Gravity.LEFT);
                break;
        }
    }

    // =========================================
    // Public methods
    // =========================================

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int milliseconds) {
        animationDuration = milliseconds;
    }

    public Boolean getIsRunning() {
        return animationRunning;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void open() {
        if(!animationRunning) {
            Log.d(TAG, "Opening...");

            Animation animation = createShowAnimation();
            this.setAnimation(animation);
            animation.start();

            isOpen = true;
        }
    }

    public void close() {
        if(!animationRunning) {
            Log.d(TAG, "Closing...");

            Animation animation = createHideAnimation();
            this.setAnimation(animation);
            animation.start();
            isOpen = false;
        }
    }

    public void toggle() {
        if(isOpen) {
            close();
        }
        else {
            open();
        }
    }

    // =========================================
    // Private methods
    // =========================================

    private Animation createHideAnimation() {
        Animation animation = null;
        switch(position) {
            case TOP:
                animation = new TranslateAnimation(0, 0, 0, -contentPlaceHolder
                        .getHeight());
                break;
            case RIGHT:
                animation = new TranslateAnimation(0, contentPlaceHolder
                        .getWidth(), 0, 0);
                break;
            case BOTTOM:
                animation = new TranslateAnimation(0, 0, 0, contentPlaceHolder
                        .getHeight());
                break;
            case LEFT:
                animation = new TranslateAnimation(0, -contentPlaceHolder
                        .getWidth(), 0, 0);
                break;
        }

        animation.setDuration(animationDuration);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationRunning = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contentPlaceHolder.setVisibility(View.GONE);
                animationRunning = false;
            }
        });
        return animation;
    }

    private Animation createShowAnimation() {
        Animation animation = null;
        switch(position) {
            case TOP:
                animation = new TranslateAnimation(0, 0, -contentPlaceHolder
                        .getHeight(), 0);
                break;
            case RIGHT:
                animation = new TranslateAnimation(contentPlaceHolder.getWidth(),
                        0, 0, 0);
                break;
            case BOTTOM:
                animation = new TranslateAnimation(0, 0, contentPlaceHolder
                        .getHeight(), 0);
                break;
            case LEFT:
                animation = new TranslateAnimation(-contentPlaceHolder.getWidth(),
                        0, 0, 0);
                break;
        }
        Log.d(TAG, "Animation duration: " + animationDuration);
        animation.setDuration(animationDuration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationRunning = true;
                contentPlaceHolder.setVisibility(View.VISIBLE);
                Log.d(TAG, "\"Show\" Animation started");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationRunning = false;
                if(onDockOpenListener != null) {
                    onDockOpenListener.onDockOpenListener(DockPanel.this);
                }
                Log.d(TAG, "\"Show\" Animation ended");
            }
        });
        return animation;
    }

}
