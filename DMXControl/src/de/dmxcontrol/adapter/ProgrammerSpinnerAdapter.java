package de.dmxcontrol.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.programmer.EntityProgrammer;

public class ProgrammerSpinnerAdapter implements SpinnerAdapter {

    private Context ctx;

    public ProgrammerSpinnerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;

        if(convertView == null) {
            rowView = inflater.inflate(R.layout.programmer_spinner_row, parent, false);
            ((TextView) rowView.findViewById(R.id.programmer_spinner_row_name)).setText(((EntityProgrammer) getItem(position)).getName());
        }
        else {
            rowView = convertView;
        }
        return rowView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return ReceivedData.get().Programmers.size();
    }

    @Override
    public Object getItem(int position) {
        return ReceivedData.get().Programmers.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(getItem(position) == null) {
            return 0;
        }
        return ((EntityProgrammer) getItem(position)).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;

        if(convertView == null) {
            rowView = inflater.inflate(R.layout.programmer_spinner_row, parent, false);
            ((TextView) rowView.findViewById(R.id.programmer_spinner_row_name)).setText(ReceivedData.get().SelectedProgrammer.getName());
        }
        else {
            rowView = convertView;
        }
        return rowView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return getCount() <= 0;
    }
}
