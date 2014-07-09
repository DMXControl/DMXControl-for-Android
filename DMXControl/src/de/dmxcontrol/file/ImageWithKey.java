package de.dmxcontrol.file;

import android.graphics.Bitmap;

/**
 * Created by Qasi on 09.07.2014.
 */
public class ImageWithKey {
    private Bitmap bitmap;
    private String key;

    public ImageWithKey(Bitmap bitmap, String key) {
        this.bitmap = bitmap;
        this.key = key;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getKey() {
        return key;
    }
}
