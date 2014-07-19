package de.dmxcontrol.model;

import android.view.View;

import org.json.JSONException;

/**
 * Created by Qasi on 19.07.2014.
 */
public class FrostModel extends BaseModel {
    private Integer frost[] = new Integer[1];

    private final static float MAX_VALUE = 100f;

    public FrostModel(ModelManager manager) {
        super(manager, ModelManager.Type.Frost, "frost");
        init();
    }

    private void init() {
        frost[0] = 0;
    }

    @Override
    public void onValueChanged(View v, float x, float y) {
        frost[0] = (int) (x * MAX_VALUE);
        try {
            SendData("Frost", "double", x);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        notifyListener();
    }

    public Integer getValue() {
        return frost[0];
    }

    public Float getWidgetValue() {
        return (float) frost[0] / MAX_VALUE;
    }

    @Override
    public Object[] getOSCAttributes() {
        return frost;
    }
}
