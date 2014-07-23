package de.dmxcontrol.model;

import android.view.View;

import org.json.JSONException;

/**
 * Created by Qasi on 19.07.2014.
 */
public class IrisModel extends BaseModel {
    private Integer iris[] = new Integer[1];

    private final static float MAX_VALUE = 100f;

    public IrisModel(ModelManager manager) {
        super(manager, ModelManager.Type.Iris, "iris");
        init();
    }

    private void init() {
        iris[0] = (int) MAX_VALUE;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {
        iris[0] = (int) (x * MAX_VALUE);
        try {
            SendData("Iris", "double", x);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        notifyListener();
    }

    public Integer getValue() {
        return iris[0];
    }

    public Float getWidgetValue() {
        return (float) iris[0] / MAX_VALUE;
    }

    @Override
    public Object[] getOSCAttributes() {
        return iris;
    }
}
