package de.dmxcontrol.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.preset.EntityPreset;
import de.dmxcontrol.preset.PresetCollection;

/**
 * Created by Qasi on 11.07.2014.
 */
public class PresetAdapter extends BaseAdapter {
    private ViewGroup parent;

    public PresetAdapter() {
        super();
        ReceivedData.get().Presets.setChangedListener(new PresetCollection.ChangedListener() {
            @Override
            public void onChanged() {
                ((Activity) parent.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void finalize() throws Throwable {
        ReceivedData.get().Presets.removeChangedListeners();
        super.finalize();
    }

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
        return ReceivedData.get().Presets.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.parent = parent;
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.preset_row, parent, false);
        ((TextView) rowView.findViewById(R.id.preset_row_name)).setText(((EntityPreset) getItem(position)).getName());
        ((TextView) rowView.findViewById(R.id.preset_row_property_value_types)).setText(((EntityPreset) getItem(position)).getPropertyValueTypesAsString());
        return rowView;
    }
}
