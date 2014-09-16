package de.dmxcontrol.device;

import org.json.JSONObject;

/**
 * Created by Qasi on 29.06.2014.
 */
public class DeviceProperty {
    public static String NetworkID = "DeviceProperty";
    private String guid;
    private String name;
    private String xmlfilename;
    private Object value;

    public DeviceProperty(JSONObject o) throws Exception {
        if(!o.has("Type")) {
            throw new Exception("Type not found!");
        }
        if(!o.get("Type").equals(NetworkID)) {
            throw new Exception("Type isn't " + NetworkID);
        }
        this.guid = o.getString("GUID");
        this.name = o.getString("Name");
        this.value = o.getString("Value");
        if(o.has("XMLFile")) {
            this.xmlfilename = o.getString("XMLFile");
        }
    }

    public String getGUID() {
        return this.guid;
    }

    public String getName() {
        return this.name;
    }

    public String getXMLName() {
        return this.xmlfilename + "";
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
