package de.dmxcontrol.device;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.dmxcontrol.android.R;
import de.dmxcontrol.file.FileManager;

/**
 * Created by Qasi on 29.06.2014.
 */
public class DeviceProperty {
    private boolean isGobo, isColor, isRawStep;
    public static String NetworkID = "DeviceProperty";
    private String guid;
    private String name;
    private String valueIndex;
    private DevicePropertyValue[] values;

    public DeviceProperty(JSONObject o) throws Exception {
        if(!o.has("Type")) {
            throw new Exception("Type not found!");
        }
        if(!o.get("Type").equals(NetworkID)) {
            throw new Exception("Type isn't " + NetworkID);
        }
        this.guid = o.getString("GUID");
        this.name = o.getString("Name");

        if(this.name.equals("GoboProperty")) {
            isGobo = true;
        }
        else if(this.name.equals("ColorProperty")) {
            isColor = true;
        }
        else {
            isRawStep = true;
        }

        if(o.has("Values") && !o.isNull("Values")) {
            JSONArray array = o.getJSONArray("Values");
            this.values = new DevicePropertyValue[array.length()];
            for(int i = 0; i < array.length(); i++) {
                this.values[i] = new DevicePropertyValue(array.getJSONObject(i), this.name);
            }
        }

        if(!o.isNull("Value")) {
            setValue(o.getString("Value"));
        }
    }

    public class DevicePropertyValue {
        private boolean isGobo, isColor, isRawStep;
        private String name, value;
        private String index;

        public DevicePropertyValue(JSONObject o, String type) throws JSONException {
            if(type.equals("GoboProperty")) {
                isGobo = true;
                this.value = o.getString("File");
            }
            else if(type.equals("ColorProperty")) {
                isColor = true;
                this.value = o.getString("Color");
            }
            else {
                isRawStep = true;
            }
            this.name = o.getString("Name");
            this.index = o.getString("Index");
        }

        public boolean isGobo() {
            return this.isGobo;
        }

        public boolean isColor() {
            return this.isColor;
        }

        public boolean isRawStep() {
            return this.isRawStep;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public String getIndex() {
            return this.index;
        }

        public Bitmap getImage(Context context) {
            try {
                if(this.name.equals("Open") && this.value.equals("null")) {
                    Bitmap open = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(open);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(open.getWidth() / 2, open.getHeight() / 2, open.getWidth() / 2 * 0.95f, paint);
                    return open;
                }
                File imgFile = new File(FileManager.IconStorageName + File.separator + this.value);
                if(imgFile.isFile()) {
                    if(imgFile.exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if(bmp.getHeight() > 128 || bmp.getWidth() > 128) {
                            bmp = Bitmap.createScaledBitmap(bmp, 128, 128, false);
                        }
                        return bmp;
                    }
                }
            }
            catch(Exception e) {
            }
            // Replace this icon with something else
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
        }
    }

    public String getGUID() {
        return this.guid;
    }

    public String getName() {
        return this.name;
    }

    public String getValueIndex() {
        return valueIndex;
    }

    public String getalueIndex() {
        return this.valueIndex;
    }

    public DevicePropertyValue[] getValues() {
        return this.values;
    }

    public void setValue(DevicePropertyValue value) {
        this.valueIndex = value.index;
    }

    public void setValues(DevicePropertyValue[] values) {
        this.values = values;
    }

    public void setValue(String index) {
        this.valueIndex = index;
    }

    public boolean isGobo() {
        return this.isGobo;
    }

    public boolean isColor() {
        return this.isColor;
    }

    public boolean isRawStep() {
        return this.isRawStep;
    }
}
