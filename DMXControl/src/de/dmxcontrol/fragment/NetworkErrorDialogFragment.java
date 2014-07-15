/*
 * NetworkErrorFragment.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2012 DMXControl-For-Android. All rights reserved.
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

package de.dmxcontrol.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.network.ServiceFrontend;

public class NetworkErrorDialogFragment extends DialogFragment implements OnClickListener {
    public final static String TAG = "networkerorrdialog";
    private final static String ARGUMENT_MESSAGE = "de.dmxcontrol.ARGUMENT_MESSAGE";

    static boolean isShowing = false;

    private CloseListener mListener;
    private CheckBox mCheckBox;

    public interface CloseListener {
        public void onCloseDialog(String fragmentTag);
    }

    public final static NetworkErrorDialogFragment newInstance(String msg) {
        if(isShowing) {
            return null;
        }

        isShowing = true;
        NetworkErrorDialogFragment dialog = new NetworkErrorDialogFragment();
        Bundle b = new Bundle();
        b.putString(ARGUMENT_MESSAGE, msg);
        dialog.setArguments(b);
        return dialog;
    }

    public NetworkErrorDialogFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof CloseListener) {
            mListener = (CloseListener) activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_network_error, null);
        mCheckBox = (CheckBox) ll.findViewById(R.id.offline_mode_enable);
        mCheckBox.setChecked(true);

        String msg = getArguments().getString(ARGUMENT_MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.error_network_occurred))
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.button_okay), this);

        builder.setView(ll);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        // We need to dismiss dialog before trying to reconnect, so it can popup again.
        isShowing = false;
        dialog.dismiss();

        if(mCheckBox.isChecked()) {

            Prefs.get().setOffline(true);
        }
        else {
            // If user unchecked -> try to connect again
            ServiceFrontend.get().connect();
        }

        if(mListener != null) {
            mListener.onCloseDialog(TAG);
        }
    }

}
