/*
 *  AboutDialogs.java
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

import java.lang.ArrayIndexOutOfBoundsException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import de.dmxcontrol.android.R;
import de.dmxcontrol.changelog.Changelog;

public class AboutDialogs {
    private Activity activity;

    public final static int ABOUT_DIALOG_MAIN = 0x11;
    public final static int ABOUT_DIALOG_CREDITS = 0x12;
    public final static int ABOUT_DIALOG_CHANGELOG = 0x13;

    public AboutDialogs(Activity activity) {
        this.activity = activity;
    }

    public AlertDialog onCreateDialog(int id) {
        switch (id) {
            case ABOUT_DIALOG_MAIN:
                return mainAboutDialog();
            case ABOUT_DIALOG_CREDITS:
                return creditsDialog();
            case ABOUT_DIALOG_CHANGELOG:
                return changelogDialog(null);
            default:
                return null;
        }
    }

    public AlertDialog mainAboutDialog() {

        AlertDialog ad = new AlertDialog.Builder(activity)
                .setTitle(R.string.about_title)
                .setIcon(R.drawable.androidmann_neu)
                .setItems(R.array.about_main_items, mOnClickListener)
                .setNeutralButton(R.string.about_dismiss,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

								/* User clicked Something so do some stuff */
                            }
                        }
                ).create();
        return ad;
    }

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

            switch (which) {

                // Credits
                case 0:
                    activity.showDialog(ABOUT_DIALOG_CREDITS);
                    break;
                // Changelog
                case 1:
                    activity.showDialog(ABOUT_DIALOG_CHANGELOG);
                    break;
                // Feedback
                case 2:
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                            "DMXControl Android App Feedback");
                    sendIntent.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{"info@dmxcontrol.de"});
                    sendIntent.setType("message/rfc822");
                    activity.startActivity(Intent.createChooser(sendIntent,
                            "Feedback via:"));
                    break;
                // Rating/Market
                // case 3:
                // Intent intent = new Intent(Intent.ACTION_VIEW);
                // intent.setData(Uri
                // .parse("market://search?q=pname:de.studiorutton.gerengdic"));
                // activity.startActivity(intent);
                // break;
                default:

            }
        }
    };

    public AlertDialog creditsDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(activity)
                .setTitle(R.string.about_title)
                .setIcon(R.drawable.androidmann_neu)
                .setNeutralButton(R.string.about_dismiss,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

								/* User clicked Something so do some stuff */
                            }
                        }
                );
        // FrameLayout fl = (FrameLayout) ad.findViewById(android.R.id.custom);
        // fl.addView(myView, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ScrollView sv = (ScrollView) LayoutInflater.from(activity).inflate(
                R.layout.about_credits, null);

        adb.setView(sv);
        AlertDialog ad = adb.create();
        return ad;
    }

    public AlertDialog changelogDialog(DialogInterface.OnClickListener listener) {
        if (listener == null) {
            listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

					/* User clicked Something so do some stuff */
                }
            };
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(activity)
                .setTitle(R.string.about_title_changelog)
                .setIcon(R.drawable.androidmann_neu)
                .setNeutralButton(R.string.about_dismiss, listener);

        // FrameLayout fl = (FrameLayout) ad.findViewById(android.R.id.custom);
        // fl.addView(myView, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        Changelog cl = new Changelog(activity.getApplicationContext());
        try {
            cl.populateItems(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout ll = (LinearLayout) LayoutInflater.from(activity).inflate(
                R.layout.changelog_list, null);
        adb.setView(ll);

        ListView lv = (ListView) ll.findViewById(R.id.changelog_list);
        lv.setAdapter(cl.getChangelogListAdapter());
        lv.setClickable(false);

        AlertDialog ad = adb.create();
        return ad;
    }
}
