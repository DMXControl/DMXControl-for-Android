/*
 * GridViewsFragment.java
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import de.dmxcontrol.adapter.DeviceAdapter;
import de.dmxcontrol.adapter.GroupAdapter;
import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.DeviceParameterDialog;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.device.EntitySelection;
import de.dmxcontrol.device.EntitySelection.OnEntitySelectionListener;

public class DeviceGroupFragment extends BasePanelFragment implements
        OnEntitySelectionListener, OnItemClickListener {
    private final static String TAG = "gridviewsfragment";
    private GridView mGroupGrid, mDeviceGrid;
    private EntitySelection mEntitySelection;

    private DeviceAdapter mDeviceAdapter;
    private GroupAdapter mGroupAdapter;

    // startup process initiated
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mEntitySelection = EntityManager.get().getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION);
        mEntitySelection.addListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        LinearLayout deviceLayout = (LinearLayout) inflater.inflate(
                R.layout.devicegroup_fragment, container, false);

        mGroupGrid = (GridView) deviceLayout.findViewById(R.id.group_grid);
        /** mGroupGrid.setOnHoverListener(new View.OnHoverListener() {
         //@override
         public boolean onHover(View view, MotionEvent motionEvent) {
         switch(motionEvent.getAction()){
         case MotionEvent.ACTION_HOVER_ENTER: // Enter Hovered
         case MotionEvent.ACTION_HOVER_EXIT: // Leave Hovered
         view.setBackgroundColor(Color.TRANSPARENT);
         view.setScaleX((float)1.0);
         view.setScaleY((float)1.0);
         break;
         case ACTION_HOVER_MOVE: // On Hover
         view.setBackgroundColor(Color.RED);
         view.setScaleX((float)2.0);
         view.setScaleY((float)2.0);
         break;
         }
         Log.wtf("Something", "I'm Being Hovered" + motionEvent.getAction());
         return false;
         }
         });**/
        mGroupAdapter = new GroupAdapter(EntityManager.get(),
                EntityManager.CENTRAL_ENTITY_SELECTION, getActivity());
        mGroupGrid.setAdapter(mGroupAdapter);
        mGroupGrid.setOnItemClickListener(this);

        mDeviceGrid = (GridView) deviceLayout.findViewById(R.id.device_grid);
        mDeviceAdapter = new DeviceAdapter(EntityManager.get(), EntityManager.CENTRAL_ENTITY_SELECTION, getActivity());
        mDeviceGrid.setAdapter(mDeviceAdapter);
        mDeviceGrid.setOnItemClickListener(this);
        mDeviceGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                EntityDevice device = (EntityDevice) adapterView.getItemAtPosition(position);

                if(device != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(adapterView.getContext());
                    alert.setTitle("Parameter");
                    alert.setIcon(new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(device.getImage(adapterView.getContext()), 120, 120, false)));
                    alert.setView(new DeviceParameterDialog(adapterView.getContext(), device));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    return true;
                }
                return false;
            }
        });

        if(!Prefs.get().getDisableAnimations()) {
            addFadeInAnimation(deviceLayout);
        }

        return deviceLayout;
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

    // teardown process done

    public void clean() {
        mGroupGrid = null;
        mDeviceGrid = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position,
                            long id) {

        switch(adapterView.getId()) {
            case R.id.group_grid:
                EntityGroup group = (EntityGroup) adapterView
                        .getItemAtPosition(position);
                mEntitySelection.toogleEntity(group);
                break;
            case R.id.device_grid:
                EntityDevice device = (EntityDevice) adapterView
                        .getItemAtPosition(position);
                mEntitySelection.toogleEntity(device);
                break;
            default:
                ; // nothing
        }
    }

    @Override
    public void onEntitySelectionChanged(EntitySelection es) {
        mDeviceAdapter.notifyDataSetChanged();
        mGroupAdapter.notifyDataSetChanged();
    }
}
