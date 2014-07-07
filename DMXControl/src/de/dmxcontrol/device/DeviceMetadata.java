package de.dmxcontrol.device;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Qasi on 03.07.2014.
 */
public class DeviceMetadata {

    private String Vendor, Modell, Author, Type, XmlFile, Comment;
    private int DMXAddressCount;

    public String getVendor() {
        return this.Vendor;
    }

    public String getModell() {
        return this.Modell;

    }

    public String getAuthor() {
        return this.Author;
    }

    public String getType() {
        return this.Type;
    }

    public String getXmlFile() {
        return this.XmlFile;
    }

    public String getComment() {
        return this.Comment;
    }

    public int getDMXAddressCount() {
        return this.DMXAddressCount;
    }

    public String getDescription() {
        String out = "Author: " + this.Author + "\n" +
                "Count: " + this.DMXAddressCount + "\n" +
                "File: " + this.XmlFile;
        if(this.Comment.equals("") || this.Comment.equals("null")) {
            return out;
        }
        else {
            return out + "\n" +
                    "Comment: " + this.Comment;
        }
    }

    public DeviceMetadata(JSONObject o) throws JSONException {
        this.Vendor = o.getString("Vendor");
        this.Modell = o.getString("Modell");
        this.Author = o.getString("Author");
        this.Type = o.getString("Type");
        this.XmlFile = o.getString("XmlFile");
        this.Comment = o.getString("Comment");
        this.DMXAddressCount = o.getInt("DMXAddressCount");
    }
}
