/*
 * CompatabilityWrapper8.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2011 DMXControl-For-Android. All rights reserved.
 *
 *      This software is free software; you can redistribute it and/or
 *      modify it under the terms of the GNU General Public License
 *      as published by the Free Software Foundation; either
 *      version 3, june 2007 of the License, or (at your option) any later version.
 *
 *      This software is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *      General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public
 *      License (gpl.txt) along with this software; if not, write to the Free Software
 *      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *      For further information, please contact info [(at)] dmxcontrol.de
 *
 * 
 */

package de.dmxcontrol.compatibility;

import android.content.Context;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class CompatibilityWrapper8 {
    protected Context ctx;

    static public CompatibilityWrapper8 wrap(Context ctx) {
        try {
            return new CompatibilitySdk8(ctx);
        }
        catch(VerifyError e) {
            return new CompatibilityWrapper8(ctx);
        }
    }

    protected CompatibilityWrapper8(Context ctx) {
        this.ctx = ctx;
    }

    public boolean isDisplayPortrait() {
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int o = display.getRotation();
        if(o == Surface.ROTATION_0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isDisplayLandscape() {
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int o = display.getRotation();
        if(o == Surface.ROTATION_90) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isDisplayLandscapeOverhead() {
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int o = display.getRotation();
        if(o == Surface.ROTATION_270) {
            return true;
        }
        else {
            return false;
        }
    }

}
