package de.dmxcontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import de.dmxcontrol.android.R;
import de.dmxcontrol.network.ReceivedData;

/**
 * Created by Qasi on 16.07.2014.
 */
public class ProgrammerAdapter extends BaseExpandableListAdapter {
    private Context ctx;

    public ProgrammerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getGroupCount() {
        return ReceivedData.get().Programmer.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ReceivedData.get().Programmer.getDeviceCount(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ReceivedData.get().Programmer.getGroup(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ReceivedData.get().Programmer.getDevice(groupPosition, childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.programmer_row_group, parent, false);
        if(getGroupCount() > groupPosition) {
        }
        inflater = null;
        return rowView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.programmer_row_device, parent, false);
        if(getGroupCount() > groupPosition) {
            try {
            }
            catch(Exception e) {
                e.toString();
            }
        }
        inflater = null;
        return rowView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}