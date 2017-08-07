package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;

import butterknife.Bind;

public class WithdrawRuleActivity extends BaseActivity {

    @Bind(R.id.rule_showmsg)
    TextView ruleShowmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "提现");
        TextView textView = new TextView(this);
        textView.setText("提现记录");
        addToolBarRightView(textView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithdrawRuleActivity.this, WithDrawHistoryActivity.class));
            }
        });


        int rule = getIntent().getIntExtra("rule", 0);
        if (rule == 1) {
            ruleShowmsg.setText("钱包满100元才能提现哦~(๑•ᴗ•๑)");
        }else {
            ruleShowmsg.setText("一周只能提现一次哦~(๑•ᴗ•๑)");
        }
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_withdraw_rule, null);
    }
}
