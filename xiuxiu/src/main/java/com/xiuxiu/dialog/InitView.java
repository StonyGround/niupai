package com.xiuxiu.dialog;

import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by oneki on 2017/6/26.
 */

public class InitView {

    public static void initiBottomDialog(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
//        window.setWindowAnimations(R.style.dialog_bottom_anim);  //添加动画
    }

    public static void setDialogMatchParent(Dialog dialog) {
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    public static void initiCenterDialog(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
//        window.setWindowAnimations(R.style.dialog_bottom_anim);  //添加动画
    }
}
