package com.xiuxiu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.xiuxiu.R;

/**
 * Created by hzdykj on 2017/7/31.
 */

public class DialogGuide extends Dialog {
    private ImageView rlGuide;
    private int guideIcon;
    private Context context;

    public DialogGuide(Context context, int guideIcon) {
        super(context, R.style.dialog);
        this.context = context;
        this.guideIcon = guideIcon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_guide);
        rlGuide = (ImageView) findViewById(R.id.rl_guide);
        Glide.with(context).load(guideIcon).asBitmap().into(rlGuide);
//        rlGuide.setImageResource(guideIcon);
        rlGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogGuide.this.dismiss();
            }
        });
    }
}
