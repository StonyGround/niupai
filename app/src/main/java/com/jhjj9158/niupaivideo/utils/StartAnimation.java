package com.jhjj9158.niupaivideo.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.jhjj9158.niupaivideo.R;


/**
 * Created by Administrator on 15-6-30.
 */
public class StartAnimation {
    public static void startRotate(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        animation.setInterpolator(new LinearInterpolator());
        view.setAnimation(animation);
    }
}
