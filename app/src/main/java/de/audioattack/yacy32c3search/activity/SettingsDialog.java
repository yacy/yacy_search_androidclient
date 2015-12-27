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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.audioattack.yacy32c3search.R;

/**
 * Allows to change settings.
 *
 * @author Marc Nause <marc.nause@gmx.de>
 */
public class SettingsDialog extends DialogFragment {

    public static final String KEY_HOST = SettingsDialog.class.getName();
    public static final String DEFAULT_HOST = "32c3.yacy.net";

    private View customView;

    public static DialogFragment newInstance() {
        return new SettingsDialog();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        // using {@link getLayoutInflator(Bundle) here will cause endless recursion
        customView = LayoutInflater.from(getActivity()).inflate(R.layout.settings_dialog, null, false);

        return new android.app.AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_settings)
                .setView(customView)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                final TextView tvSettingsHost = (TextView) customView.findViewById(R.id.settings_host);
                                if (tvSettingsHost != null && tvSettingsHost.getText() != null) {
                                    store(getActivity(), KEY_HOST, (tvSettingsHost.getText().toString()));
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final TextView tvSettingsHost = (TextView) customView.findViewById(R.id.settings_host);
        tvSettingsHost.setText(load(getActivity(), KEY_HOST, DEFAULT_HOST));

    }

    private static void store(final Context context, final String key, final String value) {
        final SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString(key, Base64.encodeToString(value.getBytes(), Base64.NO_WRAP));
        editor.commit();
    }

    public static String load(final Context context, final String key, String dflt) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        final String base64Value = sharedPreferences.getString(key, "");

        final String value;
        if (base64Value.length() == 0) {
            value = dflt;
        } else {
            value = new String(Base64.decode(base64Value, Base64.NO_WRAP));
        }

        return value;
    }
}
