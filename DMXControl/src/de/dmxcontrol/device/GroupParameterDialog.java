package de.dmxcontrol.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;

/**
 * Created by Qasi on 26.06.2014.
 */
public class GroupParameterDialog extends FrameLayout {

    private View xmlView;
    private TextView name, image, number;
    private EntityGroup group;

    public EntityGroup getGroup() {
        return group;
    }

    public GroupParameterDialog(final Context context, final EntityGroup group) {
        super(context);
        this.group = group;
        xmlView = View.inflate(context, R.layout.group_parameter_dialog, null);
        GridLayout grid = ((GridLayout) xmlView.findViewById(R.id.groupParameter_GridLayout));
        try {
            name = ((TextView) grid.findViewWithTag("groupParameter_Name"));
            image = ((TextView) grid.findViewWithTag("groupParameter_Image"));
            number = ((TextView) grid.findViewWithTag("groupParameter_Number"));
        }
        catch(Exception e) {
            Log.w("DeviceParameterDialog", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
        try {
            name.setText(group.getName());
            image.setText(group.getImageName());
            number.setText(group.getId() + "");
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
                input.setText(group.getName());
                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = input.getEditableText().toString();
                        group.setName(str, false);
                        name.setText(group.getName());
                        Toast.makeText(context, "Name: " + str, Toast.LENGTH_LONG).show();
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
