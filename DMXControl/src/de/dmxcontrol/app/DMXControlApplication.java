/*
 * DMXControlApplication.java
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

package de.dmxcontrol.app;

import android.app.Application;
import android.os.Environment;

import java.io.File;

import de.dmxcontrol.executor.EntityExecutor;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.network.ServiceFrontend;

public class DMXControlApplication extends Application {
    private final static String TAG = "dmxcontrol";
    private final static String StoragePath = Environment.getExternalStorageDirectory() + File.separator + "DMXControl";
    private final static String IconStorageName = StoragePath + File.separator + "Icons";
    private Prefs prefs;
    private boolean mJustStarted;

    public DMXControlApplication() {
        mJustStarted = true;
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            prefs = Prefs.get();
            prefs.setPreferences(this);
            ServiceFrontend.initOnce(this);

            /**try {
                File Directory = new Directory(StoragePath);
                if (!Directory.exists()) {
                    Directory.mkdirs();

                }
                Directory = new File(IconStorageName);
                if (!Directory.exists()) {
                    Directory.mkdirs();
                }
            }catch(Exception e){}**/
            EntityExecutor.SendAllRequest();
            EntityExecutorPage.SendAllRequest();

            if (!prefs.getOffline())
                ServiceFrontend.get().connect();
        } catch (Exception e) {
            e.toString();
        }
    }


    public boolean getJustStarted() {
        boolean result = mJustStarted;
        mJustStarted = false;
        return result;
    }

    @Override
    public void onTerminate() {

    }
}
