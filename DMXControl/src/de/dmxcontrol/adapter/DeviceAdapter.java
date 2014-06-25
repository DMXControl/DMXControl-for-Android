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

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.device.EntityManager.Type;

//import android.view.MotionEvent;

public class DeviceAdapter extends BaseAdapter {
    private EntityManager mEntityManager;
    private Context ctx;
    private int mEntitySelection;
    public int SelectionColor;

    public DeviceAdapter(EntityManager em, int entitySelection, Context ctx) {
        mEntityManager = em;
        this.ctx = ctx;
        mEntitySelection = entitySelection;
        SelectionColor = this.ctx.getResources().getColor(R.color.btn_background_highlight);
    }

    public void setDeviceContext(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return mEntityManager.getSizeForType(Type.DEVICE);
    }

    @Override
    public Object getItem(int index) {
        return mEntityManager.getItemForType(Type.DEVICE, index);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(ctx);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
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
        else
        {
            imageView = (ImageView) convertView;
        }

        Entity ent = mEntityManager.getItemForType(Type.DEVICE, index);

        try {
            imageView.setImageBitmap(ent.getImage(ctx));
        }
        catch (Exception ex) {
        }

        if (mEntityManager.isInEntitySelection(Type.DEVICE, mEntitySelection, ent.getId())) {
            imageView.setBackgroundColor(SelectionColor);
        }
        else {
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }
        return imageView;
    }

}
