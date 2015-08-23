/*
 * DeviceAdapter.java
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

package de.dmxcontrol.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.DeviceCollection;
import de.dmxcontrol.device.DeviceGroupWrapper;
import de.dmxcontrol.device.DeviceProperty;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.file.ImageWithKey;
import de.dmxcontrol.file.ImageWithKeyCollection;
import de.dmxcontrol.network.ReceivedData;

//import android.view.MotionEvent;

public class GoboAdapter extends BaseAdapter {

    private ImageWithKeyCollection images = new ImageWithKeyCollection();
    private Context ctx;
    public int SelectionColor;

    public GoboAdapter(final Context ctx) {

        this.ctx = ctx;

        SelectionColor = this.ctx.getResources().getColor(R.color.btn_background_highlight);

        ReceivedData.get().Devices.setChangedListener(new DeviceCollection.ChangedListener() {
            @Override
            public void onChanged() {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void finalize() throws Throwable {
        ReceivedData.get().Devices.removeChangedListeners();
        super.finalize();
    }

    @Override
    public int getCount() {

        if(this.items == null) {

            getItems();

            if(this.items == null) {
                return 0;
            }
        }

        return this.items.length;
    }

    private DeviceProperty.DevicePropertyValue[] items;

    public Object getItems() {

        EntityDevice selection = DeviceGroupWrapper.get().getLastWrappedDeviceFromSelection();

        if(selection != null) {
            this.items = selection.getGobos();
        }

        return this.items;
    }

    @Override
    public Object getItem(int index) {
        return this.items[index];
    }

    @Override
    public long getItemId(int i) {
        // return -1 if object has gone so item
        return -1;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ImageView imageView = null;

        View view = null;
        try {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ctx
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // 2. Get rowView from inflater
                View rowView = inflater.inflate(R.layout.gobo_cell, parent, false);

                view = rowView;
                imageView = (ImageView) view.findViewById(R.id.goboicon);
                imageView.setPadding(2, 2, 2, 2);
            }
            else {
                view = (View) convertView;
                imageView = (ImageView) view.findViewById(R.id.goboicon);
            }

            if(this.items == null) {
                imageView.setVisibility(View.INVISIBLE);
                return view;
            }
            imageView.setVisibility(View.VISIBLE);
            if(!images.contains(this.items[index].getValue())) {
                images.add(new ImageWithKey(this.items[index].getImage(ctx), this.items[index].getValue()));
            }
            imageView.setImageBitmap(images.get(this.items[index].getValue()).getBitmap());

            if(DeviceGroupWrapper.get().getLastWrappedDeviceFromSelection().getSelectedValueIndices().contains(items[index].getIndex())) {
                //imageView.setBackgroundColor(SelectionColor);
            }
            else {
                imageView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        catch(Exception e) {
            Log.d("", DMXControlApplication.stackTraceToString(e));
        }
        return view;
    }
}
