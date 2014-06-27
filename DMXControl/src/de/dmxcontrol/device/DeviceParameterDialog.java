package de.dmxcontrol.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;

/**
 * Created by Qasi on 26.06.2014.
 */
public class DeviceParameterDialog extends FrameLayout {

    private View xmlView;
    private TextView name, channel, channelCount, image, enabled, model, vendor, author, number;
    private EntityDevice device;

    public EntityDevice getDevice() {
        return device;
    }

    public DeviceParameterDialog(final Context context, final EntityDevice device) {
        super(context);
        this.device = device;
        xmlView = View.inflate(context, R.layout.device_parameter_dialog, null);
        GridLayout grid = ((GridLayout) xmlView.findViewById(R.id.deviceParameter_GridLayout));
        try {
            name = ((TextView) grid.findViewWithTag("deviceParameter_Name"));
            channel = ((TextView) grid.findViewWithTag("deviceParameter_Channel"));
            channelCount = ((TextView) grid.findViewWithTag("deviceParameter_ChannelCount"));
            image = ((TextView) grid.findViewWithTag("deviceParameter_Image"));
            enabled = ((TextView) grid.findViewWithTag("deviceParameter_Enabled"));
            model = ((TextView) grid.findViewWithTag("deviceParameter_Model"));
            vendor = ((TextView) grid.findViewWithTag("deviceParameter_Vendor"));
            author = ((TextView) grid.findViewWithTag("deviceParameter_Author"));
            number = ((TextView) grid.findViewWithTag("deviceParameter_Number"));
        }
        catch(Exception e) {
            Log.w("DeviceParameterDialog", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
        try {
            name.setText(device.getName());
            int fulllChannel = device.getChannel();
            if(fulllChannel <= 512) {
                channel.setText(fulllChannel + "");
            }
            else {
                int universeChannel = (fulllChannel / 512) + 1;
                int realChannel = fulllChannel - ((universeChannel - 1) * 512);
                channel.setText(universeChannel + "." + realChannel + " (" + fulllChannel + ")");
            }
            channelCount.setText(device.getChannelCount() + "");
            image.setText(device.getImageName());
            enabled.setText(device.getEnabled() + "");
            model.setText(device.getModel());
            vendor.setText(device.getVendor());
            author.setText(device.getAuthor());
            number.setText(device.getId() + "");
        }
        catch(Exception e) {
            Log.w("DeviceParameterDialog", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }

        name.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Rename");
                alert.setMessage("Enter Name");
                final EditText input = new EditText(context);
                input.setText(device.getName());
                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = input.getEditableText().toString();
                        device.setName(str, false);
                        name.setText(device.getName());
                        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });
        channel.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Re-Address");
                alert.setMessage("Enter Channel");
                final EditText input = new EditText(context);
                input.setText(device.getChannel() + "");
                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = input.getEditableText().toString();
                        device.setChannel(Integer.parseInt(str), false);
                        channel.setText(device.getChannel() + "");
                        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });
        this.addView(xmlView);
    }
}
