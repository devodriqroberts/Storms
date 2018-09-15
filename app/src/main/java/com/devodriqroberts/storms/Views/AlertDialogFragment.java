package com.devodriqroberts.storms.Views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Context;
import android.app.Dialog;
import android.app.DialogFragment;


import com.devodriqroberts.storms.R;

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.network_alert_error_title)
        .setMessage(R.string.network_alert_error_message)
        .setPositiveButton(R.string.network_alert_error_pos_button_text, null);

        return builder.create();
    }
}
