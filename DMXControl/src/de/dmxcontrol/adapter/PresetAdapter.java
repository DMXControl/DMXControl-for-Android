package de.dmxcontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.preset.EntityPreset;

/**
 * Created by Qasi on 11.07.2014.
 */
public class PresetAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return ReceivedData.get().Presets.size();
    }

    @Override
    public Object getItem(int position) {
        return ReceivedData.get().Presets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.preset_row, parent, false);
        ((TextView) rowView.findViewById(R.id.preset_row_name)).setText(((EntityPreset) getItem(position)).getName());
        ((TextView) rowView.findViewById(R.id.preset_row_property_value_types)).setText(((EntityPreset) getItem(position)).getPropertyValueTypesAsString());
        return rowView;
    }
}
