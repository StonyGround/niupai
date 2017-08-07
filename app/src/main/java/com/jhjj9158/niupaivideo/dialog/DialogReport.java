package com.jhjj9158.niupaivideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.widget.AutoFitSizeTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/19.
 */
public class DialogReport extends Dialog {

    @Bind(R.id.personal_report)
    AutoFitSizeTextView personalReport;
    @Bind(R.id.personal_report_cancel)
    AutoFitSizeTextView personalReportCancel;
    private Context context;

    public DialogReport(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_report);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.personal_report, R.id.personal_report_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_report:
                mListener.onClick();
                break;
            case R.id.personal_report_cancel:
                dismiss();
                break;
        }
    }

    private ReportDialogListener mListener;

    public void setReportDialogListener(ReportDialogListener mListener) {
        this.mListener = mListener;
    }

    public interface ReportDialogListener {
        void onClick();
    }
}
