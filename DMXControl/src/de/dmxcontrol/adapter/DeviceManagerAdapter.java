package de.dmxcontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.DeviceMetadata;
import de.dmxcontrol.device.DeviceMetadataCollection;
import de.dmxcontrol.network.ReceivedData;

/**
 * Created by Qasi on 16.07.2014.
 */
public class DeviceManagerAdapter extends BaseExpandableListAdapter {
    private Context ctx;

    public DeviceManagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getGroupCount() {
        return ((ArrayList<DeviceMetadataCollection.VendorCollection>) ReceivedData.get().AvailableDevices.getVendors()).size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<DeviceMetadataCollection.VendorCollection>) ReceivedData.get().AvailableDevices.getVendors()).get(groupPosition).getCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ((ArrayList<DeviceMetadataCollection.VendorCollection>) ReceivedData.get().AvailableDevices.getVendors()).get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((ArrayList<DeviceMetadataCollection.VendorCollection>) ReceivedData.get().AvailableDevices.getVendors()).get(groupPosition).getDeviceMetadata(childPosition);
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
        View rowView = inflater.inflate(R.layout.device_manager_group, parent, false);
        if(getGroupCount() > groupPosition) {
            ((TextView) rowView.findViewById(R.id.deviceManager_GroupView_Name)).setText(((DeviceMetadataCollection.VendorCollection) getGroup(groupPosition)).getVendor());
            ((TextView) rowView.findViewById(R.id.deviceManager_GroupView_count)).setText(((DeviceMetadataCollection.VendorCollection) getGroup(groupPosition)).getCount() + "");
        }
        inflater = null;
        return rowView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.device_manager_device, parent, false);
        if(getGroupCount() > groupPosition) {
            try {
                ((TextView) rowView.findViewById(R.id.deviceManager_DeviceView_Name)).setText(((DeviceMetadata) getChild(groupPosition, childPosition)).getModell());
                ((TextView) rowView.findViewById(R.id.deviceManager_DeviceView_)).setText(((DeviceMetadata) getChild(groupPosition, childPosition)).getDescription());
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