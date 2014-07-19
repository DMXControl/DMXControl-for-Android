package de.dmxcontrol.model;

import android.view.View;

import org.json.JSONException;

/**
 * Created by Qasi on 19.07.2014.
 */
public class FocusModel extends BaseModel {
    private Integer focus[] = new Integer[1];

    private final static float MAX_VALUE = 100f;

    public FocusModel(ModelManager manager) {
        super(manager, ModelManager.Type.Focus, "focus");
        init();
    }

    private void init() {
        focus[0] = 0;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {
        focus[0] = (int) (x * MAX_VALUE);
        try {
            SendData("Focus", "double", x);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        notifyListener();
    }

    public Integer getValue() {
        return focus[0];
    }

    public Float getWidgetValue() {
        return (float) focus[0] / MAX_VALUE;
    }

    @Override
    public Object[] getOSCAttributes() {
        return focus;
    }
}
