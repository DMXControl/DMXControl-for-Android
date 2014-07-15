package de.dmxcontrol.device;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.network.ServiceFrontend;

/**
 * Created by Qasi on 26.06.2014.
 */
public class DeviceManagerDialog extends FrameLayout {

    private Context ctx;
    private View xmlView;
    private Button btnDevice;
    private EditText editCount, editName, editStartAddress, editSpace, editGroup, editRepeat;
    private ToggleButton btnAutogenerateGroup;
    private DeviceMetadata deviceMetadata;
    private ProgressDialog pDialog;

    private void setDeviceMetadata(DeviceMetadata dm) {
        deviceMetadata = dm;
        editCount.setEnabled(true);
        editCount.setFocusable(true);
        editName.setEnabled(true);
        editName.setFocusable(true);
        editName.setText(deviceMetadata.getModell());
        editStartAddress.setEnabled(true);
        editStartAddress.setFocusable(true);
        editSpace.setEnabled(true);
        editSpace.setFocusable(true);
        editRepeat.setEnabled(true);
        editRepeat.setFocusable(true);
        btnAutogenerateGroup.setEnabled(true);
    }

    public String GetDeviceMetadata() throws JSONException {
        JSONObject o = new JSONObject();
        String out = "";
        if(deviceMetadata != null) {
            o.put("Type", "CreateDevice");
            o.put("DeviceMeta", deviceMetadata.getXmlFile());
            o.put("Count", editCount.getText());
            o.put("Name", editName.getText());
            o.put("StartAddress", editStartAddress.getText());
            o.put("Space", editSpace.getText());
            o.put("GroupName", editGroup.getText());
            o.put("AutoGroup", btnAutogenerateGroup.isChecked());
            o.put("Repeat", editRepeat.getText());
            out = o.toString();
        }
        return out;
    }

    public DeviceManagerDialog(final Context context) {
        super(context);
        this.ctx = context;
        System.gc(); //Clear Memory while this Dialog needs a lot of them ;)
        try {
            JSONObject o = new JSONObject();
            o.put("Type", "AvailableDevices");
            o.put("Request", Entity.Request_All);

            ServiceFrontend.get().sendMessage(o.toString().getBytes());
        }
        catch(Exception e) {
            Log.e("SendAllRequest: ", e.getMessage());
            DMXControlApplication.SaveLog();
        }
        xmlView = View.inflate(context, R.layout.device_manager, null);
        GridLayout grid = ((GridLayout) xmlView.findViewById(R.id.deviceManager_GridLayout));
        try {
            btnDevice = (Button) grid.findViewById(R.id.deviceManager_btnDevice);
            editCount = (EditText) grid.findViewById(R.id.deviceManager_editCount);
            editName = (EditText) grid.findViewById(R.id.deviceManager_editName);
            editStartAddress = (EditText) grid.findViewById(R.id.deviceManager_editAddress);
            editSpace = (EditText) grid.findViewById(R.id.deviceManager_editSpace);
            editGroup = (EditText) grid.findViewById(R.id.deviceManager_editGroupName);
            btnAutogenerateGroup = (ToggleButton) grid.findViewById(R.id.deviceManager_btnAutogenerateGroup);
            editRepeat = (EditText) grid.findViewById(R.id.deviceManager_editRepeat);
        }
        catch(Exception e) {
            Log.w("DeviceManagerDialog", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
        try {

            btnDevice.setSelected(true);
            editCount.setSelected(false);
            editCount.setEnabled(false);
            editName.setEnabled(false);
            editStartAddress.setEnabled(false);
            editSpace.setEnabled(false);
            editRepeat.setEnabled(false);
            btnAutogenerateGroup.setEnabled(false);
            editGroup.setEnabled(false);
            btnAutogenerateGroup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editGroup.setEnabled(btnAutogenerateGroup.isChecked());
                    if((editGroup.getText() + "").equals("")) {
                        editGroup.setText(deviceMetadata.getModell() + " Group");
                    }
                }
            });
        }
        catch(Exception e) {
            Log.w("DeviceManagerDialog", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }

        btnDevice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context ctx = v.getContext();
                pDialog = new ProgressDialog(ctx);
                pDialog.setCancelable(true);
                pDialog.setTitle("Loading");
                pDialog.setMessage("Please wait...");

                new Thread(new Runnable() {
                    private Handler mHandler = new Handler(Looper.myLooper());

                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        mHandler.post(new Runnable() {
                            public void run() {
                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                alert.setTitle("Select");
                                final ExpandableListAdapter adapter = new ExpandableListAdapter(ctx);
                                alert.setIcon(new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(EntityDevice.getDefaultIcon(ctx), 120, 120, false)));
                                View view = View.inflate(context, R.layout.device_manager_selector, null);

                                LinearLayout ll = ((LinearLayout) view.findViewById(R.id.deviceManager_Slector_LinearLayout));
                                final ExpandableListView list = ((ExpandableListView) ll.findViewById(R.id.expandableListView));
                                list.setOnScrollListener(new AbsListView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                                        System.gc();
                                    }

                                    @Override
                                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    }
                                });
                                alert.setView(view);
                                final AlertDialog alertDialog = alert.create();
                                list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                    @Override
                                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                        try {
                                            setDeviceMetadata((DeviceMetadata) adapter.getChild(groupPosition, childPosition));
                                            alertDialog.dismiss();
                                            return true;
                                        }
                                        catch(Exception e) {
                                            e.printStackTrace();
                                            return false;
                                        }
                                    }
                                });
                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                                        list.setAdapter(adapter);
                                    }
                                });
                                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
                                        System.gc();
                                    }
                                });
                                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
                                        System.gc();
                                    }
                                });
                                alertDialog.show();
                            }
                        });
                    }
                }).start();
                pDialog.dismiss();
            }
        });
        btnDevice.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        this.addView(xmlView);
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context ctx;

        public ExpandableListAdapter(Context ctx) {
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
}