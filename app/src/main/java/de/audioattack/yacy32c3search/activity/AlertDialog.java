/*
 * Copyright 2014 Marc Nause <marc.nause@gmx.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see  http:// www.gnu.org/licenses/.
 */
package de.audioattack.yacy32c3search.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.util.Locale;

import de.audioattack.yacy32c3search.R;

/**
 * Displays all kinds of dialogs in the app.
 *
 * @author Marc Nause <marc.nause@gmx.de>
 */
public class AlertDialog extends DialogFragment {

    /**
     * Creates dialog for simple messages.
     *
     * @param title   ID of title of the message
     * @param message ID of the message
     * @return the dialog
     */
    public static DialogFragment newInstance(final int title, final int message) {
        final AlertDialog frag = new AlertDialog();
        final Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("message", message);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Creates dialog to display message regarding an exception.
     *
     * @param title   ID of title of the message
     * @param message ID of the message (message should contain %s to be replaced with message from exception)
     * @param ex      the exception
     * @return the dialog
     */
    public static DialogFragment newInstance(final int title, final int message, final Exception ex) {

        final AlertDialog frag = new AlertDialog();
        final Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("message", message);
        args.putString("exception", ex.getMessage() == null ? ex.toString() : ex.getMessage());
        frag.setArguments(args);
        return frag;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final int title = getArguments().getInt("title");
        final int message = getArguments().getInt("message");
        final String exception = getArguments().getString("exception");

        return new android.app.AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(exception == null ? Html.fromHtml(getString(message)) : String.format(Locale.US, getString(message), exception))
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        ((TextView) getDialog().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

}
