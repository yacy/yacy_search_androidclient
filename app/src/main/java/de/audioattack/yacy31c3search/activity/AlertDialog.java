package de.audioattack.yacy31c3search.activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import de.audioattack.yacy31c3search.R;

/**
 * Created by low012 on 23.12.14.
 */
public class AlertDialog extends DialogFragment {
    public static AlertDialog newInstance(final int title, final int message) {
        final AlertDialog frag = new AlertDialog();
        final Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final int title = getArguments().getInt("title");
        final int message = getArguments().getInt("message");

        return new android.app.AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setMessage(Html.fromHtml(getString(message)))
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
