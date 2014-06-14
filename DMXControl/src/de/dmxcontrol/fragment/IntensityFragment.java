/*
 * IntensityFragment.java
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.model.DimmerModel;
import de.dmxcontrol.model.ModelManager;
import de.dmxcontrol.model.ModelManager.Type;
import de.dmxcontrol.model.ShutterModel;
import de.dmxcontrol.model.StrobeModel;
import de.dmxcontrol.widget.FaderVerticalControl;

public class IntensityFragment extends BasePanelFragment {
    private final static String TAG = "intensityfragment";
    private ShutterModel mShutterModel;

    // startup process initiated
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        EntityManager entityManager = EntityManager.get();

        LinearLayout intensityLayout = (LinearLayout) inflater.inflate(
                R.layout.intensity_fragment, container, false);
        FaderVerticalControl faderDimmer = (FaderVerticalControl) intensityLayout
                .findViewById(R.id.fader_dimmer);
        DimmerModel dimmerModel = entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(
                ModelManager.Type.Dimmer);
        faderDimmer.setValueListener(dimmerModel);
        faderDimmer.setValue(dimmerModel.getWidgetValue(), 0f);

        Button bFaderHundred = (Button) intensityLayout
                .findViewById(R.id.button_dimmer_hundred);
        bFaderHundred.setOnClickListener(new OnPercentListener(1.0f,
                faderDimmer));

        Button bFaderSeventy = (Button) intensityLayout
                .findViewById(R.id.button_dimmer_seventy);
        bFaderSeventy.setOnClickListener(new OnPercentListener(0.7f,
                faderDimmer));

        Button bFaderFifty = (Button) intensityLayout
                .findViewById(R.id.button_dimmer_fifty);
        bFaderFifty
                .setOnClickListener(new OnPercentListener(0.5f, faderDimmer));

        Button bFaderThirty = (Button) intensityLayout
                .findViewById(R.id.button_dimmer_thirty);
        bFaderThirty
                .setOnClickListener(new OnPercentListener(0.3f, faderDimmer));

        Button bFaderZero = (Button) intensityLayout
                .findViewById(R.id.button_dimmer_zero);
        bFaderZero.setOnClickListener(new OnPercentListener(0.0f, faderDimmer));

        FaderVerticalControl faderStrobe = (FaderVerticalControl) intensityLayout
                .findViewById(R.id.fader_strobe);
        StrobeModel strobeModel = entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(Type.Strobe);
        faderStrobe.setValueListener(strobeModel);
        faderStrobe.setValue(strobeModel.getWidgetValue(), 0f);

        OnShutterListener osl = new OnShutterListener();
        Button buttonShutterOpen = (Button) intensityLayout
                .findViewById(R.id.button_shutter_open);
        buttonShutterOpen.setOnClickListener(osl);

        Button buttonShutterClose = (Button) intensityLayout
                .findViewById(R.id.button_shutter_close);
        buttonShutterClose.setOnClickListener(osl);

        mShutterModel = (ShutterModel) entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(Type.Shutter);

        OnLumosListener oll = new OnLumosListener(faderDimmer,
                buttonShutterOpen, buttonShutterClose);
        Button buttonIntensityLumos = (Button) intensityLayout
                .findViewById(R.id.button_intensity_lumos);
        buttonIntensityLumos.setOnClickListener(oll);
        Button buttonIntensityNox = (Button) intensityLayout
                .findViewById(R.id.button_intensity_nox);
        buttonIntensityNox.setOnClickListener(oll);

        if (!Prefs.get().getDisableAnimations())
            addFadeInAnimation(intensityLayout);

        return intensityLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    private class OnPercentListener implements OnClickListener {
        private float mPercent;
        private FaderVerticalControl mFaderDimmer;

        public OnPercentListener(float percent, FaderVerticalControl faderDimmer) {
            mPercent = percent;
            mFaderDimmer = faderDimmer;
        }

        @Override
        public void onClick(View v) {
            mFaderDimmer.setValue(mPercent, 0f);
            mFaderDimmer.notifyListener();
        }
    }

    private class OnShutterListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            int value;
            switch (id) {
                case R.id.button_shutter_open:
                    value = ShutterModel.SHUTTER_OPEN;
                    break;
                case R.id.button_shutter_close:
                    value = ShutterModel.SHUTTER_CLOSE;
                    break;
                default:
                    value = ShutterModel.SHUTTER_OPEN;
            }
            mShutterModel.setValue(value);
        }
    }

    private class OnLumosListener implements OnClickListener {
        private FaderVerticalControl mFaderDimmer;
        private Button mShutterOpen, mShutterClose;

        public OnLumosListener(FaderVerticalControl faderDimmer,
                               Button shutterOpen, Button shutterClose) {
            mFaderDimmer = faderDimmer;
            mShutterOpen = shutterOpen;
            mShutterClose = shutterClose;
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.button_intensity_lumos:
                    mFaderDimmer.setValue(1f, 0f);
                    mFaderDimmer.notifyListener();
                    mShutterOpen.performClick();
                    break;
                case R.id.button_intensity_nox:
                    mFaderDimmer.setValue(0f, 0f);
                    mFaderDimmer.notifyListener();
                    mShutterClose.performClick();
                    break;
                default:
            }

        }
    }
}
