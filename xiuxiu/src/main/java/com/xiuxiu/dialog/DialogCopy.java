package com.xiuxiu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.widget.AutoFitSizeTextView;

import java.util.List;

/**
 * Created by oneki on 2017/6/29.
 */

public class DialogCopy extends Dialog implements View.OnClickListener{

    private AutoFitSizeTextView copy_five,copy_ten,copy_all,cancle;

    private CopyFiveListener copyFiveListener;
    private CopyTenListener copyTenListener;
    private CopyAllListener copyAllListener;

    public DialogCopy(Context context) {
        super(context, R.style.dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_copy);

        copy_five= (AutoFitSizeTextView) findViewById(R.id.copy_five);
        copy_ten= (AutoFitSizeTextView) findViewById(R.id.copy_ten);
        copy_all= (AutoFitSizeTextView) findViewById(R.id.copy_all);
        cancle= (AutoFitSizeTextView) findViewById(R.id.cancel);

        copy_five.setOnClickListener(this);
        copy_ten.setOnClickListener(this);
        copy_all.setOnClickListener(this);
        cancle.setOnClickListener(this);
    }

    public void setCopyFiveListener(CopyFiveListener mListener) {
        this.copyFiveListener = mListener;
    }

    public void setCopyTenListener(CopyTenListener mListener) {
        this.copyTenListener = mListener;
    }

    public void setCopyAllListener(CopyAllListener mListener) {
        this.copyAllListener = mListener;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.copy_five){
            copyFiveListener.onClick();
        }else if(v.getId()==R.id.copy_ten){
            copyTenListener.onClick();
        }else if(v.getId()==R.id.copy_all){
            copyAllListener.onClick();
        }else if(v.getId()==R.id.cancel){
            dismiss();
        }
    }

    public interface CopyFiveListener {
        void onClick();
    }

    public interface CopyTenListener {
        void onClick();
    }

    public interface CopyAllListener {
        void onClick();
    }

}
