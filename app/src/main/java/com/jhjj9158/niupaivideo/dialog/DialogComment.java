package com.jhjj9158.niupaivideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/19.
 */
public class DialogComment extends Dialog {

    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static String imageDir = "temp.jpg";
    @Bind(R.id.et_comment)
    EditText etComment;
    @Bind(R.id.tv_send_comment)
    TextView tvSendComment;

    private Context context;
    private String replyName;

    public DialogComment(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    public DialogComment(Context context, String replyName) {
        super(context, R.style.dialog);
        this.context = context;
        this.replyName = replyName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment);
        ButterKnife.bind(this);
        if (!TextUtils.isEmpty(replyName)) {
            etComment.setHint(Html.fromHtml("<small>" + "@" + replyName + "：" + "</small>"));
        }
    }

    private NoticeDialogListener mListener;

    public void setNoticeDialogListerner(NoticeDialogListener mListener) {
        this.mListener = mListener;
    }

    public interface NoticeDialogListener {
        void onClick(String comment);
    }

    @OnClick(R.id.tv_send_comment)
    public void onViewClicked() {
        mListener.onClick(etComment.getText().toString());
        DialogComment.this.dismiss();
    }
}
