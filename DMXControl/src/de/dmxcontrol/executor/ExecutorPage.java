package de.dmxcontrol.executor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.network.ResceivdData;
import de.dmxcontrol.widget.ExecuterPageMultitouchLayout;
import de.dmxcontrol.widget.ExecutorView;
import de.dmxcontrol.widget.LinearLayoutWithMultitouch;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ExecutorPage {

    public View view;
    private HorizontalScrollView hsview;
    private ExecuterPageMultitouchLayout llview;
    public ArrayList<ExecutorView> executors;
    public void SetParentActivity(Activity parent) {
        for (int i = 0; i < executors.size(); i++) {
            executors.get(i).parentActivity = parent;
        }
    }

    public ExecutorPage(int id, String name,Context context)
    {

        try {

            view = View.inflate(context, R.layout.executor_page, null);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

            hsview = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
            llview= (ExecuterPageMultitouchLayout) hsview.findViewById(R.id.executor_collection);
            //llview.ExecutorPage=this;
            if(executors==null) {
                executors= new ArrayList<ExecutorView>();
                for (int i = 0; i < ResceivdData.get().Executors.size(); i++) {
                    executors.add(new ExecutorView(llview.getContext(), ResceivdData.get().Executors.get(i)));
                    llview.addView(executors.get(i));

                    //executors.get(i).Resice();
                }
            }
            else {
                for (int i = 0; i < executors.size(); i++) {
                    llview.addView(executors.get(i));
                }
            }
            hsview.setEnabled(false);
            llview.executorPage=this;


        } catch (Exception e) {
            e.toString();
        }
    }

    public void RaiseMultiTouch(MotionEvent.PointerCoords[] pointer, MotionEvent event){
        for (int i = 0; i <executors.size() ; i++) {
            int[] l = new int[2];
            executors.get(i).getLocationOnScreen(l);
            Rect rect = new Rect(l[0], l[1], l[0] + executors.get(i).getWidth(), l[1] + executors.get(i).getHeight());
            for (int j = 0; j < pointer.length; j++)
                try {

                    boolean contains = rect.contains((int) pointer[j].x, (int) pointer[j].y);
                    if (contains) {
                        //executors.get(i).RaiseMultiTouch(pointer[j],event);
                    }
                } catch (Exception e) {
                    e.toString();
                }
        }
    }
    public void Resize(){
        for (int i = 0; i <executors.size() ; i++) {
            executors.get(i).Resize();
        }
    }
}
