/*
 * ChangelogListAdapter.java
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

package de.dmxcontrol.changelog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.dmxcontrol.android.R;

public class ChangelogListAdapter extends BaseAdapter {
    private final static String TAG = "changelog";
    private Changelog mChangelog;
    private IItemsUpdateListener ul = new MyItemsUpdateListener();
    private Context mCtx;

    public ChangelogListAdapter(Context ctx, Changelog changelog) {
        mChangelog = changelog;
        mChangelog.setUpdateListener(ul);
        mCtx = ctx;
    }

    public void setContext(Context ctx) {
        mCtx = ctx;
    }

    public int getCount() {
        return mChangelog.getSize();
    }

    public Object getItem(int position) {
        return mChangelog.getRelease(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mChangelog.getRelease(position).size();
    }

    @Override
    public int getViewTypeCount() {
        return mChangelog.getTypeCount();
    }

    public View getView(int index, View convertView, ViewGroup parent) {
        LinearLayout ll;
        if(convertView == null) {
            ll = (LinearLayout) LayoutInflater.from(mCtx).inflate(
                    R.layout.changelog_release, parent, false);
        }
        else {
            ll = (LinearLayout) convertView;
        }

        Release r = mChangelog.getRelease(index);
        TextView tvVersion = (TextView) ll.findViewById(R.id.release_version);
        tvVersion.setText(r.getVersion());

        int crrItem = 0;
        while(crrItem < r.size()) {
            TextView tvItem;
            LinearLayout llChild;
            if(crrItem < ll.getChildCount() - 1) {
                llChild = (LinearLayout) ll.getChildAt(crrItem + 1);
            }
            else {
                llChild = (LinearLayout) LayoutInflater.from(mCtx).inflate(
                        R.layout.changelog_release_item, ll, false);
                ll.addView(llChild);
            }
            tvItem = (TextView) llChild.findViewById(R.id.release_item);
            tvItem.setText("●" + r.getItem(crrItem));

            crrItem++;
        }

        return ll;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    class MyItemsUpdateListener implements IItemsUpdateListener {

        public void notifyUpdate() {
            notifyDataSetChanged();
        }

    }

}
