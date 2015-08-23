package org.openintents.widget;

import android.view.View;

/**
 * Interface for notifications of position change of slider.
 *
 * @author Peli
 */
public interface OnColorChangedListener {

    /**
     * This method is called when the user changed the color.
     * <p/>
     * This works in touch mode, by dragging the along the
     * color circle with the finger.
     */
    void onColorChanged(View view, int newColor);

    /**
     * This method is called when the user clicks the center button.
     *
     * @param view The color circle view which is clicked on
     * @param newColor the new color which is set
     */
    void onColorPicked(View view, int newColor);
}
