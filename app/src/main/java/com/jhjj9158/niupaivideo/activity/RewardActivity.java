package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.RewardBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.utils.ToolUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

public class RewardActivity extends BaseActivity {

    @Bind(R.id.tv_reward_day_number)
    TextView tvRewardDayNumber;
    @Bind(R.id.tv_reward_day_money)
    TextView tvRewardDayMoney;
    @Bind(R.id.ll_reward_day)
    LinearLayout llRewardDay;

    @Bind(R.id.tv_reward_browse_number)
    TextView tvRewardBrowseNumber;
    @Bind(R.id.tv_reward_browse_money)
    TextView tvRewardBrowseMoney;
    @Bind(R.id.tv_reward_browse_day_money)
    TextView tvRewardBrowseDayMoney;
    @Bind(R.id.ll_reward_browse_rule)
    LinearLayout llRewardBrowseRule;
    @Bind(R.id.ll_reward_browse_more)
    LinearLayout llRewardBrowseMore;
    @Bind(R.id.iv_reward_browse_rule_down)
    ImageView ivRewardBrowseRuleDown;
    @Bind(R.id.tv_reward_browse_moneys)
    TextView tvRewardBrowseMoneys;
    @Bind(R.id.tv_reward_browse_money_more)
    TextView tvRewardBrowseMoneyMore;
//    @Bind(R.id.iv_reward_browse_rule_top)
//    ImageView ivRewardBrowseRuleTop;

    @Bind(R.id.tv_reward_comment_number)
    TextView tvRewardCommentNumber;
    @Bind(R.id.tv_reward_comment_money)
    TextView tvRewardCommentMoney;
    @Bind(R.id.tv_reward_comment_day_money)
    TextView tvRewardCommentDayMoney;
    @Bind(R.id.ll_reward_comment_rule)
    LinearLayout llRewardCommentRule;
    @Bind(R.id.ll_reward_comment_more)
    LinearLayout llRewardCommentMore;
    @Bind(R.id.iv_reward_comment_rule_down)
    ImageView ivRewardCommentRuleDown;
    @Bind(R.id.tv_reward_comment_moneys)
    TextView tvRewardCommentMoneys;
    @Bind(R.id.tv_reward_comment_money_more)
    TextView tvRewardCommentMoneyMore;
//    @Bind(R.id.iv_reward_comment_rule_top)
//    ImageView ivRewardCommentRuleTop;

    @Bind(R.id.tv_reward_praise_number)
    TextView tvRewardPraiseNumber;
    @Bind(R.id.tv_reward_praise_money)
    TextView tvRewardPraiseMoney;
    @Bind(R.id.tv_reward_praise_day_money)
    TextView tvRewardPraiseDayMoney;
    @Bind(R.id.ll_reward_praise_rule)
    LinearLayout llRewardPraiseRule;
    @Bind(R.id.ll_reward_praise_more)
    LinearLayout llRewardPraiseMore;
    @Bind(R.id.iv_reward_praise_rule_down)
    ImageView ivRewardPraiseRuleDown;
    @Bind(R.id.tv_reward_praise_moneys)
    TextView tvRewardPraiseMoneys;
    @Bind(R.id.tv_reward_praise_money_more)
    TextView tvRewardPraiseMoneyMore;
//    @Bind(R.id.iv_reward_praise_rule_top)
//    ImageView ivRewardPraiseRuleTop;

    private static String TAG = "RewardActivity";
    boolean isExpandBrowse = false;
    boolean isExpandComment = false;
    boolean isExpandPraise = false;
    private String urlReward = "http://www.niupaisp.com/activity/rewards.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
        initTitle(this, "每日奖励");

        int buidx = CacheUtils.getInt(RewardActivity.this,Contact.USERIDX);
        getRewardData(buidx);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_reward, null);
    }

    private void getRewardData(int buidx){
        String url = Contact.HOST + Contact.GET_REWARD_DATA + "?buidx=" + buidx ;
        OkHttpClientManager.get(url, new OKHttpCallback<RewardBean>() {
            @Override
            public void onResponse(RewardBean rewardBean) {
                //发布
                tvRewardDayNumber.setText(ToolUtils.isEmpty(rewardBean.getRewardsVideoNum()) ? "0" : rewardBean.getRewardsVideoNum());
                tvRewardDayMoney.setText(ToolUtils.isEmpty(rewardBean.getRewardsVideoMoney()) ? "0" : rewardBean.getRewardsVideoMoney());

                //每日
                tvRewardBrowseNumber.setText(rewardBean.getLlNumber());
                tvRewardBrowseMoney.setText(rewardBean.getLlMoney());
                tvRewardBrowseDayMoney.setText(rewardBean.getLlAcquire());
                tvRewardBrowseMoneys.setText(rewardBean.getBrowseMoney());
                tvRewardBrowseMoneyMore.setText(rewardBean.getBrowseDayMoney());

                tvRewardCommentNumber.setText(rewardBean.getPlNumber());
                tvRewardCommentMoney.setText(rewardBean.getPlMoney());
                tvRewardCommentDayMoney.setText(rewardBean.getPlAcquire());
                tvRewardCommentMoneys.setText(rewardBean.getCommentMoney());
                tvRewardCommentMoneyMore.setText(rewardBean.getCommentDayMoney());

                tvRewardPraiseNumber.setText(rewardBean.getZanNumber());
                tvRewardPraiseMoney.setText(rewardBean.getZanMoney());
                tvRewardPraiseDayMoney.setText(rewardBean.getZanAcquire());
                tvRewardPraiseMoneys.setText(rewardBean.getPraiseMoney());
                tvRewardPraiseMoneyMore.setText(rewardBean.getPraiseDayMoney());
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @OnClick({R.id.ll_reward_browse_rule, R.id.ll_reward_comment_rule, R.id.ll_reward_praise_rule, R.id.ll_reward_day})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.ll_reward_browse_rule:
                isExpandBrowse = !isExpandBrowse;
                llRewardBrowseMore.clearAnimation();
                int durationMillisB = 50;
                if (isExpandBrowse) {
                    llRewardBrowseMore.setVisibility(View.VISIBLE);
                    RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillisB);
                    animation.setFillAfter(true);
                    ivRewardBrowseRuleDown.startAnimation(animation);
                } else {
                    llRewardBrowseMore.setVisibility(View.GONE);
                    RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillisB);
                    animation.setFillAfter(true);
                    ivRewardBrowseRuleDown.startAnimation(animation);
                }
                break;
            case R.id.ll_reward_comment_rule:
                isExpandComment = !isExpandComment;
                llRewardCommentMore.clearAnimation();
                int durationMillisC = 50;
                if (isExpandComment) {
                    llRewardCommentMore.setVisibility(View.VISIBLE);
                    RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillisC);
                    animation.setFillAfter(true);
                    ivRewardCommentRuleDown.startAnimation(animation);
                } else {
                    llRewardCommentMore.setVisibility(View.GONE);
                    RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillisC);
                    animation.setFillAfter(true);
                    ivRewardCommentRuleDown.startAnimation(animation);
                }
                break;
            case R.id.ll_reward_praise_rule:
                isExpandPraise = !isExpandPraise;
                llRewardPraiseMore.clearAnimation();
                int durationMillisP = 50;
                if (isExpandPraise) {
                    llRewardPraiseMore.setVisibility(View.VISIBLE);
                    RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillisP);
                    animation.setFillAfter(true);
                    ivRewardPraiseRuleDown.startAnimation(animation);
                } else {
                    llRewardPraiseMore.setVisibility(View.GONE);
                    RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillisP);
                    animation.setFillAfter(true);
                    ivRewardPraiseRuleDown.startAnimation(animation);
                }
                break;
            case R.id.ll_reward_day:
                Intent intent = new Intent();
                intent.putExtra("url", urlReward);
                intent.setClass(RewardActivity.this, WebViewActivity.class);
                startActivity(intent);
                break;
        }
    }
}
