package com.adori.personlistsample.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * Created by adria.navarro on 30/8/16.
 */
public class ConfirmationDialog extends DialogFragment {

    public interface Listener {
        void onPositiveClick(ConfirmationDialog dialog);
        void onNegativeClick(ConfirmationDialog dialog);
    }

    private static final String TITLE_ARG = "title";
    private static final String MESSAGE_ARG = "message";
    private static final String POS_ARG = "positive-button";
    private static final String NEG_ARG = "negative-button";


    private Listener mListener;

    public static ConfirmationDialog newInstance(String title, String message,
                                                 String positiveButton, String negativeButton) {
        ConfirmationDialog dialog = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(TITLE_ARG, title);
        args.putString(MESSAGE_ARG, message);
        args.putString(POS_ARG, positiveButton);
        args.putString(NEG_ARG, negativeButton);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE_ARG);
        String message = getArguments().getString(MESSAGE_ARG);
        String positiveButton = getArguments().getString(POS_ARG);
        String negativeButton = getArguments().getString(NEG_ARG);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onPositiveClick(ConfirmationDialog.this);
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onNegativeClick(ConfirmationDialog.this);
                    }
                })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof Listener)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement ConfirmationDialog.Listener");
        }
        mListener = (Listener)getActivity();
    }
}
