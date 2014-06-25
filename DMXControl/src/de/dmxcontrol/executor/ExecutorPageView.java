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
import de.dmxcontrol.network.ReceivedData;
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
    private Context Context;

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
                        if (ReceivedData.get().ExecutorPages.size() > ReceivedData.get().ExecutorPages.indexOf(ReceivedData.get().SelectedExecutorPage) + 1)
                            ReceivedData.get().SelectedExecutorPage = ReceivedData.get().ExecutorPages.get(ReceivedData.get().ExecutorPages.indexOf(ReceivedData.get().SelectedExecutorPage) + 1);
                        LoadExecutors();
                    }
                    catch (Exception e) {
                        Log.w("", DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                }
            });
            up_btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        if (ReceivedData.get().ExecutorPages.size() - 1 != ReceivedData.get().ExecutorPages.indexOf(ReceivedData.get().SelectedExecutorPage))
                            ReceivedData.get().SelectedExecutorPage = ReceivedData.get().ExecutorPages.get(ReceivedData.get().ExecutorPages.size() - 1);
                        LoadExecutors();
                    }
                    catch (Exception e) {
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
                        if (0 <= ReceivedData.get().ExecutorPages.indexOf(ReceivedData.get().SelectedExecutorPage) - 1)
                            ReceivedData.get().SelectedExecutorPage = ReceivedData.get().ExecutorPages.get(ReceivedData.get().ExecutorPages.indexOf(ReceivedData.get().SelectedExecutorPage) - 1);
                        LoadExecutors();
                    }
                    catch (Exception e) {
                        Log.w("", DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                }
            });
            down_btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        if (0 != ReceivedData.get().ExecutorPages.indexOf(ReceivedData.get().SelectedExecutorPage))
                            ReceivedData.get().SelectedExecutorPage = ReceivedData.get().ExecutorPages.get(0);
                        LoadExecutors();
                    }
                    catch (Exception e) {
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
                    if (ReceivedData.get().SelectedExecutorPage != null) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Context);
                        alert.setTitle("Rename");
                        alert.setMessage("Enter Executor Page Name");
                        final EditText input = new EditText(Context);
                        input.setText(name_label.getText());
                        alert.setView(input);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String str = input.getEditableText().toString();
                                ReceivedData.get().SelectedExecutorPage.setName(str,false);
                                name_label.setText(ReceivedData.get().SelectedExecutorPage.getName());
                                Toast.makeText(Context, str, Toast.LENGTH_LONG).show();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                        return true;
                    }
                    return false;
                }
            });
            hsview = (ExecuterPageSliderView) view.findViewById(R.id.horizontalScrollView);
            llview = (ExecuterPageMultitouchLayout) hsview.findViewById(R.id.executor_collection);

            LoadExecutors();
        }
        catch (Exception e) {
            Log.w("", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }

    public void LoadExecutors() {
        if (ReceivedData.get().SelectedExecutorPage == null) {
            ReceivedData.get().SelectedExecutorPage = ReceivedData.get().ExecutorPages.get(0);
        }
        ReceivedData.get().SelectedExecutorPage.removeNameChangedListeners();
        ReceivedData.get().SelectedExecutorPage.setNameChangedListener(new Entity.NameChangedListener() {
            @Override
            public void onNameChanged(String name) {
                name_label.setText(ReceivedData.get().SelectedExecutorPage.getName());
            }
        });
        name_label.setText(ReceivedData.get().SelectedExecutorPage.getName());
        if (executors == null) {
            executors = new ArrayList<ExecutorView>();
            for (int i = 0; i < ReceivedData.get().Executors.size(); i++) {
                ExecutorView newView = new ExecutorView(llview.getContext(), ReceivedData.get().Executors.get(i));
                executors.add(newView);
                llview.addView(newView);

                //executors.get(i).Resice();
            }
        } else if (executors.size() != ReceivedData.get().Executors.size()) {
            for (int i = 0; i < ReceivedData.get().Executors.size(); i++) {
                if (executors.size() < i) {
                    if (!ReceivedData.get().Executors.contains(executors.get(i).getEntityExecutor())) {
                        ExecutorView newView = new ExecutorView(llview.getContext(), ReceivedData.get().Executors.get(i));
                        executors.add(newView);
                        llview.addView(newView);
                    }
                }
                else {
                    ExecutorView newView = new ExecutorView(llview.getContext(), ReceivedData.get().Executors.get(i));
                    executors.add(newView);
                    llview.addView(newView);
                }
                //executors.get(i).Resice();
            }
        }
        else {
            for (int i = 0; i < executors.size(); i++) {
                //llview.addView(executors.get(i));
            }
        }

        llview.removeAllViews();

        for (int i = 0; i <executors.size() ; i++) {
             if(ReceivedData.get().SelectedExecutorPage.getExecutors().contains(executors.get(i).getEntityExecutor().guid)){
                 llview.addView(executors.get(i));
             }
        }

        hsview.setEnabled(false);
        llview.executorPage = this;
    }

    public void Resize() {
        if (executors != null) {
            for (int i = 0; i < executors.size(); i++) {
                executors.get(i).Resize();
            }
        }
    }

    public View getView() {
        return view;
    }

    public ExecuterPageSliderView getExecuterPageSliderView() {
        return hsview;
    }

    public void SetParentActivity(Activity parent) {
        if (executors != null) {
            for (int i = 0; i < executors.size(); i++) {
                executors.get(i).parentActivity = parent;
            }
        }
    }

    public ArrayList<ExecutorView> getExecutors() {
        return executors;
    }
}