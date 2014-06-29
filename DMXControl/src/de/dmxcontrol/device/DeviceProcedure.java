package de.dmxcontrol.device;

import org.json.JSONObject;

/**
 * Created by Qasi on 29.06.2014.
 */
public class DeviceProcedure {
    public static String NetworkID = "DeviceProcedure";
    private String name;
    private String icon;

    public DeviceProcedure(JSONObject o) throws Exception {
        if(!o.has("Type")) {
            throw new Exception("Type not found!");
        }
        if(!o.get("Type").equals(NetworkID)) {
            throw new Exception("Type isn't " + NetworkID);
        }
        this.name = o.getString("Name");
        this.icon = o.getString("Icon");
    }

    public String getName() {
        return this.name;
    }

    public Object getIcon() {
        return this.icon;
    }

}
