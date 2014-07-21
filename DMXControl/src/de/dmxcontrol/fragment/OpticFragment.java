package de.dmxcontrol.fragment;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.device.EntityManager;
import de.dmxcontrol.model.FocusModel;
import de.dmxcontrol.model.FrostModel;
import de.dmxcontrol.model.IrisModel;
import de.dmxcontrol.model.ModelManager;
import de.dmxcontrol.model.ZoomModel;
import de.dmxcontrol.widget.FaderVerticalControl;
import de.dmxcontrol.widget.OpticControl;

/**
 * Created by Qasi on 11.07.2014.
 */
public class OpticFragment extends BasePanelFragment {
    public final static String TAG = "opticFragment";
    private ZoomModel zoomModel;
    private FocusModel focusModel;
    private IrisModel irisModel;
    private FrostModel frostModel;

    private boolean mMultiToch;

    private View view;
    private OpticControl optic;
    private FaderVerticalControl faderZoom;
    private FaderVerticalControl faderFocus;
    private FaderVerticalControl faderIris;
    private FaderVerticalControl faderFrost;
    private TextView textZoom;
    private TextView textFocus;
    private TextView textIris;
    private TextView textFrost;

    // startup process initiated
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mMultiToch = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.optic_fragment, container, false);
        try {
            optic = ((OpticControl) view.findViewById(R.id.optic));
        }
        catch(Exception e) {

        }
        textZoom = ((TextView) view.findViewById(R.id.optic_zoom_fader_text));
        textFocus = ((TextView) view.findViewById(R.id.optic_focus_fader_text));
        textIris = ((TextView) view.findViewById(R.id.optic_iris_fader_text));
        textFrost = ((TextView) view.findViewById(R.id.optic_frost_fader_text));
        if(optic != null) {
            optic.setZoomChangedListener(new OpticControl.ValueChangedListener() {
                @Override
                public void onValueChanged(float value) {
                    ((FaderVerticalControl) view.findViewById(R.id.optic_zoom_fader)).setValue(1 - value, 1 - value);
                    zoomModel.onValueChanged(faderFocus, 1 - value, 1 - value);
                }
            });
            optic.setFocusChangedListener(new OpticControl.ValueChangedListener() {
                @Override
                public void onValueChanged(float value) {
                    ((FaderVerticalControl) view.findViewById(R.id.optic_focus_fader)).setValue(value, value);
                    focusModel.onValueChanged(faderIris, value, value);
                }
            });
            optic.setIrisChangedListener(new OpticControl.ValueChangedListener() {
                @Override
                public void onValueChanged(float value) {
                    ((FaderVerticalControl) view.findViewById(R.id.optic_iris_fader)).setValue(1 - value, 1 - value);
                    irisModel.onValueChanged(faderFrost, 1 - value, 1 - value);
                }
            });
            optic.setFrostChangedListener(new OpticControl.ValueChangedListener() {
                @Override
                public void onValueChanged(float value) {
                    ((FaderVerticalControl) view.findViewById(R.id.optic_frost_fader)).setValue(value, value);
                    frostModel.onValueChanged(faderZoom, value, value);
                }
            });
            if(this.mMultiToch) {
                optic.setGestureModeChangedListener(new OpticControl.ValueChangedListener() {
                    @Override
                    public void onValueChanged(float value) {
                        switch(optic.getGestureMode()) {
                            case (OpticControl.GESTURE_MODE_ZOOM):
                                textZoom.setPaintFlags(textZoom.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                textFocus.setPaintFlags(textFocus.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textIris.setPaintFlags(textIris.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textFrost.setPaintFlags(textFrost.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                break;
                            case (OpticControl.GESTURE_MODE_FOCUS):
                                textZoom.setPaintFlags(textZoom.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textFocus.setPaintFlags(textFocus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                textIris.setPaintFlags(textIris.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textFrost.setPaintFlags(textFrost.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                break;
                            case (OpticControl.GESTURE_MODE_IRIS):
                                textZoom.setPaintFlags(textZoom.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textFocus.setPaintFlags(textFocus.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textIris.setPaintFlags(textIris.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                textFrost.setPaintFlags(textFrost.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                break;
                            case (OpticControl.GESTURE_MODE_FROST):
                                textZoom.setPaintFlags(textZoom.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textFocus.setPaintFlags(textFocus.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textIris.setPaintFlags(textIris.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                textFrost.setPaintFlags(textFrost.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                break;
                        }
                    }
                });
                switch(optic.getGestureMode()) {
                    case (OpticControl.GESTURE_MODE_ZOOM):
                        textZoom.setPaintFlags(textZoom.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        break;
                    case (OpticControl.GESTURE_MODE_FOCUS):
                        textFocus.setPaintFlags(textFocus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        break;
                    case (OpticControl.GESTURE_MODE_IRIS):
                        textIris.setPaintFlags(textIris.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        break;
                    case (OpticControl.GESTURE_MODE_FROST):
                        textFrost.setPaintFlags(textFrost.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        break;
                }
                textZoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optic.setGestureMode(OpticControl.GESTURE_MODE_ZOOM);
                    }
                });
                textFocus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optic.setGestureMode(OpticControl.GESTURE_MODE_FOCUS);
                    }
                });
                textIris.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optic.setGestureMode(OpticControl.GESTURE_MODE_IRIS);
                    }
                });
                textFrost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optic.setGestureMode(OpticControl.GESTURE_MODE_FROST);
                    }
                });
            }
        }
        faderZoom = ((FaderVerticalControl) view.findViewById(R.id.optic_zoom_fader));
        faderZoom.setValueChangedListener(new FaderVerticalControl.ValueChangedListener() {
            @Override
            public void onValueChanged(float value, boolean isMoving) {
                if(optic != null) {
                    if(!isMoving) {
                        optic.setZoom(1 - value);
                    }
                    else {
                        optic.setZoomDirect(1 - value);
                    }
                }
                zoomModel.onValueChanged(faderFocus, 1 - value, 1 - value);
            }
        });
        faderFocus = ((FaderVerticalControl) view.findViewById(R.id.optic_focus_fader));
        faderFocus.setValueChangedListener(new FaderVerticalControl.ValueChangedListener() {
            @Override
            public void onValueChanged(float value, boolean isMoving) {
                if(optic != null) {
                    if(!isMoving) {
                        optic.setFocus(value);
                    }
                    else {
                        optic.setFocusDirect(value);
                    }
                }
                focusModel.onValueChanged(faderIris, value, value);
            }
        });
        faderIris = ((FaderVerticalControl) view.findViewById(R.id.optic_iris_fader));
        faderIris.setValueChangedListener(new FaderVerticalControl.ValueChangedListener() {
            @Override
            public void onValueChanged(float value, boolean isMoving) {
                if(optic != null) {
                    if(!isMoving) {
                        optic.setIris(1 - value);
                    }
                    else {
                        optic.setIrisDirect(1 - value);
                    }
                }
                irisModel.onValueChanged(faderFrost, 1 - value, 1 - value);
            }
        });
        faderFrost = ((FaderVerticalControl) view.findViewById(R.id.optic_frost_fader));
        faderFrost.setValueChangedListener(new FaderVerticalControl.ValueChangedListener() {
            @Override
            public void onValueChanged(float value, boolean isMoving) {
                if(optic != null) {
                    if(!isMoving) {
                        optic.setFrost(value);
                    }
                    else {
                        optic.setFrostDirect(value);
                    }
                }
                frostModel.onValueChanged(faderZoom, value, value);
            }
        });

        EntityManager entityManager = EntityManager.get();

        zoomModel = (ZoomModel) entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(ModelManager.Type.Zoom);
        focusModel = (FocusModel) entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(ModelManager.Type.Focus);
        irisModel = (IrisModel) entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(ModelManager.Type.Iris);
        frostModel = (FrostModel) entityManager.getEntitySelection(
                EntityManager.CENTRAL_ENTITY_SELECTION).getModel(ModelManager.Type.Frost);

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
