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

/**
 * Created by Qasi on 15.06.2014.
 */
public class FileManager {
    private final static String StoragePath = Environment.getExternalStorageDirectory() + File.separator + "DMXControl";
    private final static String IconStorageName = StoragePath + File.separator + "Icons";

    private final static String[] DefaultFiles = new String[]{
            EntityDevice.defaultDeviceIcon,
            EntityGroup.defaultDeviceGroupIcon};

    private static Context context;
    private static FileManager INSTANCE = null;

    public static FileManager get() {
        if (INSTANCE == null) {
            INSTANCE = new FileManager();
        }
        return INSTANCE;
    }

    public static FileManager get(Context ctx) {
        if (context == null) {
            context = ctx;
        }
        if (INSTANCE == null) {
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
    }

    private static void createDirectory() {
        try {
            File Directory = new File(StoragePath);
            if (!Directory.isDirectory()) {
                Directory.mkdirs();
            }
            Directory = new File(IconStorageName);
            if (!Directory.isDirectory()) {
                Directory.mkdirs();
            }
        } catch (Exception e) {
        }
    }

    private static void createDefaultIcons() {
        try {
            if (context == null) {
                return;
            }
            FileOutputStream out = null;
            for (String string : DefaultFiles) {
                File file = new File(IconStorageName + File.separator + string);
                if (!file.exists()) {
                    Bitmap bmp = null;
                    if (string.equals(EntityDevice.defaultDeviceIcon)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.device_new);
                    } else if (string.equals(EntityGroup.defaultDeviceGroupIcon)) {
                        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.device_group_new);
                    }

                    try {
                        out = new FileOutputStream(IconStorageName + File.separator + string);
                    } catch (Exception e) {
                        Log.e("", DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
            }
        } catch (Exception e) {
            Log.e("", DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }
}