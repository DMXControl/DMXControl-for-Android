package de.dmxcontrol.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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

    // Tag for log messages
    private final static String TAG = "ServerConnActivity";

    public ServerConnection() {

    }

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
            //listView.setOnClickListener((View.OnClickListener) mOnClickListener);
        }
        catch(Exception e) {
            Log.w(TAG, e.getMessage());
            Log.w(TAG, DMXControlApplication.stackTraceToString(e));
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

        Timer timer = new Timer();

        timer.schedule(myTimerTask, 500, 500);

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_toast_please_wait), Toast.LENGTH_LONG).show();
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
            
            if(kernelPing.GetIPAdresses().length > 0) {

                // TODO: 23.08.15 Why do you get first item here and not in for loop??
                String ips = kernelPing.GetIPAdresses()[0];
                
                if(kernelPing.GetIPAdresses().length > 1) {

                    for(int j = 1; j < kernelPing.GetIPAdresses().length; j++) {
                        ips += " , " + kernelPing.GetIPAdresses()[j];
                    }
                }

                items.add(new KernelPingItem(
                        kernelPing.GetHostName(),
                        ips,
                        kernelPing.GetVersion(),
                        kernelPing.GetProject(),
                        kernelPing.GetCompatible()));
            }
        }
        return items;
    }

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    private class KernelPingItem {

        private String name, ips, version, project;
        private boolean compatible;

        public KernelPingItem(String Name, String IPs, String Version, String Project, boolean Compatible) {

            this.name = Name;
            this.ips = IPs;
            this.version = Version;
            this.project = Project;
            this.compatible = Compatible;
        }

        public String getName() {
            return this.name;
        }

        public String getIPs() {
            return this.ips;
        }

        public String getVersion() {
            return this.version;
        }

        public String getProject() {
            return this.project;
        }

        public boolean getCompatible() {
            return this.compatible;
        }
    }

    public class MyAdapter extends ArrayAdapter<KernelPingItem> {

        private final Context context;
        private final ArrayList<KernelPingItem> itemsArrayList;

        public MyAdapter(Context context, ArrayList<KernelPingItem> itemsArrayList) {

            super(context, R.layout.connection_server_row, itemsArrayList);

            this.context = context;
            this.itemsArrayList = itemsArrayList;
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {

            final KernelPingItem value = itemsArrayList.get(position);

            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.connection_server_row, parent, false);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String ip;

                    // If we get multiple ips, seperated by ' , ' we use first one
                    if(value.getIPs().contains(" , ")) {
                        ip = value.getIPs().split(" , ")[0];
                    }
                    else {
                        ip = value.getIPs();
                    }
                    
                    // Set found ip in prefs
                    Prefs.get().setServerAddress(ip);

                    // Show little text
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_toast_connected), Toast.LENGTH_SHORT).show();
                    
                    // Go online (Try to connect)
                    Prefs.get().setOffline(false);
                    
                    // We don't need to start here anymore. Just starting Kernel detection
                    //Prefs.get().StartNetwork();
                }
            });

            // 3. Get the 4 text view from the rowView
            TextView nameView = (TextView) rowView.findViewById(R.id.server_name);
            TextView ipsView = (TextView) rowView.findViewById(R.id.server_ips);
            TextView versionView = (TextView) rowView.findViewById(R.id.server_version);
            TextView projectView = (TextView) rowView.findViewById(R.id.server_project);
            TextView compatibleView = (TextView) rowView.findViewById(R.id.server_compatible);

            // 4. Set the text for textView
            nameView.setText(value.getName());
            ipsView.setText(getResources().getString(R.string.connection_host_ips) + ": " + value.getIPs());
            versionView.setText(getResources().getString(R.string.connection_host_version) + ": " + value.getVersion());
            projectView.setText(getResources().getString(R.string.connection_host_project) + ": " + value.getProject());

            if(value.getCompatible()) {
                compatibleView.setText(getResources().getString(R.string.connection_host_compatible_true));
            }
            else {
                compatibleView.setText(getResources().getString(R.string.connection_host_compatible_false));

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rowView.setBackground(getResources().getDrawable(R.drawable.server_row_background_incompatible));
                }
                else {
                    rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.server_row_background_incompatible));
                }
            }

            // 5. return rowView
            return rowView;
        }
    }
}
