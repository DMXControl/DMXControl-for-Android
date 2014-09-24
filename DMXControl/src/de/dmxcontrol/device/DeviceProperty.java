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
public class DeviceProperty implements Cloneable {
    private boolean isGobo, isColor, isRawStep, isBeam, isPrism, isOptical;
    private String guid;
    private String name;
    private String valueIndex;
    private DevicePropertyValue[] values;

    private DeviceProperty() {
    }

    public DeviceProperty(JSONObject o) throws Exception {
        this.name = o.getString("Name");
        if(o.has("GUID")) {
            this.guid = o.getString("GUID");
        }

        if(this.name.equals("Gobo")) {
            isGobo = true;
        }
        else if(this.name.contains("Color")) {
            isColor = true;
        }
        else if(this.name.contains("Dimmer") || this.name.contains("Shutter") || this.name.contains("Strobe")) {
            isBeam = true;
        }
        else if(this.name.contains("Prism")) {
            isPrism = true;
        }
        else if(this.name.contains("Zoom") || this.name.contains("Iris") || this.name.contains("Frost") || this.name.contains("Focus")) {
            isOptical = true;
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
        if(o.has("Value")) {
            if(!o.isNull("Value")) {
                setValue(o.getString("Value"));
            }
        }
    }

    public class DevicePropertyValue {
        private boolean isGobo, isColor, isRawStep, isBeam, isPrism, isOptical;
        private String name, value;
        private String index;

        public DevicePropertyValue(JSONObject o, String type) throws JSONException {
            if(type.equals("Gobo")) {
                isGobo = true;
                this.value = o.getString("File");
            }
            else if(type.contains("Color")) {
                isColor = true;
                this.value = o.getString("Color");
            }
            else if(type.contains("Dimmer") || type.contains("Shutter") || type.contains("Strobe")) {
                isBeam = true;
                this.value = o.getString("Value");
            }
            else if(type.contains("Prism")) {
                isPrism = true;
                this.value = o.getString("Value");
            }
            else if(type.contains("Zoom") || type.contains("Iris") || type.contains("Frost") || type.contains("Focus")) {
                isOptical = true;
                this.value = o.getString("Value");
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

        public boolean isOptical() {
            return this.isOptical;
        }

        public boolean isBeam() {
            return this.isBeam;
        }

        public boolean isPrism() {
            return this.isPrism;
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

    public boolean isOptical() {
        return this.isOptical;
    }

    public boolean isBeam() {
        return this.isBeam;
    }

    public boolean isPrism() {
        return this.isPrism;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    /**public DeviceProperty clone(){
     DeviceProperty clone = null;
     try {
     clone = new DeviceProperty();
     clone.name=this.name;
     clone.valueIndex=this.valueIndex;
     clone.values=clone.values.clone();
     clone.guid=this.guid;
     clone.isBeam=this.isBeam;
     clone.isColor=this.isColor;
     clone.isGobo=this.isGobo;
     clone.isOptical=this.isOptical;
     clone.isPrism=this.isPrism;
     clone.isRawStep=this.isRawStep;
     }
     catch(Exception e) {
     e.printStackTrace();
     }
     return clone;
     }**/
}
