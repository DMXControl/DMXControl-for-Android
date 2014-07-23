package de.dmxcontrol.model;

import android.view.View;

import org.json.JSONException;

/**
 * Created by Qasi on 19.07.2014.
 */
public class ZoomModel extends BaseModel {
    private Integer zoom[] = new Integer[1];

    private final static float MAX_VALUE = 100f;

    public ZoomModel(ModelManager manager) {
        super(manager, ModelManager.Type.Zoom, "zoom");
        init();
    }

    private void init() {
        zoom[0] = (int) MAX_VALUE;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {
        zoom[0] = (int) (x * MAX_VALUE);
        try {
            SendData("Zoom", "double", x);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        notifyListener();
    }

    public Integer getValue() {
        return zoom[0];
    }

    public Float getWidgetValue() {
        return (float) zoom[0] / MAX_VALUE;
    }

    @Override
    public Object[] getOSCAttributes() {
        return zoom;
    }
}
