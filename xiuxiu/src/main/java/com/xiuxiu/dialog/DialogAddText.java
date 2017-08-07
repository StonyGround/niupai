package com.xiuxiu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiuxiu.R;

/**
 * Created by oneki on 2017/6/26.
 */

public class DialogAddText extends Dialog {
    private EditText etComment;
    private TextView tvSendComment;
    private String text;

    public DialogAddText(Context context, String text) {
        super(context, R.style.dialog);
        if (text.equals("双击编辑文字")) {
            text = "";
        }
        this.text = text;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_text);

        etComment = (EditText) findViewById(R.id.et_comment);
        tvSendComment = (TextView) findViewById(R.id.tv_send_comment);
        etComment.setText(text);
        etComment.setSelection(text.length());

        tvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(etComment.getText().toString());
                DialogAddText.this.dismiss();
            }
        });
    }

    private DialogAddText.NoticeDialogListener mListener;

    public void setNoticeDialogListerner(DialogAddText.NoticeDialogListener mListener) {
        this.mListener = mListener;
    }

    public interface NoticeDialogListener {
        void onClick(String text);
    }
}
