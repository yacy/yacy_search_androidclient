package de.audioattack.yacy31c3search.activity;

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

import de.audioattack.yacy31c3search.R;

/**
 * Allows to change settings.
 */
public class SettingsDialog extends DialogFragment {

    public static final String KEY_HOST = SettingsDialog.class.getName();
    public static final String DEFAULT_HOST = "31c3.yacy.net";

    public static DialogFragment newInstance() {
        return new SettingsDialog();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        // using {@link getLayoutInflator(Bundle) here will cause endless recursion
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.settings_dialog, null, false);

        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_settings)
                .setView(view)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                final TextView tvSettingsHost = (TextView) view.findViewById(R.id.settings_host);
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

        final TextView tvSettingsHost = (TextView) view.findViewById(R.id.settings_host);
        tvSettingsHost.setText(load(getActivity(), KEY_HOST, DEFAULT_HOST));

        return dialog;
    }

    public static boolean store(final Context context, final String key, final String value) {
        final SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString(key, Base64.encodeToString(value.getBytes(), Base64.NO_WRAP));
        return editor.commit();
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
