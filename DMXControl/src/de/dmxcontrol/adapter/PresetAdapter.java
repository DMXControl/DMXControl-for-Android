package de.dmxcontrol.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Looper;
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
        View rowView = null;
        if(convertView == null) {
            rowView = inflater.inflate(R.layout.preset_row, parent, false);
        }
        else {
            rowView = convertView;
        }
        ((TextView) rowView.findViewById(R.id.preset_row_name)).setText(((EntityPreset) getItem(position)).getName());
        ((TextView) rowView.findViewById(R.id.preset_row_property_value_types)).setText(((EntityPreset) getItem(position)).getPropertyValueTypesAsString());
        return rowView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }
}
