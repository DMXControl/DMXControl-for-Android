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

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mobileanarchy.android.widgets.dockpanel.DockPanel;
import com.mobileanarchy.android.widgets.dockpanel.OnDockOpenListener;

import java.sql.Connection;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.fragment.ActionSelectorFragment;
import de.dmxcontrol.fragment.ActionSelectorFragment.OnUpdateActionView;
import de.dmxcontrol.fragment.ColorFragment;
import de.dmxcontrol.fragment.DeviceGroupFragment;
import de.dmxcontrol.fragment.ErrorDialogFragment;
import de.dmxcontrol.fragment.IntensityFragment;
import de.dmxcontrol.fragment.NetworkErrorDialogFragment;
import de.dmxcontrol.fragment.OnPanelResumedListener;
import de.dmxcontrol.fragment.PanTiltFragment;
import de.dmxcontrol.fragment.PanelSelectorFragment;
import de.dmxcontrol.network.IMessageListener;
import de.dmxcontrol.network.ServiceFrontend;
import de.dmxcontrol.network.ServiceFrontend.OnServiceListener;

public class ControlActivity extends FragmentActivity implements
        OnUpdateActionView, OnPanelResumedListener {
    public final static String TAG = "controlactivity";

    private MessageListener mMessageListener = new MessageListener();
    private UpdatePanel mUpdatePanel;
    private AboutDialogs ad;

    private final static int DIALOG_SPLASH = 101;
    private final static int DIALOG_SPLASH_DELAY = 1500;
    private static boolean DISABLE_SPLASH = false;
    private static int SCREEN_MODE;
    private int oldState = -1;

    private boolean isInForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_screen_with_selector_drawer);

        if (((DMXControlApplication) getApplication()).getJustStarted()
                && !DISABLE_SPLASH) {
            showDialog(DIALOG_SPLASH);
        }

        ad = new AboutDialogs(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        switch (SCREEN_MODE) {
            case Prefs.SCREEN_MODE_AUTOMATIC:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            case Prefs.SCREEN_MODE_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Prefs.SCREEN_MODE_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        ServiceFrontend.get().setListener(mMessageListener);
        mUpdatePanel = new UpdatePanel();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForeground = true;
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

            mConnectionImage = (ImageView) panel
                    .findViewById(DockPanel.IMAGEVIEW_ID);
            ServiceFrontend s = ServiceFrontend.get();
            s.addListener(this);
            onServiceChanged(s);

        }

        @Override
        public void onDockOpenListener(ViewGroup viewGroup) {
            PanelSelectorFragment fragment = (PanelSelectorFragment) ControlActivity.this
                    .getSupportFragmentManager().findFragmentById(
                            R.id.panel_fragment);
            if (fragment == null)
                return;

            fragment.updateActiveInputType();

        }

        @Override
        public void onServiceChanged(ServiceFrontend cs) {
            if (cs.isConnected())
                mConnectionImage.setImageResource(R.drawable.device_connected);
            else
                mConnectionImage
                        .setImageResource(R.drawable.device_not_connected);
        }
    }

    @Override
    public void onUpdateActionView(int state) {
        Log.d(TAG, "onUpdateActionView state = " + state);

        if (state == oldState)
            return;

        Fragment newFragment;

        switch (state) {
            case ActionSelectorFragment.STATE_DEVICE_PANEL:
                newFragment = new DeviceGroupFragment();
                break;
            case ActionSelectorFragment.STATE_INTENSITY_PANEL:
                newFragment = new IntensityFragment();
                break;
            case ActionSelectorFragment.STATE_COLOR_PANEL:
                newFragment = new ColorFragment();
                break;
            case ActionSelectorFragment.STATE_PANTILT_PANEL:
                newFragment = new PanTiltFragment();
                break;
            default:
                return; // dont do anything without a new fragment
        }

        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction transaction = fManager.beginTransaction();

        transaction.replace(R.id.action_screen, newFragment);
        // transaction.addToBackStack(null);
        transaction.commit();

        oldState = state;

    }

    @Override
    public void onPanelResumed() {
        FragmentManager fManager = getSupportFragmentManager();
        ActionSelectorFragment asf = (ActionSelectorFragment) fManager
                .findFragmentById(R.id.action_fragment);
        if (asf != null)
            asf.updateStateSelected();

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
        switch (item.getItemId()) {

            case R.id.preferences:
                i = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.connection:
                try {
                    if(liveActivity==null) {
                        liveActivity = new Intent(getApplicationContext(), ServerConnection.class);
                    }
                    startActivity(liveActivity);
                } catch (Exception e) {
                    Log.e("Open ConnectionDialog", e.toString());
                }
                return true;
            case R.id.live:
                try {
                    i = new Intent(getApplicationContext(), LiveActivity.class);
                    startActivity(i);
                } catch (Exception e) {
                    Log.e("Open ConnectionDialog", e.toString());
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
        switch (id) {
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
        Dialog dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_splash);
        ImageView image = (ImageView) dialog.findViewById(R.id.image_splash);
        image.setImageResource(R.drawable.image_splash);

        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        animation.setInterpolator(AnimationUtils.loadInterpolator(this,
                android.R.anim.accelerate_decelerate_interpolator));
        set.addAnimation(animation);
        animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(500);
        animation.setInterpolator(AnimationUtils.loadInterpolator(this,
                android.R.anim.accelerate_decelerate_interpolator));
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
                } catch (IllegalArgumentException e) {
                    // We ignore this here - race condition error
                }
            }

        }, DIALOG_SPLASH_DELAY);

    }

    public void showErrorDialog(String msg) {
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(msg);
        if (errorDialog == null)
            return;
        if (!isInForeground)
            return;

        errorDialog.show(fm, ErrorDialogFragment.TAG);
    }

    public void showNetworkErrorDialog(String msg) {
        FragmentManager fm = getSupportFragmentManager();
        NetworkErrorDialogFragment errorDialog = NetworkErrorDialogFragment
                .newInstance(msg);
        if (errorDialog == null)
            return;
        if (!isInForeground)
            return;

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

        @Override
        public void notifyInterrupted() {

        }

    }
}
