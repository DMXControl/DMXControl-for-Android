/*
 * PanTiltFragment.java
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import de.dmxcontrol.activity.ControlActivity;
import de.dmxcontrol.android.R;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.model.ModelManager.Type;
import de.dmxcontrol.model.PositionModel;
import de.dmxcontrol.widget.CrossControl;
import de.dmxcontrol.widget.FaderVerticalControl;
import de.dmxcontrol.widget.IValueListener;
import de.dmxcontrol.widget.RelativeLayoutWithMultitouch;

public class PanTiltFragment extends BasePanelFragment implements
        OnClickListener {
    public final static String TAG = "pantiltfragment";

    private RelativeLayoutWithMultitouch softpultLayout;
    private CrossControl cc;
    private FaderVerticalControl fader;
    private boolean lockedY = false;
    private boolean lockedX = false;
    Button modePlain, modeFollow, modeSensor;
    Button modeCrrSelected;
    Button lockX, lockY;

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

        softpultLayout = (RelativeLayoutWithMultitouch) inflater.inflate(
                R.layout.pantilt_fragment, container, false);

        softpultLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        Log.d(ControlActivity.TAG, "onGlobalLayout called");
                        if(cc == null || fader == null) {
                            return;
                        }

                        if(cc.getMode() == CrossControl.MODE_POINTER_PLAIN) {
                            fader.setVisibility(View.GONE);
                        }
                        else {
                            fader.setVisibility(View.VISIBLE);
                        }
                        fader.setValue(cc.getSpeed() / 100.0f, 0);
                        setCrrSelected();
                        setEnableLock();

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            softpultLayout.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                        else {

                            //noinspection deprecation
                            softpultLayout.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                }
        );

        EntityManager entityManager = EntityManager.get();
        cc = (CrossControl) softpultLayout.findViewById(R.id.crosscontrol);
        PositionModel positionModel = entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(Type.Position);
        cc.setValueListener(positionModel);
        Float[] position = positionModel.getWidgetValue();
        cc.setValue(position[0], position[1]);

        fader = (FaderVerticalControl) softpultLayout
                .findViewById(R.id.speedfader);

        fader.setValueListener(new IValueListener() {

            @Override
            public void onValueChanged(View v, float x, float y) {
                cc.setSpeed((int) (x * 100));
            }
        });
        Log.d(ControlActivity.TAG, " cc.getSpeed = " + cc.getSpeed());
        fader.setValue(cc.getSpeed() / 100.0f, 0);

        modePlain = (Button) softpultLayout.findViewById(R.id.mode_plain);
        modePlain.setOnClickListener(this);

        modeFollow = (Button) softpultLayout.findViewById(R.id.mode_follow);
        modeFollow.setOnClickListener(this);

        modeSensor = (Button) softpultLayout.findViewById(R.id.mode_sensor);
        modeSensor.setOnClickListener(this);

        lockX = (Button) softpultLayout.findViewById(R.id.lockxdirection);
        lockX.setOnClickListener(this);

        lockY = (Button) softpultLayout.findViewById(R.id.lockydirection);
        lockY.setOnClickListener(this);

        Button reset = (Button) softpultLayout.findViewById(R.id.reset);
        reset.setOnClickListener(this);

        return softpultLayout;

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
        cc.stopAllThings();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clean();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void clean() {
        cc = null;
        fader = null;
        softpultLayout = null;
    }

    private void setCrrSelected() {
        if(modeCrrSelected != null) {
            modeCrrSelected.setSelected(false);
        }
        switch(cc.getMode()) {
            case CrossControl.MODE_POINTER_PLAIN:
                modeCrrSelected = modePlain;
                break;
            case CrossControl.MODE_POINTER_FOLLOW:
                modeCrrSelected = modeFollow;
                break;
            case CrossControl.MODE_POINTER_SENSOR:
                modeCrrSelected = modeSensor;
                break;
            default:
                ; // do nothing
        }

        modeCrrSelected.setSelected(true);
    }

    public void setEnableLock() {
        lockX.setSelected(cc.getLockXDirection());
        lockY.setSelected(cc.getLockYDirection());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.mode_plain:
                cc.setMode(CrossControl.MODE_POINTER_PLAIN);
                fader.setVisibility(View.GONE);
                setCrrSelected();
                break;
            case R.id.mode_follow:
                cc.setMode(CrossControl.MODE_POINTER_FOLLOW);
                fader.setVisibility(View.VISIBLE);
                setCrrSelected();
                break;
            case R.id.mode_sensor:
                cc.setMode(CrossControl.MODE_POINTER_SENSOR);
                fader.setVisibility(View.VISIBLE);
                setCrrSelected();
                break;
            case R.id.lockxdirection:
                if(!lockedX) {
                    lockedX = true;
                }
                else {
                    lockedX = false;
                }
                cc.enableLockXDirection(lockedX);
                lockX.setSelected(lockedX);
                break;
            case R.id.lockydirection:
                if(!lockedY) {
                    lockedY = true;
                }
                else {
                    lockedY = false;
                }
                cc.enableLockYDirection(lockedY);
                lockY.setSelected(lockedY);

                break;
            case R.id.reset:
                if(cc != null) {
                    cc.reset();
                }
                break;
            default:
                ;
        }
    }
}
