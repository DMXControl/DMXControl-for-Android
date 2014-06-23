package de.dmxcontrol.executor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.dmxcontrol.android.R;
import de.dmxcontrol.network.ResceivdData;
import de.dmxcontrol.widget.ExecuterPageMultitouchLayout;
import de.dmxcontrol.widget.ExecuterPageSliderView;
import de.dmxcontrol.widget.ExecutorView;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ExecutorPageView {

    private View view;
    private ExecuterPageSliderView hsview;
    private ExecuterPageMultitouchLayout llview;
    private Button up_btn;
    private Button down_btn;
    private TextView name_label;
    private ArrayList<ExecutorView> executors;

    public ExecutorPageView(int id, String name, Context context) throws Exception {
        try {

            view = View.inflate(context, R.layout.executor_page, null);
            if(ResceivdData.get().ExecutorPages.size()>0) {

                up_btn = (Button) view.findViewById(R.id.executorPageUp_btn);
                up_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "octicons.ttf"));
                up_btn.setText("\uf03d");
                up_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if (ResceivdData.get().ExecutorPages.size() > ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) + 1)
                            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) + 1);
                        LoadExecutors();
                    }
                });
                down_btn = (Button) view.findViewById(R.id.executorPageDown_btn);
                down_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "octicons.ttf"));
                down_btn.setText("\uf03f");
                down_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (0 <= ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) - 1)
                            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) - 1);
                        LoadExecutors();
                    }
                });
                name_label = (TextView) view.findViewById(R.id.executorPageName);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            }
            hsview = (ExecuterPageSliderView) view.findViewById(R.id.horizontalScrollView);
            llview = (ExecuterPageMultitouchLayout) hsview.findViewById(R.id.executor_collection);
            if(ResceivdData.get().ExecutorPages.size()>0) {LoadExecutors();}
        } catch (Exception e) {
            e.toString();
            throw new Exception(e.toString());
        }
    }

    public void LoadExecutors() {
        if (ResceivdData.get().SelectedExecutorPage == null) {
            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(0);
        }
        name_label.setText(ResceivdData.get().SelectedExecutorPage.getName());
        if (executors == null) {
            executors = new ArrayList<ExecutorView>();
            for (int i = 0; i < ResceivdData.get().Executors.size(); i++) {
                ExecutorView newView = new ExecutorView(llview.getContext(), ResceivdData.get().Executors.get(i));
                executors.add(newView);
                llview.addView(newView);

                //executors.get(i).Resice();
            }
        } else if (executors.size() != ResceivdData.get().Executors.size()) {
            for (int i = 0; i < ResceivdData.get().Executors.size(); i++) {
                if (executors.size() < i) {
                    if (!ResceivdData.get().Executors.contains(executors.get(i).getEntityExecutor())) {
                        ExecutorView newView = new ExecutorView(llview.getContext(), ResceivdData.get().Executors.get(i));
                        executors.add(newView);
                        llview.addView(newView);
                    }
                } else {
                    ExecutorView newView = new ExecutorView(llview.getContext(), ResceivdData.get().Executors.get(i));
                    executors.add(newView);
                    llview.addView(newView);
                }
                //executors.get(i).Resice();
            }
        } else {
            for (int i = 0; i < executors.size(); i++) {
                //llview.addView(executors.get(i));
            }
        }
        llview.removeAllViews();
        for (int i = 0; i <executors.size() ; i++) {
             if(ResceivdData.get().SelectedExecutorPage.getExecutors().contains(executors.get(i).getEntityExecutor().guid)){
                 llview.addView(executors.get(i));
             }
        }
        hsview.setEnabled(false);
        llview.executorPage = this;
    }

    public void Resize() {
        if (executors != null)
        for (int i = 0; i < executors.size(); i++) {
            executors.get(i).Resize();
        }
    }
    public View getView() {
        return view;
    }
    public ExecuterPageSliderView getExecuterPageSliderView() {
        return hsview;
    }
    public void SetParentActivity(Activity parent) {
        if (executors != null)
            for (int i = 0; i < executors.size(); i++) {
                executors.get(i).parentActivity = parent;
            }
    }

    public ArrayList<ExecutorView> getExecutors() {
        return executors;
    }
}