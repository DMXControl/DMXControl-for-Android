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
