/*
 * ActionFragment.java
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

package de.dmxcontrol.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.compatibility.CompatibilityWrapper8;

public class ActionSelectorFragment extends Fragment implements OnClickListener {
    private final static String TAG = "actionfragment";

    private Button crrActionButton;
    private OnUpdateActionView updateActionViewListener;
    private CompatibilityWrapper8 compat8;
    private Button
            bDeviceAction,
            bColorAction,
            bIntensityAction,
            bPanTiltAction,
            bOpticAction,
            bRawAction,
            bEffectAction;

    private final static String EXTRA_PANEL_STATE = "de.dmxcontrol.PANEL_STATE";
    public final static int STATE_DEVICE_PANEL = 0;
    public final static int STATE_INTENSITY_PANEL = 1;
    public final static int STATE_COLOR_PANEL = 2;
    public final static int STATE_PANTILT_PANEL = 3;
    public final static int STATE_GOBO_PANEL = 4;
    public final static int STATE_OPTIC_PANEL = 5;
    public final static int STATE_RAW_PANEL = 6;
    public final static int STATE_EFFECT_PANEL = 20;
    public int mState = STATE_DEVICE_PANEL;

    // startup process initiated
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        updateActionViewListener = (OnUpdateActionView) activity;
        compat8 = CompatibilityWrapper8.wrap(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mState = savedInstanceState.getInt(EXTRA_PANEL_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        LinearLayout actionButtons = (LinearLayout) inflater.inflate(
                R.layout.action_selector_fragment, container, false);
        ViewGroup vg = (ViewGroup) actionButtons
                .findViewById(R.id.action_selector_scroll);
        if(!Prefs.get().getDisableAnimations()) {
            addBounceInAnimation(vg);
        }

        bDeviceAction = (Button) actionButtons
                .findViewById(R.id.button_device_action);
        bDeviceAction.setOnClickListener(this);

        bColorAction = (Button) actionButtons
                .findViewById(R.id.button_color_action);
        bColorAction.setOnClickListener(this);

        bIntensityAction = (Button) actionButtons
                .findViewById(R.id.button_intensity_action);
        bIntensityAction.setOnClickListener(this);

        bPanTiltAction = (Button) actionButtons
                .findViewById(R.id.button_pantilt_action);
        bPanTiltAction.setOnClickListener(this);

        bOpticAction = (Button) actionButtons
                .findViewById(R.id.button_optic_action);
        bOpticAction.setOnClickListener(this);
        bOpticAction.setVisibility(View.INVISIBLE);
        actionButtons.removeView(bOpticAction);

        bRawAction = (Button) actionButtons
                .findViewById(R.id.button_raw_action);
        bRawAction.setOnClickListener(this);
        bRawAction.setVisibility(View.INVISIBLE);
        actionButtons.removeView(bRawAction);

        bEffectAction = (Button) actionButtons
                .findViewById(R.id.button_effect_action);
        bEffectAction.setOnClickListener(this);
        bEffectAction.setVisibility(View.INVISIBLE);
        actionButtons.removeView(bEffectAction);

        updateStateSelected();
        return actionButtons;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateActionViewListener.onUpdateActionView(mState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // startup process done

    // teardown process initiated
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // teardown process done

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_PANEL_STATE, mState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.button_device_action:
                mState = STATE_DEVICE_PANEL;
                break;
            case R.id.button_color_action:
                mState = STATE_COLOR_PANEL;
                break;
            case R.id.button_intensity_action:
                mState = STATE_INTENSITY_PANEL;
                break;
            case R.id.button_pantilt_action:
                mState = STATE_PANTILT_PANEL;
                break;
            case R.id.button_optic_action:
                mState = STATE_OPTIC_PANEL;
                break;
            case R.id.button_raw_action:
                mState = STATE_RAW_PANEL;
                break;
            case R.id.button_effect_action:
                mState = STATE_EFFECT_PANEL;
                break;
            default:
                return;
        }

        crrActionButton.setSelected(false);
        crrActionButton = (Button) view;
        crrActionButton.setSelected(true);

        if(updateActionViewListener != null) {
            updateActionViewListener.onUpdateActionView(mState);
        }
    }

    public void updateStateSelected() {

        if(crrActionButton != null) {
            crrActionButton.setSelected(false);
        }

        switch(mState) {
            case STATE_DEVICE_PANEL:
                crrActionButton = bDeviceAction;
                break;
            case STATE_INTENSITY_PANEL:
                crrActionButton = bIntensityAction;
                break;
            case STATE_COLOR_PANEL:
                crrActionButton = bColorAction;
                break;
            case STATE_PANTILT_PANEL:
                crrActionButton = bPanTiltAction;
                break;
            case STATE_OPTIC_PANEL:
                crrActionButton = bPanTiltAction;
                break;
            case STATE_RAW_PANEL:
                crrActionButton = bPanTiltAction;
                break;
            case STATE_EFFECT_PANEL:
                crrActionButton = bPanTiltAction;
                break;
            default:
                crrActionButton = bDeviceAction;
        }

        crrActionButton.setSelected(true);
    }

    private void addBounceInAnimation(ViewGroup target) {
        AnimationSet set = new AnimationSet(true);
        Animation animation;

        if(compat8.isDisplayPortrait()) {
            // Portrait
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                    1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);

        }
        else {
            // Landscape
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                    0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
        }

        animation.setDuration(750);
        animation.setInterpolator(AnimationUtils.loadInterpolator(
                getActivity(), android.R.anim.decelerate_interpolator));
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.0f);
        target.setLayoutAnimation(controller);
    }

    public static interface OnUpdateActionView {
        public void onUpdateActionView(int state);
    }

}
