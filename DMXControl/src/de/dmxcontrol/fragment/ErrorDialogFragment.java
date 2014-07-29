/*
 * ErrorDialogFragment.java
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

import de.dmxcontrol.android.R;

public class ErrorDialogFragment extends DialogFragment implements
        OnClickListener {
    public final static String TAG = "erorrdialog";
    private final static String ARGUMENT_MESSAGE = "de.dmxcontrol.ARGUMENT_MESSAGE";

    static boolean isShowing = false;

    private CloseListener mListener;

    public interface CloseListener {
        public void onCloseDialog(String fragmentTag);
    }

    public final static ErrorDialogFragment newInstance(String msg) {
        if(isShowing) {
            return null;
        }

        isShowing = true;
        ErrorDialogFragment dialog = new ErrorDialogFragment();
        Bundle b = new Bundle();
        b.putString(ARGUMENT_MESSAGE, msg);
        dialog.setArguments(b);
        return dialog;
    }

    public ErrorDialogFragment() {

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
        String msg = getArguments().getString(ARGUMENT_MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.error_occurred))
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(android.R.string.ok),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        isShowing = false;
        dialog.dismiss();

        if(mListener != null) {
            mListener.onCloseDialog(TAG);
        }
    }

}
