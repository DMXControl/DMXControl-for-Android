package de.dmxcontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.changelog.Changelog;
import de.dmxcontrol.network.UDP.KernelPingDeserielizer;
import de.dmxcontrol.network.UDP.Reader;
import de.dmxcontrol.widget.FaderVerticalControl;


/**
 * Created by Qasi on 13.06.2014.
 */
public class LiveActivity extends Activity {

    private View view;
    private boolean update = false;
    private Context context;

    //private TwoWayView  scrolView;
    public LiveActivity() {

    }

    //public AlertDialog onCreateDialog(int id) {
    //return mainAboutDialog();
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        Prefs.get().getUDPReader().setOnNewsUpdateListener(
                new Reader.NewsUpdateListener() {
                    @Override
                    public void onNewsUpdate() {
                        update = true;
                    }

                }
        );
        try {
            view = View.inflate(this, R.layout.executor_page, null);
            setContentView(view);
            MyAdapter adapter = new MyAdapter(context, generateData());
            //scrolView.setAdapter(adapter);
        } catch (Exception e) {
            e.toString();
        }
    }

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {


        }
    };

    private ArrayList<ClipData.Item> generateData() {
        ArrayList<ClipData.Item> items = new ArrayList<ClipData.Item>();
        items.add(new ClipData.Item(""));
        items.add(new ClipData.Item(""));
        items.add(new ClipData.Item(""));
        return items;
    }

    public class MyAdapter extends ArrayAdapter<ClipData.Item> {

        private final Context context;
        private final ArrayList<ClipData.Item> itemsArrayList;

        public MyAdapter(Context context, ArrayList<ClipData.Item> itemsArrayList) {

            super(context, R.layout.executor, itemsArrayList);

            this.context = context;
            this.itemsArrayList = itemsArrayList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ClipData.Item value = itemsArrayList.get(position);
            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.executor, parent, false);
            // 3. Get the two text view from the rowView
            FaderVerticalControl fader = (FaderVerticalControl) rowView.findViewById(R.id.fader);
            //TextView valueView = (TextView) rowView.findViewById(R.id.value);

            fader.pointerPosition(0, 100);
            //valueView.setText(value.getHtmlText());

            // 5. retrn rowView
            return rowView;
        }
    }
}
