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
import android.view.Window;
import android.view.WindowManager;
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
import de.dmxcontrol.widget.ExecutorView;
import de.dmxcontrol.widget.FaderVerticalControl;


/**
 * Created by Qasi on 13.06.2014.
 */
public class LiveActivity extends Activity {

    private View view;
    private HorizontalScrollView hsview;
    private LinearLayout llview;
    private boolean update = false;
    private Context context;
    private ArrayList<ExecutorView> executors=new ArrayList<ExecutorView>();

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

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            view = View.inflate(this, R.layout.executor_page, null);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

            hsview = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
            llview= (LinearLayout) hsview.findViewById(R.id.executor_collection);
            setContentView(view);
            for(int i=0;i<10;i++) {
                executors.add(new ExecutorView(llview.getContext(),"Executor "+(i+1)));
                llview.addView(executors.get(i));
                //executors.get(i).Resice();
            }

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
        return items;
    }
}
