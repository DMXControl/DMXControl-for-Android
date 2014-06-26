/*
 * Preferences.java
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

package de.dmxcontrol.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.List;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.network.ServiceFrontend;

public class PreferencesActivity extends PreferenceActivity {
    private final static String TAG = "dmxcontrol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit().putString("pref_connect_address", Prefs.get().getServerAddress()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Log.d(TAG, "PreferenceActivity:onStop" );
        Prefs prefs = Prefs.get();
        prefs.setPreferences(this.getApplicationContext());

        boolean networkChanged = prefs.connectConfigChanged();

        ServiceFrontend cs = ServiceFrontend.get();
        if(networkChanged && prefs.getOffline()) {
            cs.disconnect(true);
        }
        else if(networkChanged && !prefs.getOffline()) {
            cs.connect();
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        // loadHeadersFromResource(R.xml.preferences, target);
    }

}
