package de.dmxcontrol.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.file.FileManager;
import de.dmxcontrol.network.UDP.KernelPingDeserializer;
import de.dmxcontrol.network.UDP.ReaderKernelPing;


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

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Prefs.get().getUDPReaderKernelPing().setOnNewsUpdateListener(
                new ReaderKernelPing.NewsUpdateListener() {
                    @Override
                    public void onNewsUpdate() {
                        update = true;
                    }

                }
        );
        try {
            view = View.inflate(this, R.layout.connection, null);
            listView = (ListView) view.findViewById(R.id.connection_listView);
        }
        catch(Exception e) {
            Log.w("", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
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

        Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_LONG).show();
    }

    private void Update() {
        if(update) {
            update = false;
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyAdapter adapter = new MyAdapter(context, generateData());
                        listView.setAdapter(adapter);
                    }
                });
            }
            catch(Exception e) {
                Log.w("", DMXControlApplication.stackTraceToString(e));
                DMXControlApplication.SaveLog();
            }
        }
    }

    private ArrayList<KernelPingItem> generateData() {
        ArrayList<KernelPingItem> items = new ArrayList<KernelPingItem>();
        ArrayList<KernelPingDeserializer> kernelPinglist = Prefs.get().getKernelPing();
        for(int i = 0; i < kernelPinglist.size(); i++) {
            KernelPingDeserializer kernelPing = kernelPinglist.get(i);
            String ips = kernelPing.GetIPAdresses()[0];
            if(kernelPing.GetIPAdresses().length > 1) {
                for(int j = 1; j < kernelPing.GetIPAdresses().length; j++) {
                    ips += " , " + kernelPing.GetIPAdresses()[j];
                }
            }
            items.add(new KernelPingItem(kernelPing.GetHostName(), ips));
        }
        return items;
    }

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        }
    };

    private class KernelPingItem {
        private String name, ips;

        public KernelPingItem(String Name, String IPs) {
            this.name = Name;
            this.ips = IPs;
        }

        public String getName() {
            return this.name;
        }

        public String getIPs() {
            return this.ips;
        }
    }

    public class MyAdapter extends ArrayAdapter<KernelPingItem> {
        private final Context context;
        private final ArrayList<KernelPingItem> itemsArrayList;

        public MyAdapter(Context context, ArrayList<KernelPingItem> itemsArrayList) {

            super(context, R.layout.listview_row, itemsArrayList);

            this.context = context;
            this.itemsArrayList = itemsArrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final KernelPingItem value = itemsArrayList.get(position);
            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.listview_row, parent, false);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ip;
                    if(value.getIPs().contains(" , ")) {
                        ip = value.getIPs().split(" , ")[0];
                    }
                    else {
                        ip = value.getIPs();
                    }
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putString("pref_connect_address", ip).commit();
                    Toast.makeText(getApplicationContext(), "You'r now Connected", Toast.LENGTH_SHORT).show();
                    Prefs.get().StartNetwork();
                }
            });
            // 3. Get the two text view from the rowView
            TextView labelView = (TextView) rowView.findViewById(R.id.label);
            TextView valueView = (TextView) rowView.findViewById(R.id.value);

            // 4. Set the text for textView
            labelView.setText(value.getName());
            valueView.setText(value.getIPs());

            // 5. retrn rowView
            return rowView;
        }
    }
}
