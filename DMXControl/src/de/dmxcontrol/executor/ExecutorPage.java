package de.dmxcontrol.executor;

import android.app.Activity;
import android.content.Context;
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
import de.dmxcontrol.widget.ExecutorView;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ExecutorPage extends Entity{

    public View view;
    private HorizontalScrollView hsview;
    private LinearLayout llview;
    public ArrayList<ExecutorView> executors;
    public void SetParentActivity(Activity parent) {
        for (int i = 0; i < executors.size(); i++) {
            executors.get(i).parentActivity = parent;
        }
    }

    public ExecutorPage(int id, String name,Context context)
    {
        super(id,name, EntityManager.Type.EXECUTOR);
        try {
            view = View.inflate(context, R.layout.executor_page, null);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

            hsview = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
            llview= (LinearLayout) hsview.findViewById(R.id.executor_collection);
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

        } catch (Exception e) {
            e.toString();
        }
    }
    public void Resize(){
        for (int i = 0; i <executors.size() ; i++) {
            executors.get(i).Resize();
        }
    }
}
