package de.dmxcontrol.model;

import android.view.View;

import org.json.JSONException;

/**
 * Created by Qasi on 19.07.2014.
 */
public class GoboModel extends BaseModel {
    private String gobo[] = new String[1];

    public GoboModel(ModelManager manager) {
        super(manager, ModelManager.Type.Gobo, "gobo");
        init();
    }

    private void init() {
        gobo[0] = "";
    }

    public void setValue(String value) {
        gobo[0] = value;
        try {
            SendData("Gobo", "string", value + "");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        notifyListener();
    }

    @Override
    public void onValueChanged(View v, float x, float y) {

    }

    public String getValue() {
        return gobo[0];
    }

    @Override
    public Object[] getOSCAttributes() {
        return gobo;
    }
}
