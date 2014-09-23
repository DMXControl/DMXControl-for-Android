package de.dmxcontrol.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import de.dmxcontrol.adapter.GoboAdapter;
import de.dmxcontrol.android.R;
import de.dmxcontrol.device.DeviceProperty;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.model.GoboModel;
import de.dmxcontrol.model.ModelManager;
import de.dmxcontrol.widget.FaderHorizontalControl;

/**
 * Created by Qasi on 11.07.2014.
 */
public class GoboFragment extends BasePanelFragment implements AdapterView.OnItemClickListener {
    public final static String TAG = "goboFragment";
    private View view;

    private GoboModel goboModel;
    private FaderHorizontalControl faderRotation, faderIndex, faderShake;
    private GridView gridView;

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
        view = inflater.inflate(R.layout.gobo_fragment, container, false);
        gridView = (GridView) view.findViewById(R.id.gobo_fragment_gridView);
        gridView.setAdapter(new GoboAdapter(view.getContext()));
        gridView.setOnItemClickListener(this);

        faderRotation = (FaderHorizontalControl) view.findViewById(R.id.gobo_rotation_fader);
        faderIndex = (FaderHorizontalControl) view.findViewById(R.id.gobo_index_fader);
        faderShake = (FaderHorizontalControl) view.findViewById(R.id.gobo_shake_fader);
        faderRotation.setValue(0.5f, 0.5f);
        faderIndex.setValue(0.5f, 0.5f);
        faderShake.setValue(0.5f, 0.5f);

        goboModel = (GoboModel) EntityManager.get().getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(ModelManager.Type.Gobo);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        Object item = adapterView.getItemAtPosition(position);
        goboModel.setValue(((DeviceProperty.DevicePropertyValue) item).getIndex());
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
}
