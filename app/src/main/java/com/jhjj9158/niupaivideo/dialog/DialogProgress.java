package com.jhjj9158.niupaivideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.StartAnimation;
import com.jhjj9158.niupaivideo.widget.ProgressView;


public class DialogProgress extends Dialog {

    public DialogProgress(Context context) {
        this(context, R.style.dialog);
    }

    public DialogProgress(Context paramContext, int paramInt) {
        super(paramContext, paramInt);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.dialog_progress);

        ProgressView progressView = (ProgressView) findViewById(R.id.progress);
        StartAnimation.startRotate(getContext(), progressView);
    }
}