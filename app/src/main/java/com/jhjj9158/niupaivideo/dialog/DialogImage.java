package com.jhjj9158.niupaivideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jhjj9158.niupaivideo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/10/19.
 */
public class DialogImage extends Dialog {

    @Bind(R.id.dialog_imageview)
    ImageView dialogImageview;

    private Context context;
    private String imageUrl;

    public DialogImage(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    public DialogImage(Context context, String imageUrl) {
        super(context, R.style.dialog);
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image);
        ButterKnife.bind(this);

        Glide.with(context).load(imageUrl).placeholder(R.drawable.me_user_admin).into(dialogImageview);
    }
}
