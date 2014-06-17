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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import de.dmxcontrol.executor.ExecutorPage;
import de.dmxcontrol.executor.ExecutorPageManager;
import de.dmxcontrol.network.ResceivdData;
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
    private ArrayList<ExecutorView> executors;

    public LiveActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            if(ResceivdData.get().executorPage==null){ResceivdData.get().executorPage=new ExecutorPage(1,"",context);}
            view = ResceivdData.get().executorPage.view;
            boolean needResize=false;
            if(view.getParent()!=null){
                ((FrameLayout)view.getParent()).removeAllViews();
                needResize=true;
            }
            ResceivdData.get().executorPage.SetParentActivity(this);
            setContentView(view);
            if(needResize){
                ResceivdData.get().executorPage.Resize();
            }
        } catch (Exception e) {
            e.toString();
        }
    }

   /** @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            int counter = event.getPointerCount();
            MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[counter];
            for (int i = 0; i < counter; i++) {
                MotionEvent.PointerCoords coordinate = new MotionEvent.PointerCoords();
                event.getPointerCoords(i, coordinate);
                coords[i] = coordinate;
            }
            ResceivdData.get().executorPage.RaiseMultiTouch(coords, event);
        }
        catch (Exception e){
            e.toString();
        }
        return false;
    }**/

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

        }
    };
}
