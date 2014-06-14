/*
 * ColorFragment.java
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

import org.openintents.widget.ColorCircle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.model.ColorModel;
import de.dmxcontrol.model.ModelManager.Type;

public class ColorFragment extends BasePanelFragment {
    private final static String TAG = "colorfragment";
    private LinearLayout colorLayout;

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
        colorLayout = (LinearLayout) inflater.inflate(R.layout.color_fragment,
                container, false);
        ColorCircle circle = (ColorCircle) colorLayout
                .findViewById(R.id.color_circle);
        ColorModel colorModel = EntityManager.get()
                .getEntitySelection(EntityManager.CENTRAL_ENTITY_SELECTION)
                .getModel(Type.Color);
        circle.setOnColorChangedListener(colorModel);
        circle.setColor(colorModel.getValue());

        if (!Prefs.get().getDisableAnimations())
            addFadeInAnimation(colorLayout);

        return colorLayout;
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
        colorLayout = null;
    }
}
