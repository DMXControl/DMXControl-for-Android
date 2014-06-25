package de.dmxcontrol.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;

import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.executor.ExecutorPageView;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.widget.ExecutorView;


/**
 * Created by Qasi on 13.06.2014.
 */
public class LiveActivity extends Activity {

    private View view;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            if(ReceivedData.get().executorPageView==null){
                ReceivedData.get().executorPageView=new ExecutorPageView(1,"",context);}
            else{
                ReceivedData.get().executorPageView.LoadExecutors();
            }
            view = ReceivedData.get().executorPageView.getView();
            boolean needResize=false;

            if(view.getParent()!=null) {
                ((FrameLayout)view.getParent()).removeAllViews();
                needResize=true;
            }
            ReceivedData.get().executorPageView.SetParentActivity(this);
            setContentView(view);

            if(needResize) {
                ReceivedData.get().executorPageView.Resize();
            }
        }
        catch (Exception e) {
            Log.w("",DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
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
            ReceivedData.get().executorPage.RaiseMultiTouch(coords, event);
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
