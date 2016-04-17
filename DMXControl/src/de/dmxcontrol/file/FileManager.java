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

    private final static String DMXCRootStorageName = "DMXControl";
    private final static String ImageStorageName = File.separator + "Images";
    private final static String IconStorageName = File.separator + "Icons";
    private final static String TexturesStorageName = File.separator + "Textures";
    private final static String GoboStorageName = File.separator + "Gobos";
    private final static String LogsStorageName = File.separator + "Logs";

    private static String StoragePath;

    private final static String[] DefaultFiles = new String[]{
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


    public String getImagePath() {
        return StoragePath + ImageStorageName;
    }

    public String getIconPath() {
        return StoragePath + IconStorageName;
    }

    public String getTexturePath() {
        return StoragePath + TexturesStorageName;
    }

    public String getGoboPath() {
        return StoragePath + GoboStorageName;
    }

    public String getLogPath() {
        return StoragePath + LogsStorageName;
    }

    private FileManager() {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            StoragePath = Environment.getExternalStorageDirectory() + File.separator + DMXCRootStorageName;
        }
        else {
            StoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + DMXCRootStorageName;
        }

        createDirectory();
        createDefaultIcons();
        loadTextures();
    }

    private void createDirectory() {
        try {

            File Directory = new File(StoragePath);

            if(!Directory.isDirectory()) {
                if(Directory.mkdirs()) {
                    Log.i(TAG, "Create directory [" + StoragePath + "]");
                }
                else {
                    Log.e(TAG, "Directory [" + StoragePath + "] not created");
                    DMXControlApplication.SaveLog();
                    // TODO: 11.10.15 do something here -> inform user -> need this folder for textures
                }
            }

            Directory = new File(getTexturePath());
            if(!Directory.isDirectory()) {
                if(Directory.mkdirs()) {
                    Log.i(TAG, "Create directory [" + getTexturePath() + "]");
                }
                else {
                    Log.i(TAG, "Directory [" + getTexturePath()  + "] not created");
                }
            }

            Directory = new File(getImagePath());
            if(!Directory.isDirectory()) {
                if(Directory.mkdirs()) {
                    Log.i(TAG, "Create directory [" + getImagePath() + "]");
                }
                else {
                    Log.i(TAG, "Directory [" + getImagePath() + "] not created");
                }
            }

            Directory = new File(getIconPath());
            if(!Directory.isDirectory()) {
                if(Directory.mkdirs()) {
                    Log.i(TAG, "Create directory [" + getIconPath() + "]");
                }
                else {
                    Log.i(TAG, "Directory [" + getIconPath() + "] not created");
                }
            }

            Directory = new File(getGoboPath());
            if(!Directory.isDirectory()) {
                if(Directory.mkdirs()) {
                    Log.i(TAG, "Create directory [" + getGoboPath() + "]");
                }
                else {
                    Log.i(TAG, "Directory [" + getGoboPath()  + "] not created");
                }
            }

            Directory = new File(getLogPath());
            if(!Directory.isDirectory()) {
                if(Directory.mkdirs()) {
                    Log.i(TAG, "Create directory [" + getLogPath() + "]");
                }
                else {
                    Log.i(TAG, "Directory [" + getLogPath()  + "] not created");
                }
            }
        }
        catch(Exception e) {
            Log.e(TAG, "Exception at create directory in [" + StoragePath + "]");
        }
    }

    private void createDefaultIcons() {
        try {
            if(context == null) {
                return;
            }
            FileOutputStream out = null;
            for(String string : DefaultFiles) {
                File file = new File(getTexturePath() + File.separator + string);
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
                        out = new FileOutputStream(getTexturePath() + File.separator + string);
                        Log.i(TAG, "Create Bitmap " + string);
                    }
                    catch(Exception e) {
                        Log.w(TAG, "Exception at create Bitmap [" + string + "]");
                        Log.e(TAG, DMXControlApplication.stackTraceToString(e));
                        DMXControlApplication.SaveLog();
                    }

                    if(bmp != null) {
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                }
            }
        }
        catch(Exception e) {
            Log.w(TAG, "Exception at create Bitmaps createDefaultIcons()");
            Log.e(TAG, DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }

    private void loadTextures() {
        images.add(new ImageWithKey(loadTexture(OpticControl.Lens1), OpticControl.Lens1));
        images.add(new ImageWithKey(loadTexture(OpticControl.Lens2), OpticControl.Lens2));
        images.add(new ImageWithKey(loadTexture(OpticControl.Lens3), OpticControl.Lens3));
        images.add(new ImageWithKey(loadTexture(OpticControl.Frost), OpticControl.Frost));
        images.add(new ImageWithKey(loadTexture(OpticControl.FocusWheel), OpticControl.FocusWheel));
    }

    private Bitmap loadTexture(String name) {
        return BitmapFactory.decodeFile(new File(getTexturePath() + File.separator + name).getAbsolutePath());
    }
}