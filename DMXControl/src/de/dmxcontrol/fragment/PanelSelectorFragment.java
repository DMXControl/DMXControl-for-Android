/*
 * PanelSelectorFragment.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2012 DMXControl-For-Android. All rights reserved.
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
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.device.EntitySelection;
import de.dmxcontrol.device.EntitySelection.OnEntitySelectionListener;

public class PanelSelectorFragment extends Fragment implements OnClickListener,
        OnLongClickListener, OnEntitySelectionListener {
    private final static String TAG = "panelselectorfragment";
    private Button mBDevice;
    private EntitySelection mEntitySelection;
    private EditText mEditDevice;
    private EditText mEditGroup;

    private static final String EXTRA_ACTIVE_INPUT_TYPE = "de.dmxcontrol.EXTRA_ACTIVE_INPUT_TYPE";
    private int mActiveInputType;
    private static final int ACTIVE_INPUT_TYPE_DEVICE = 0;
    private static final int ACTIVE_INPUT_TYPE_GROUP = 1;
    private boolean mIsEditing;

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
        if(savedInstanceState != null) {
            mActiveInputType = savedInstanceState.getInt(
                    EXTRA_ACTIVE_INPUT_TYPE, ACTIVE_INPUT_TYPE_DEVICE);
        }
        else {
            mActiveInputType = ACTIVE_INPUT_TYPE_DEVICE;
        }

        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.group_device_selector_fragment, container, false);
        mEntitySelection = EntityManager.get().getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION);
        mEntitySelection.addListener(this);

        mEditDevice = (EditText) layout.findViewById(R.id.device_input);
        mEditDevice.setInputType(InputType.TYPE_NULL);
        mEditDevice.setText(mEntitySelection.getRangesString(Type.DEVICE));

        mEditGroup = (EditText) layout.findViewById(R.id.group_input);
        mEditGroup.setInputType(InputType.TYPE_NULL);
        mEditGroup.setText(mEntitySelection.getRangesString(Type.GROUP));

        mBDevice = (Button) layout.findViewById(R.id.button_dev);
        mBDevice.setOnClickListener(this);

        updateActiveInputType();

        Button bSpan = (Button) layout.findViewById(R.id.button_span);
        bSpan.setOnClickListener(this);
        Button bAggregate = (Button) layout.findViewById(R.id.button_aggregate);
        bAggregate.setOnClickListener(this);
        Button bStar = (Button) layout.findViewById(R.id.button_star);
        bStar.setOnClickListener(this);

        Button bErase = (Button) layout.findViewById(R.id.button_clear);
        bErase.setOnClickListener(this);
        bErase.setOnLongClickListener(this);

        Button bOne = (Button) layout.findViewById(R.id.button_one);
        bOne.setOnClickListener(this);
        Button bTwo = (Button) layout.findViewById(R.id.button_two);
        bTwo.setOnClickListener(this);
        Button bThree = (Button) layout.findViewById(R.id.button_three);
        bThree.setOnClickListener(this);
        Button bFour = (Button) layout.findViewById(R.id.button_four);
        bFour.setOnClickListener(this);
        Button bFive = (Button) layout.findViewById(R.id.button_five);
        bFive.setOnClickListener(this);
        Button bSix = (Button) layout.findViewById(R.id.button_six);
        bSix.setOnClickListener(this);
        Button bSeven = (Button) layout.findViewById(R.id.button_seven);
        bSeven.setOnClickListener(this);
        Button bEight = (Button) layout.findViewById(R.id.button_eight);
        bEight.setOnClickListener(this);
        Button bNine = (Button) layout.findViewById(R.id.button_nine);
        bNine.setOnClickListener(this);
        Button bZero = (Button) layout.findViewById(R.id.button_zero);
        bZero.setOnClickListener(this);

        return layout;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_ACTIVE_INPUT_TYPE, mActiveInputType);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.button_dev:
                toggleActiveInputType();
                updateActiveInputType();
                break;
            case R.id.button_span:
                appendToActive("-");
                break;
            case R.id.button_aggregate:
                appendToActive("+");
                break;
            case R.id.button_star:
                appendToActive("*");
                break;
            case R.id.button_clear:
                removeLastCharFromActive();
                break;
            case R.id.button_one:
                appendToActive("1");
                break;
            case R.id.button_two:
                appendToActive("2");
                break;
            case R.id.button_three:
                appendToActive("3");
                break;
            case R.id.button_four:
                appendToActive("4");
                break;
            case R.id.button_five:
                appendToActive("5");
                break;
            case R.id.button_six:
                appendToActive("6");
                break;
            case R.id.button_seven:
                appendToActive("7");
                break;
            case R.id.button_eight:
                appendToActive("8");
                break;
            case R.id.button_nine:
                appendToActive("9");
                break;
            case R.id.button_zero:
                appendToActive("0");
                break;

        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.button_clear:
                clearActive();
                break;
            default:
                return false;
        }
        return true;

    }

    private void clearActive() {
        if(mActiveInputType == ACTIVE_INPUT_TYPE_DEVICE) {
            mEntitySelection.clearEntities(Type.DEVICE);
        }
        else {
            mEntitySelection.clearEntities(Type.GROUP);
        }
    }

    private void removeLastCharFromActive() {
        Editable et;

        if(mActiveInputType == ACTIVE_INPUT_TYPE_DEVICE) {
            et = mEditDevice.getEditableText();
        }
        else {
            et = mEditGroup.getEditableText();
        }

        if(et.length() > 0) {
            Log.d(TAG,
                    "deleting from " + (et.length() - 1) + " - " + et.length());
            et.delete(et.length() - 1, et.length());
        }

        mIsEditing = true;
        updateEntitySelection(et);
        mIsEditing = false;
    }

    private void appendToActive(String s) {
        Editable et;

        if(mActiveInputType == ACTIVE_INPUT_TYPE_DEVICE) {
            et = mEditDevice.getEditableText();
        }
        else {
            et = mEditGroup.getEditableText();
        }

        et.append(s);
        mIsEditing = true;
        updateEntitySelection(et);
        mIsEditing = false;
    }

    public void updateActiveInputType() {
        if(mActiveInputType == ACTIVE_INPUT_TYPE_DEVICE) {
            mEditDevice.requestFocus();
            mBDevice.setText("D");
        }
        else {
            mEditGroup.requestFocus();
            mBDevice.setText("G");
        }
    }

    private void updateEntitySelection(Editable et) {
        String s = et.toString();

        if(mActiveInputType == ACTIVE_INPUT_TYPE_DEVICE) {
            mEntitySelection.parse(Type.DEVICE, s);
        }
        else {
            mEntitySelection.parse(Type.GROUP, s);
        }
    }

    private void toggleActiveInputType() {
        if(mActiveInputType == ACTIVE_INPUT_TYPE_DEVICE) {
            mActiveInputType = ACTIVE_INPUT_TYPE_GROUP;
        }
        else {
            mActiveInputType = ACTIVE_INPUT_TYPE_DEVICE;
        }
    }

    @Override
    public void onEntitySelectionChanged(EntitySelection es) {
        if(mIsEditing) {
            return;
        }

        mEditDevice.setText(es.getRangesString(Type.DEVICE));
        mEditGroup.setText(es.getRangesString(Type.GROUP));
    }
}
