/*
 * ControlActivity.java
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

package de.dmxcontrol.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mobileanarchy.android.widgets.dockpanel.DockPanel;
import com.mobileanarchy.android.widgets.dockpanel.OnDockOpenListener;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.fragment.ActionSelectorFragment;
import de.dmxcontrol.fragment.ActionSelectorFragment.OnUpdateActionView;
import de.dmxcontrol.fragment.ColorFragment;
import de.dmxcontrol.fragment.DeviceGroupFragment;
import de.dmxcontrol.fragment.EffectFragment;
import de.dmxcontrol.fragment.ErrorDialogFragment;
import de.dmxcontrol.fragment.GoboFragment;
import de.dmxcontrol.fragment.IntensityFragment;
import de.dmxcontrol.fragment.NetworkErrorDialogFragment;
import de.dmxcontrol.fragment.OnPanelResumedListener;
import de.dmxcontrol.fragment.OpticFragment;
import de.dmxcontrol.fragment.PanTiltFragment;
import de.dmxcontrol.fragment.PanelSelectorFragment;
import de.dmxcontrol.fragment.PresetFragment;
import de.dmxcontrol.fragment.PrismFragment;
import de.dmxcontrol.fragment.ProgrammerFragment;
import de.dmxcontrol.fragment.RawFragment;
import de.dmxcontrol.network.IMessageListener;
import de.dmxcontrol.network.ServiceFrontend;
import de.dmxcontrol.network.ServiceFrontend.OnServiceListener;

public class ControlActivity extends FragmentActivity implements
        OnUpdateActionView,
        GestureDetector.OnGestureListener,
        OnPanelResumedListener {
    public final static String TAG = "controlactivity";
    private float SWIPE_MIN_VELOCITY;
    private float SWIPE_MIN_DISTANCE;

    private MessageListener mMessageListener = new MessageListener();
    private UpdatePanel mUpdatePanel;
    private AboutDialogs ad;

    private final static int DIALOG_SPLASH = 101;
    private final static int DIALOG_SPLASH_DELAY = 2000;
    private static boolean DISABLE_SPLASH = false;
    private static int SCREEN_MODE;
    private int oldState = -1;

    private FragmentTransaction transaction;

    private GestureDetector gestureDetector;
    private EdgeEffect edgeEffect;

    private FragmentManager fManager = getSupportFragmentManager();
    private DeviceGroupFragment deviceGroupFragment = new DeviceGroupFragment();
    private IntensityFragment intensityFragment = new IntensityFragment();
    private ColorFragment colorFragment = new ColorFragment();
    private PanTiltFragment panTiltFragment = new PanTiltFragment();
    private GoboFragment goboFragment = new GoboFragment();
    private OpticFragment opticFragment = new OpticFragment();
    private PrismFragment prismFragment = new PrismFragment();
    private RawFragment rawFragment = new RawFragment();
    private EffectFragment effectFragment = new EffectFragment();
    private PresetFragment presetFragment = new PresetFragment();
    private ProgrammerFragment programmerFragment = new ProgrammerFragment();

    private boolean isInForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        SWIPE_MIN_VELOCITY = this.getResources().getInteger(R.integer.swipe_min_velocity);
        SWIPE_MIN_DISTANCE = this.getResources().getInteger(R.integer.swipe_min_distance);
        //Change the EdgeEffectColor to our HighlightColor
        int glowDrawableId = this.getResources().getIdentifier("overscroll_glow", "drawable", "android");
        Drawable androidGlow = this.getResources().getDrawable(glowDrawableId);
        androidGlow.setColorFilter(this.getResources().getColor(R.color.btn_background_highlight), PorterDuff.Mode.SRC_IN);

        int edgeDrawableId = this.getResources().getIdentifier("overscroll_edge", "drawable", "android");
        Drawable androidEdge = this.getResources().getDrawable(edgeDrawableId);
        androidEdge.setColorFilter(this.getResources().getColor(R.color.btn_background_highlight), PorterDuff.Mode.SRC_IN);

        int scrollBarHDrawableId = this.getResources().getIdentifier("scrollbar_handle_horizontal", "drawable", "android");
        Drawable androidscrollBarH = this.getResources().getDrawable(scrollBarHDrawableId);
        androidscrollBarH.setColorFilter(this.getResources().getColor(R.color.btn_background_highlight), PorterDuff.Mode.SRC_IN);

        int scrollBarVDrawableId = this.getResources().getIdentifier("scrollbar_handle_vertical", "drawable", "android");
        Drawable androidscrollBarV = this.getResources().getDrawable(scrollBarVDrawableId);
        androidscrollBarV.setColorFilter(this.getResources().getColor(R.color.btn_background_highlight), PorterDuff.Mode.SRC_IN);


        setContentView(R.layout.root_screen_with_selector_drawer);
        gestureDetector = new GestureDetector(this, this);

        edgeEffect = new EdgeEffect(this);

        if(((DMXControlApplication) getApplication()).getJustStarted() && !DISABLE_SPLASH) {
            showDialog(DIALOG_SPLASH);
        }

        ad = new AboutDialogs(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        switch(SCREEN_MODE) {
            case Prefs.SCREEN_MODE_AUTOMATIC:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            case Prefs.SCREEN_MODE_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Prefs.SCREEN_MODE_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        ServiceFrontend.get().setNetworkListener(mMessageListener);
        mUpdatePanel = new UpdatePanel();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForeground = true;

        // Update connection icon here

        // Check if we have to connect or reconnect network service
        Prefs prefs = Prefs.get();

        boolean networkChanged = prefs.connectConfigChanged();

        ServiceFrontend cs = ServiceFrontend.get();

        if(networkChanged && prefs.getOffline()) {
            cs.disconnect(true);
        }
        else if(networkChanged && !prefs.getOffline()) {
            cs.connect();
        }

        /*
        if(mUpdatePanel != null) {
            mUpdatePanel.onServiceChanged(cs);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!isPointInsideView(event.getX(), event.getY(), fManager.findFragmentById(R.id.action_fragment).getView())) {
            if(event.getPointerCount() == 2 && oldState == ActionSelectorFragment.STATE_OPTIC_PANEL) {
                return super.dispatchTouchEvent(event);
            }
            else {

                gestureDetector.onTouchEvent(event);
            }
            switch(oldState) {
                case ActionSelectorFragment.STATE_INTENSITY_PANEL:
                case ActionSelectorFragment.STATE_COLOR_PANEL:
                case ActionSelectorFragment.STATE_PANTILT_PANEL:
                    if(!(event.getAction() == MotionEvent.ACTION_MOVE) || event.getEventTime() - event.getDownTime() > this.getResources().getInteger(R.integer.touch_gesture_delay)) {
                        return super.dispatchTouchEvent(event);
                    }
                    return false;
                default:
                    return super.dispatchTouchEvent(event);
            }
        }
        return super.dispatchTouchEvent(event);
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

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Get Position
        if(e1 == null || e2 == null) {
            return false;
        }
        float ev1X = e1.getX();
        float ev2X = e2.getX();

        //Get distance of X (e1) to X (e2)
        final float xdistance = Math.abs(ev1X - ev2X);
        //Get velocity of cursor
        final float xvelocity = Math.abs(velocityX);

        if((xvelocity > SWIPE_MIN_VELOCITY) && (xdistance > SWIPE_MIN_DISTANCE)) {
            if(ev1X > ev2X)//Switch Left
            {
                //Change Stata, if you add a new Fragment
                if(oldState < ActionSelectorFragment.STATE_PROGRAMMER_PANEL) {
                    Log.d("Slide", "SWING_LEFT_EVENT");
                    onUpdateActionView(true, oldState + 1);
                }
            }
            else//Switch Right
            {
                if(oldState > ActionSelectorFragment.STATE_DEVICE_PANEL) {
                    Log.d("Slide", "SWING_RIGHT_EVENT");
                    onUpdateActionView(true, oldState - 1);
                }
            }
        }

        return false;
    }

	/*
     *
	 * Base Elements of the Activity
	 *
	 * Panel SelectorButtons UpdateDeviceGroupText ActionButtons
	 */

    class UpdatePanel implements OnDockOpenListener, OnServiceListener {
        private ImageView mConnectionImage;

        public UpdatePanel() {
            FrameLayout frame = (FrameLayout) findViewById(R.id.root);
            DockPanel panel = (DockPanel) frame.findViewById(R.id.panel);
            panel.setOnDockOpenListener(this);

            mConnectionImage = panel.getImageView();
            mConnectionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(liveActivity == null) {
                        liveActivity = new Intent(getApplicationContext(), ServerConnection.class);
                    }
                    startActivity(liveActivity);
                }
            });

            // get service
            ServiceFrontend s = ServiceFrontend.get();

            // Init icons.
            if(s.isConnected()) {
                onServiceConnected();
            }
            else {
                onServiceDisconnected();
            }

            // Add listener to service so we get updates in future
            s.addServiceListener(this);
        }

        @Override
        public void onDockOpenListener(ViewGroup viewGroup) {
            PanelSelectorFragment fragment = (PanelSelectorFragment) fManager.findFragmentById(R.id.panel_fragment);
            if(fragment == null) {
                return;
            }

            fragment.updateActiveInputType();

        }

        @Override
        public void onServiceConnected() {
            ControlActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mConnectionImage.setImageResource(R.drawable.device_connected);
                }
            });
        }

        @Override
        public void onServiceDisconnected() {
            ControlActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mConnectionImage.setImageResource(R.drawable.device_not_connected);
                }
            });
        }
    }

    @Override
    public void onUpdateActionView(int state) {
        onUpdateActionView(false, state);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount() == 2 && oldState == ActionSelectorFragment.STATE_OPTIC_PANEL) {
            return false;
        }
        else {
            return gestureDetector.onTouchEvent(event);
        }
    }

    @Override
    public void onBackPressed() {
        if(oldState != ActionSelectorFragment.STATE_DEVICE_PANEL) {
            onUpdateActionView(true, ActionSelectorFragment.STATE_DEVICE_PANEL);
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.about_title)
                    .setIcon(R.drawable.androidmann_neu)
                    .setTitle(getString(R.string.ask_close))
                    .setMessage(getString(R.string.ask_close_app))
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Process.killProcess(Process.myPid());
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            alertDialog.show();
        }
    }

    private void close() {
        super.onBackPressed();
    }


    public void onUpdateActionView(boolean animation, int state) {
        Log.d(TAG, "onUpdateActionView state = " + state);
        Fragment newFragment;

        transaction = fManager.beginTransaction();
        if(animation) {
            ((ActionSelectorFragment) fManager.findFragmentById(R.id.action_fragment)).updateStateSelected(state);
        }

        if(Math.abs(oldState - state) == 1 && animation) {
            if(oldState < state) {
                transaction.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
            else if(oldState > state) {
                transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            }
        }
        if(state == oldState) {
            return;
        }
        else {
            oldState = state;
        }
        switch(state) {
            case ActionSelectorFragment.STATE_DEVICE_PANEL:
                newFragment = deviceGroupFragment;
                break;
            case ActionSelectorFragment.STATE_INTENSITY_PANEL:
                newFragment = intensityFragment;
                break;
            case ActionSelectorFragment.STATE_COLOR_PANEL:
                newFragment = colorFragment;
                break;
            case ActionSelectorFragment.STATE_PANTILT_PANEL:
                newFragment = panTiltFragment;
                break;
            case ActionSelectorFragment.STATE_GOBO_PANEL:
                newFragment = goboFragment;
                break;
            case ActionSelectorFragment.STATE_OPTIC_PANEL:
                newFragment = opticFragment;
                break;
            case ActionSelectorFragment.STATE_PRISM_PANEL:
                newFragment = prismFragment;
                break;
            case ActionSelectorFragment.STATE_RAW_PANEL:
                newFragment = rawFragment;
                break;
            case ActionSelectorFragment.STATE_EFFECT_PANEL:
                newFragment = effectFragment;
                break;
            case ActionSelectorFragment.STATE_PRESET_PANEL:
                newFragment = presetFragment;
                break;
            case ActionSelectorFragment.STATE_PROGRAMMER_PANEL:
                newFragment = programmerFragment;
                break;
            default:
                return; // dont do anything without a new fragment
        }
        transaction.replace(R.id.action_screen, newFragment);
        transaction.commit();
    }

    @Override
    public void onPanelResumed() {
        // FragmentManager fManager = getSupportFragmentManager();
        ActionSelectorFragment asf = (ActionSelectorFragment) fManager.findFragmentById(R.id.action_fragment);
        if(asf != null) {
            asf.updateStateSelected();
        }

    }

	/*
     *
	 * Dialogs and Menus
	 */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    private Intent liveActivity;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch(item.getItemId()) {

            case R.id.preferences:
                i = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.connection:
                try {
                    if(liveActivity == null) {
                        liveActivity = new Intent(getApplicationContext(), ServerConnection.class);
                    }
                    startActivity(liveActivity);
                }
                catch(Exception e) {
                    Log.e("Can't open ConnectionDialog", e.toString());
                    DMXControlApplication.SaveLog();
                }
                return true;
            case R.id.live:
                try {
                    i = new Intent(getApplicationContext(), LiveActivity.class);
                    startActivity(i);
                }
                catch(Exception e) {
                    Log.e("Can't open Live", e.toString());
                    DMXControlApplication.SaveLog();
                }
                return true;
            case R.id.help:
                i = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(i);
                return true;
            case R.id.about:
                showDialog(AboutDialogs.ABOUT_DIALOG_MAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch(id) {
            case DIALOG_SPLASH:
                return createSplashDialog();
            default:
                return ad.onCreateDialog(id);
        }
    }

    public static void setDisableSplash(boolean disableSplash) {
        DISABLE_SPLASH = disableSplash;
    }

    public static void setScreenMode(int screenMode) {
        SCREEN_MODE = screenMode;
    }

    private Dialog createSplashDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_splash);
        ImageView image = (ImageView) dialog.findViewById(R.id.image_splash);
        image.setImageResource(R.drawable.image_splash);

        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        animation.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
        set.addAnimation(animation);
        animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(1000);
        animation.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
        set.addAnimation(animation);

        image.setAnimation(set);
        dismissSplashDelayed();
        return dialog;
    }

    public void dismissSplashDelayed() {
        Handler h = new Handler();

        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    ControlActivity.this.removeDialog(DIALOG_SPLASH);
                }
                catch(IllegalArgumentException e) {
                    // We ignore this here - race condition error
                }
            }

        }, DIALOG_SPLASH_DELAY);

    }


    public void showErrorDialog(String msg) {
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(msg);

        if(errorDialog == null) {
            return;
        }

        if(!isInForeground) {
            return;
        }

        errorDialog.show(fm, ErrorDialogFragment.TAG);
    }

    public void showNetworkErrorDialog(String msg) {
        FragmentManager fm = getSupportFragmentManager();
        NetworkErrorDialogFragment errorDialog = NetworkErrorDialogFragment.newInstance(msg);

        if(errorDialog == null) {
            return;
        }

        if(!isInForeground) {
            return;
        }

        errorDialog.show(fm, NetworkErrorDialogFragment.TAG);
    }

    class MessageListener implements IMessageListener {

        @Override
        public void notifyNetworkError(final String msg) {
            ControlActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showNetworkErrorDialog(msg);
                }
            });
        }

        @Override
        public void notifyError(final String msg) {
            ControlActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showErrorDialog(msg);
                }
            });

        }
    }
}
