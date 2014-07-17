package de.dmxcontrol.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import org.json.JSONException;

import de.dmxcontrol.adapter.ProgrammerAdapter;
import de.dmxcontrol.adapter.ProgrammerSpinnerAdapter;
import de.dmxcontrol.android.R;
import de.dmxcontrol.network.ReceivedData;
import de.dmxcontrol.programmer.EntityProgrammer;

/**
 * Created by Qasi on 11.07.2014.
 */
public class ProgrammerFragment extends BasePanelFragment implements AbsListView.OnScrollListener {
    public final static String TAG = "programmerFragment";
    private View view;
    private ExpandableListView programmertList;

    // startup process initiated
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.programmer_fragment, container, false);
        programmertList = (ExpandableListView) view.findViewById(R.id.programmer_list);
        view.findViewById(R.id.programmer_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EntityProgrammer.Clear(ReceivedData.get().SelectedProgrammer);
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        view.findViewById(R.id.programmer_undoclear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EntityProgrammer.Undo(ReceivedData.get().SelectedProgrammer);
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        programmertList.setAdapter(new ProgrammerAdapter(getActivity().getBaseContext()));
        programmertList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {

                return false;
            }
        });
        programmertList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        ((Spinner) view.findViewById(R.id.programmer_spinner)).setAdapter(new ProgrammerSpinnerAdapter(view.getContext()));
        programmertList.setOnScrollListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // startup process done

    // teardown process initiated
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clean();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void clean() {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }
}
