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


/**
 * Created by Qasi on 13.06.2014.
 */
public class ServerConnection extends Activity {

    private View view;
    private boolean update = false;
    private Context context;
    private ListView listView;

    public ServerConnection() {

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
            view = View.inflate(this, R.layout.connection, null);
            listView = (ListView) view.findViewById(R.id.connection_listView);
        } catch (Exception e) {
            e.toString();
        }
        setContentView(view);
        Update();
        TimerTask myTimerTask = new TimerTask() {

            @Override
            public void run() {
                Update();
            }

        };

// new timer
        Timer timer = new Timer();

// schedule timer
        timer.schedule(myTimerTask, 500, 500);
    }

    private void Update() {
        if (update) {
            update = false;
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyAdapter adapter = new MyAdapter(context, generateData());
                        listView.setAdapter(adapter);
                    }
                });
            } catch (Exception e) {
                e.toString();

            }
        }
    }

    private ArrayList<ClipData.Item> generateData() {
        ArrayList<ClipData.Item> items = new ArrayList<ClipData.Item>();
        ArrayList<KernelPingDeserielizer> kernelPinglist = Prefs.get().getKernelPing();
        for (int i = 0; i < kernelPinglist.size(); i++) {
            KernelPingDeserielizer kernelPing = kernelPinglist.get(i);
            String ips = kernelPing.GetIPAdresses()[0];
            if (kernelPing.GetIPAdresses().length > 1) {
                for (int j = 1; j < kernelPing.GetIPAdresses().length; j++) {
                    ips += " , " + kernelPing.GetIPAdresses()[j];
                }
            }
            items.add(new ClipData.Item(kernelPing.GetHostName(), ips));
        }
        return items;
    }

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {


        }
    };

    public class MyAdapter extends ArrayAdapter<ClipData.Item> {

        private final Context context;
        private final ArrayList<ClipData.Item> itemsArrayList;

        public MyAdapter(Context context, ArrayList<ClipData.Item> itemsArrayList) {

            super(context, R.layout.listview_row, itemsArrayList);

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
            View rowView = inflater.inflate(R.layout.listview_row, parent, false);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (value.getHtmlText().contains(" , ")) {
                        Prefs.get().setServerAddress(value.getHtmlText().split(" , ")[0]);
                    } else
                        Prefs.get().setServerAddress(value.getHtmlText());
                }
            });
            // 3. Get the two text view from the rowView
            TextView labelView = (TextView) rowView.findViewById(R.id.label);
            TextView valueView = (TextView) rowView.findViewById(R.id.value);

            // 4. Set the text for textView
            labelView.setText(value.getText());
            valueView.setText(value.getHtmlText());

            // 5. retrn rowView
            return rowView;
        }
    }
}
