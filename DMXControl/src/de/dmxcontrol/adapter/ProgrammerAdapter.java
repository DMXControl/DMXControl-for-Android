package de.dmxcontrol.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.programmer.EntityProgrammer;

/**
 * Created by Qasi on 16.07.2014.
 */
public class ProgrammerAdapter extends BaseExpandableListAdapter implements EntityProgrammer.ChangedListener {
    private Context ctx;

    public ProgrammerAdapter(Context ctx) {
        super();
        if(ReceivedData.get().SelectedProgrammer == null) {
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String ipString = String.format(
                    "%d.%d.%d.%d",
                    (ip & 0xff),
                    (ip >> 8 & 0xff),
                    (ip >> 16 & 0xff),
                    (ip >> 24 & 0xff));
            for(int i = 0; i < ReceivedData.get().Programmers.size(); i++) {
                if(ReceivedData.get().Programmers.get(i).getName().contains(ipString)) {
                    ReceivedData.get().SelectedProgrammer = ReceivedData.get().Programmers.get(i);
                }
            }
        }
        if(ReceivedData.get().SelectedProgrammer != null) {
            ReceivedData.get().SelectedProgrammer.setChangedListener(this);
        }
        this.ctx = ctx;
    }

    @Override
    public int getGroupCount() {
        if(ReceivedData.get().SelectedProgrammer == null) {
            return 0;
        }
        return ReceivedData.get().SelectedProgrammer.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(ReceivedData.get().SelectedProgrammer == null) {
            return 0;
        }
        return ReceivedData.get().SelectedProgrammer.getDeviceCount(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        if(ReceivedData.get().SelectedProgrammer == null) {
            return null;
        }
        return ReceivedData.get().SelectedProgrammer.getGroup(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(ReceivedData.get().SelectedProgrammer == null) {
            return null;
        }
        return ReceivedData.get().SelectedProgrammer.getDevice(groupPosition, childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return ((EntityProgrammer.State) getGroup(groupPosition)).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        if(convertView == null) {
            rowView = inflater.inflate(R.layout.programmer_row_group, parent, false);
        }
        else {
            rowView = convertView;
        }

        if(getGroupCount() > groupPosition) {
            ((TextView) rowView.findViewById(R.id.programmer_row_group_name)).setText(((EntityProgrammer.State) getGroup(groupPosition)).getName());
        }
        inflater = null;
        if(inflater == null) {
            ;
        }
        return rowView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        if(convertView == null) {
            rowView = inflater.inflate(R.layout.programmer_row_device, parent, false);
        }
        else {
            rowView = convertView;
        }
        if(getGroupCount() > groupPosition) {
            ;
        }
        inflater = null;
        if(inflater == null) {
            ;
        }
        return rowView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onChanged() {
        try {
            new Thread(new Runnable() {
                private Handler mHandler = new Handler(Looper.getMainLooper());

                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }
}
