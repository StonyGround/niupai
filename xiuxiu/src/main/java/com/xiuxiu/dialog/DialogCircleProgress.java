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

public class DialogCircleProgress extends Dialog {

    public DialogCircleProgress(Context context) {
        super(context, R.style.dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_progress);

        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }
}
