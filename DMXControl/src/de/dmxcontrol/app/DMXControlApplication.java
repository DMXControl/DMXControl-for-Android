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
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import de.dmxcontrol.executor.EntityExecutor;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.file.FileManager;
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
    public boolean stopService(Intent name) {
        Log.w("","Stop Application");
        DMXControlApplication.SaveLog();
        return super.stopService(name);
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();

            prefs = Prefs.get();
            prefs.setPreferences(this);
            ServiceFrontend.initOnce(this);

            FileManager.get(this);

            EntityExecutor.SendAllRequest();
            EntityExecutorPage.SendAllRequest();

            if (!prefs.getOffline()){
                ServiceFrontend.get().connect();
            }

        }
        catch (Exception e) {
            Log.w("",DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
    }

    public boolean getJustStarted() {
        boolean result = mJustStarted;
        mJustStarted = false;
        return result;
    }

    @Override
    public void onTerminate() {
        Log.w("","TERMINATE");
        DMXControlApplication.SaveLog();
    }

    public static void SaveLog(){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    log.append(line);
                    log.append(System.getProperty("line.separator"));
                }
                catch(Exception e){}
            }

            try {

                Writer writer;
                File outputFile = new File(StoragePath, "Log.txt");
                writer = new BufferedWriter(new FileWriter(outputFile));
                writer.write(log.toString());
                writer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
        }
    }

    public static String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();

        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
