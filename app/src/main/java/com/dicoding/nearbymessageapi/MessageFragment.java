package com.dicoding.nearbymessageapi;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageFragment extends DialogFragment {
    private TextView tvMessage;
    private EditText edtMessage;

    public static String TYPE_REPLY = "type_reply";
    public static String TYPE_INITIAL = "type_initial";
    public static String EXTRA_TYPE = "extra_type";
    public static String EXTRA_SENDER_EMAIL = "extra_sender_email";
    public static String EXTRA_RECEIVER_EMAIL = "extra_receiver_email";
    public static String EXTRA_MESSAGE = "extra_message";

    private String senderEmail = null;

    private OnMessageRepliedListener onMessageRepliedListener;

    public OnMessageRepliedListener getOnMessageRepliedListener() {
        return onMessageRepliedListener;
    }

    public void setOnMessageRepliedListener(OnMessageRepliedListener onMessageRepliedListener) {
        this.onMessageRepliedListener = onMessageRepliedListener;
    }

    public MessageFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_message, null);
        tvMessage = (TextView)view.findViewById(R.id.tv_message);
        edtMessage = (EditText)view.findViewById(R.id.edt_message);

        String action = null;
        String title = null;

        if (getArguments().getString(EXTRA_TYPE).equalsIgnoreCase(TYPE_INITIAL)){
            action = "KIRIM";
            tvMessage.setText("To: "+getArguments().getString(EXTRA_RECEIVER_EMAIL));
            title = "Send Message";
        }else {
            title = "Message From";
            senderEmail = getArguments().getString(EXTRA_SENDER_EMAIL);
            String completeMessage = senderEmail +" said "+ getArguments().getString(EXTRA_MESSAGE);
            tvMessage.setText(completeMessage);
            action = "BALAS";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(title);
        builder.setPositiveButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String repliedMessage = edtMessage.getText().toString().trim();

                if (!TextUtils.isEmpty(repliedMessage)){
                    getOnMessageRepliedListener().onMessageReplied(repliedMessage, senderEmail);
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface OnMessageRepliedListener{
        void onMessageReplied(String message, String receiverEmail);
    }


}
