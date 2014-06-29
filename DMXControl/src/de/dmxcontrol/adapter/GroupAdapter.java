/*
 * DeviceGroupAdapter.java
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

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.network.ReceivedData;

public class GroupAdapter extends BaseAdapter {
    private EntityManager mEntityManager;
    private Context ctx;
    private int mEntitySelection;
    public int SelectionColor;

    public GroupAdapter(EntityManager em, int entitySelection, Context ctx) {
        mEntityManager = em;
        mEntitySelection = entitySelection;
        this.ctx = ctx;
        SelectionColor = this.ctx.getResources().getColor(R.color.btn_background_highlight);
    }

    public void setDeviceGroupContext(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return ReceivedData.get().Groups.size();
    }

    @Override
    public Object getItem(int index) {
        return ReceivedData.get().Groups.get(index);
    }

    @Override
    public long getItemId(int index) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        TextView editText = null;

        View view = null;
        try {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ctx
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // 2. Get rowView from inflater
                View rowView = inflater.inflate(R.layout.device_cell, parent, false);

                view = rowView;
                imageView = (ImageView) view.findViewById(R.id.deviceCell_icon);
                imageView.setPadding(8, 8, 8, 8);

                editText = (TextView) view.findViewById(R.id.deviceCell_name);
            /* imageView.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {
                    ImageView imageView = (ImageView) view;
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_HOVER_EXIT:
                            imageView.setScaleX(1f);
                            imageView.setScaleY(1f);
                            imageView.setPadding(8, 8, 8, 8);
                            break;
                        case MotionEvent.ACTION_HOVER_ENTER:
                        case MotionEvent.ACTION_HOVER_MOVE:
                            imageView.setPadding(0, 0, 0, 0);
                            imageView.setScaleX(1.5f);
                            imageView.setScaleY(1.5f);
                            break;
                    }
                    return false;
                }
            }); */
            }
            else {
                view = (View) convertView;
                imageView = (ImageView) view.findViewById(R.id.deviceCell_icon);
                editText = (TextView) view.findViewById(R.id.deviceCell_name);
            }

            Entity ent = ReceivedData.get().Groups.get(position);
            if(ent == null) {
                imageView.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);
                return view;
            }
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(ent.getImage(ctx));
            editText.setText(ent.getName());
            final TextView finalEditText = editText;
            ent.setNameChangedListener(new Entity.NameChangedListener() {
                @Override
                public void onNameChanged(String name) {
                    finalEditText.setText(name);
                    notifyDataSetChanged();
                }
            });

            if(mEntityManager.isInEntitySelection(Type.GROUP, mEntitySelection, ent.getId())) {
                imageView.setBackgroundColor(SelectionColor);
            }
            else {
                imageView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        catch(Exception e) {
            e.toString();
        }
        return view;
    }

}