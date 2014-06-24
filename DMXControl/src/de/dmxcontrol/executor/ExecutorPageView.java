package de.dmxcontrol.executor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.Entity;
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
    private EntityExecutorPage selectedExecutorPage;
    private Context Context;
    private Activity parent;

    public ExecutorPageView(int id, String name, Context context) throws Exception {
        try {
            this.Context=context;
            view = View.inflate(context, R.layout.executor_page, null);

            up_btn = (Button) view.findViewById(R.id.executorPageUp_btn);
            up_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "octicons.ttf"));
            up_btn.setText("\uf03d");
            up_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (ResceivdData.get().ExecutorPages.size() > ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) + 1)
                            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) + 1);
                        LoadExecutors();
                    } catch (Exception e) {
                        Log.w("", DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                }
            });
            up_btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        if (ResceivdData.get().ExecutorPages.size() - 1 != ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage))
                            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(ResceivdData.get().ExecutorPages.size() - 1);
                        LoadExecutors();
                    } catch (Exception e) {
                        Log.w("", DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                    return true;
                }
            });
            down_btn = (Button) view.findViewById(R.id.executorPageDown_btn);
            down_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "octicons.ttf"));
            down_btn.setText("\uf03f");
            down_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (0 <= ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) - 1)
                            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage) - 1);
                        LoadExecutors();
                    } catch (Exception e) {
                        Log.w("", DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                }
            });
            down_btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        if (0 != ResceivdData.get().ExecutorPages.indexOf(ResceivdData.get().SelectedExecutorPage))
                            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(0);
                        LoadExecutors();
                    } catch (Exception e) {
                        Log.w("", DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                    return true;
                }
            });
            name_label = (TextView) view.findViewById(R.id.executorPageName);
            name_label.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(Context);
                    alert.setTitle("Rename");
                    alert.setMessage("Enter Executor Page Name");
                    final EditText input = new EditText(Context);
                    input.setText(name_label.getText());
                    alert.setView(input);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String str = input.getEditableText().toString();
                            ResceivdData.get().SelectedExecutorPage.setName(str);
                            Toast.makeText(Context, str, Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    return true;
                }
            });
            hsview = (ExecuterPageSliderView) view.findViewById(R.id.horizontalScrollView);
            llview = (ExecuterPageMultitouchLayout) hsview.findViewById(R.id.executor_collection);

            LoadExecutors();
        } catch (Exception e) {
            Log.w("", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }

    public void LoadExecutors() throws InterruptedException {
        if(ResceivdData.get().ExecutorPages.size()==0||ResceivdData.get().Executors.size()==0)
        {
            EntityExecutor.SendAllRequest();
            Thread.sleep(100);
            EntityExecutorPage.SendAllRequest();
            Thread.sleep(100);
            if(ResceivdData.get().ExecutorPages.size()==0||ResceivdData.get().Executors.size()==0) {
                return;
            }
        }
        if (ResceivdData.get().SelectedExecutorPage == null) {
            ResceivdData.get().SelectedExecutorPage = ResceivdData.get().ExecutorPages.get(0);
        }
        if(selectedExecutorPage!=null){((Entity)selectedExecutorPage).removeNameChangedListeners();}
        selectedExecutorPage=ResceivdData.get().SelectedExecutorPage;
        ((Entity)selectedExecutorPage).setNameChangedListener(new Entity.NameChangedListener() {
            @Override
            public void onNameChanged(String name) {
                if (parent != null)
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name_label.setText(selectedExecutorPage.getName());
                        }
                    });
            }
        });
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
        this.parent=parent;
        if (executors != null)
            for (int i = 0; i < executors.size(); i++) {
                executors.get(i).parentActivity = parent;
            }
    }

    public ArrayList<ExecutorView> getExecutors() {
        return executors;
    }
}