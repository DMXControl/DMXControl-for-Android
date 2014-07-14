package de.dmxcontrol.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import de.dmxcontrol.adapter.PresetAdapter;
import de.dmxcontrol.android.R;
import de.dmxcontrol.preset.EntityPreset;

/**
 * Created by Qasi on 11.07.2014.
 */
public class PresetFragment extends BasePanelFragment implements AbsListView.OnScrollListener {
    public final static String TAG = "prismFragment";
    private View view;
    private ListView presetList;

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
        view = inflater.inflate(R.layout.preset_fragment, container, false);
        presetList = (ListView) view.findViewById(R.id.preset_list);
        presetList.setAdapter(new PresetAdapter());
        presetList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
                final EntityPreset preset = (EntityPreset) parent.getItemAtPosition(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Rename");
                alert.setMessage("Enter Name");
                final EditText input = new EditText(view.getContext());
                input.setText(preset.getName());
                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = input.getEditableText().toString();
                        preset.setName(str, false);
                        Toast.makeText(view.getContext(), "Name: " + str, Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });
        presetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final EntityPreset preset = (EntityPreset) parent.getItemAtPosition(position);
                try {
                    preset.Execute();
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        presetList.setOnScrollListener(this);
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
