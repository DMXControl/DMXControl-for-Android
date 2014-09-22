package de.dmxcontrol.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.widget.OpticControl;

/**
 * Created by Qasi on 15.06.2014.
 */
public class FileManager {
    public final static String TAG = "FileManager";
    public final static String StoragePath = Environment.getExternalStorageDirectory() + File.separator + "DMXControl";
    public final static String ImageStorageName = StoragePath + File.separator + "Images";
    public final static String TexturesStorageName = StoragePath + File.separator + "Textures";
    public final static String GoboStorageName = StoragePath + File.separator + "Gobos";
    public final static String LogsStorageName = StoragePath + File.separator + "Logs";

    public final static String[] DefaultFiles = new String[]{
            EntityDevice.defaultDeviceIcon,
            EntityGroup.defaultDeviceGroupIcon,
            OpticControl.Lens1,
            OpticControl.Lens2,
            OpticControl.Lens3,
            OpticControl.Frost,
            OpticControl.FocusWheel
    };


    private ImageWithKeyCollection images = new ImageWithKeyCollection();

    public Bitmap getImage(String key) {
        return images.get(key).getBitmap();
    }

    private static Context context;
    private static FileManager INSTANCE = null;

    public static FileManager get() {
        if(INSTANCE == null) {
            INSTANCE = new FileManager();
        }
        return INSTANCE;
    }

    public static FileManager get(Context ctx) {
        if(context == null) {
            context = ctx;
        }
        if(INSTANCE == null) {
            INSTANCE = new FileManager();
        }
        return INSTANCE;
    }

    public static Context getContext() {
        return context;
    }

    private FileManager() {
        createDirectory();
        createDefaultIcons();
        loadImages();
    }

    private static void createDirectory() {
        try {
            File Directory = new File(StoragePath);
            if(!Directory.isDirectory()) {
                Directory.mkdirs();
                Log.i(TAG, "Create directory [" + StoragePath + "]");
            }
            Directory = new File(TexturesStorageName);
            if(!Directory.isDirectory()) {
                Directory.mkdirs();
                Log.i(TAG, "Create directory [" + TexturesStorageName + "]");
            }
            Directory = new File(ImageStorageName);
            if(!Directory.isDirectory()) {
                Directory.mkdirs();
                Log.i(TAG, "Create directory [" + ImageStorageName + "]");
            }
            Directory = new File(GoboStorageName);
            if(!Directory.isDirectory()) {
                Directory.mkdirs();
                Log.i(TAG, "Create directory [" + GoboStorageName + "]");
            }
            Directory = new File(LogsStorageName);
            if(!Directory.isDirectory()) {
                Directory.mkdirs();
                Log.i(TAG, "Create directory [" + LogsStorageName + "]");
            }
        }
        catch(Exception e) {
            Log.e(TAG, "Exception at create directory in [" + StoragePath + "]");
        }
    }

    private static void createDefaultIcons() {
        try {
            if(context == null) {
                return;
            }
            FileOutputStream out = null;
            for(String string : DefaultFiles) {
                File file = new File(TexturesStorageName + File.separator + string);
                if(!file.exists()) {
                    Bitmap bmp = null;
                    if(string.equals(EntityDevice.defaultDeviceIcon)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.device_new);
                    }
                    else if(string.equals(EntityGroup.defaultDeviceGroupIcon)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.device_group_new);
                    }
                    else if(string.equals(OpticControl.Lens1)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.optic_lens_1);
                    }
                    else if(string.equals(OpticControl.Lens2)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.optic_lens_2);
                    }
                    else if(string.equals(OpticControl.Lens3)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.optic_lens_3);
                    }
                    else if(string.equals(OpticControl.Frost)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.optic_frost);
                    }
                    else if(string.equals(OpticControl.FocusWheel)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.optic_focus_wheel);
                    }

                    try {
                        out = new FileOutputStream(TexturesStorageName + File.separator + string);
                        Log.i(TAG, "Create Bitmap " + string);
                    }
                    catch(Exception e) {
                        Log.w(TAG, "Exception at create Bitmap [" + string + "]");
                        Log.e(TAG, DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
            }
        }
        catch(Exception e) {
            Log.w(TAG, "Exception at create Bitmaps createDefaultIcons()");
            Log.e(TAG, DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }

    private void loadImages() {
        images.add(new ImageWithKey(loadImage(OpticControl.Lens1), OpticControl.Lens1));
        images.add(new ImageWithKey(loadImage(OpticControl.Lens2), OpticControl.Lens2));
        images.add(new ImageWithKey(loadImage(OpticControl.Lens3), OpticControl.Lens3));
        images.add(new ImageWithKey(loadImage(OpticControl.Frost), OpticControl.Frost));
        images.add(new ImageWithKey(loadImage(OpticControl.FocusWheel), OpticControl.FocusWheel));
    }

    private Bitmap loadImage(String name) {
        return BitmapFactory.decodeFile(new File(TexturesStorageName + File.separator + name).getAbsolutePath());
    }
}