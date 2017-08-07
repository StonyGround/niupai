package com.xiuxiu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.task.GetTagsTask;
import com.xiuxiu.util.GlobalDef;
import com.xiuxiu.util.Util;
import com.xiuxiu.view.TagsViewGroup;

import java.util.ArrayList;
import java.util.List;

//添加标签
public class AddTagsActivity extends Activity {
    private static final String TAG = "AddTagsActivity";

    private TextView addTagsCancelBtn = null; //取消按钮
    private Button addTagsFinishBtn = null; //完成按钮
    private EditText tagsEdit = null;        //文本输入框
    private TagsViewGroup tagesView = null; //标签显示控件
    private TextView tagesText = null;
    private String strReturnTags; //需要返回的标签文字
    private ListView listView = null; //匹配显示的列表
    private boolean bFind = false; //是否找到匹配元素
    private List<String> data = null;
    private TextView titleTags = null;
    private TextView promptView = null;// 提示
    private GetTagsTask gTagsTask = null;
    private static final int MAXLENGTH = 20;// 标签最大长度
    private String[] hotTags = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_video_tags_layout);

        addTagsCancelBtn = (TextView) findViewById(R.id.addTagsCancelTextBtn);
        addTagsFinishBtn = (Button) findViewById(R.id.addTagsFinishBtn);
        tagsEdit = (EditText) findViewById(R.id.editTags);
        listView = (ListView) findViewById(R.id.listTags);
        titleTags = (TextView) findViewById(R.id.titleTags);
        promptView = (TextView) findViewById(R.id.addTagsPromptTextView);
        tagesView = (TagsViewGroup) findViewById(R.id.myViewGroup);

        String sTags = getIntent().getStringExtra("sTags");
        Log.d(TAG, "onClick: addTagLayout" + sTags);
        if (!TextUtils.isEmpty(sTags)) {
            tagsEdit.setText(sTags);
        }

        //取消按钮
        addTagsCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });

        //确定按钮
//        addTagsFinishBtn.setEnabled(false); //初始化按钮不可用
//        addTagsFinishBtn.setTextColor(getResources().getColor(R.color.btn_disabled));//设置不用文字颜色Color.rgb(89,184,229)
        addTagsFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strReturnTags = tagsEdit.getText().toString();
//                if (Util.calculateCharactersNum(strReturnTags) > MAXLENGTH) {
//                    promptView.setText(getResources().getString(R.string.addTagsError));
//                    promptView.setTextColor(getResources().getColor(R.color.red));
//                } else {
                    Intent result = new Intent();
                    result.putExtra("LabelMsg", strReturnTags);
                    setResult(RESULT_OK, result);
                    finish();
//                }
            }
        });

        //标签输入框文字监听
        tagsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                strReturnTags = tagsEdit.getText().toString();
                //输入项的元素不为空
//                if (!TextUtils.isEmpty(strReturnTags)) {
//                    addTagsFinishBtn.setEnabled(true); //初始化按钮为可用状态
//                    addTagsFinishBtn.setTextColor(getResources().getColor(R.color.white));
//                    updataListInfo(strReturnTags);
//                } else {
//                    listView.setVisibility(View.GONE);
//                    titleTags.setVisibility(View.VISIBLE);
//                    tagesView.setVisibility(View.VISIBLE);
//                    promptView.setVisibility(View.GONE);
//                    addTagsFinishBtn.setEnabled(false); //初始化按钮为不可用状态
//                    addTagsFinishBtn.setTextColor(getResources().getColor(R.color.btn_disabled));
//                }
            }
        });

        //匹配显示列表的监听事件
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                strReturnTags = ((TextView) arg1).getText().toString();
                Intent result = new Intent();
                result.putExtra("LabelMsg", strReturnTags);
                setResult(RESULT_OK, result);
                finish();
            }
        });

        //初始化热门标签
        Util.cancelTask(gTagsTask);
        gTagsTask = new GetTagsTask(AddTagsActivity.this, handler);
        gTagsTask.execute();
    }

    //加载热门标签
    public void loadTagesInfo(Message msg) {
        hotTags = (String[]) msg.obj;
        if (hotTags == null) {
            tagesView.setVisibility(View.GONE);
            return;
        }

        for (int i = 0; i < hotTags.length; i++) {
            tagesText = new TextView(this);
            tagesText.setId(i);
            tagesText.setText(hotTags[i]);
            tagesText.setBackgroundResource(R.drawable.text_selector);
            tagesText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取选择到的热门标签
                    strReturnTags = hotTags[v.getId()];
                    Intent result = new Intent();
                    result.putExtra("LabelMsg", strReturnTags);
                    setResult(RESULT_OK, result);
                    finish();
                }
            });

            tagesView.addView(tagesText);
        }
    }

    @Override
    public void finish() {
        super.finish();
        //退出动画
        overridePendingTransition(0, R.anim.activity_down_animation);
    }

    //根据关键字查找匹配项
    private List<String> getData(String str) {
        bFind = false;
        if (data != null)
            data.clear();

        for (String strTemp : hotTags) {
            if (strTemp.indexOf(str) != -1) {
                if (data == null)
                    data = new ArrayList<String>();

                data.add(strTemp);
                bFind = true;
            }
        }

        if (!bFind)
            data = null;

        return data;
    }

    //更新匹配显示列表
    public void updataListInfo(String strSpecifiedWord) {
        ListAdapter listdata = null;
        List<String> dataTemp = null;
        if (hotTags != null) {
            dataTemp = getData(strSpecifiedWord);
        }

        if (dataTemp == null) {
            listdata = null;
            listView.setAdapter(listdata);
            listView.setVisibility(View.GONE);
            titleTags.setVisibility(View.GONE);
            tagesView.setVisibility(View.GONE);
            promptView.setText(getResources().getString(R.string.noFindWord));
            promptView.setTextColor(getResources().getColor(R.color.text_dark_gray));
            promptView.setVisibility(View.VISIBLE);//若找不到相应的匹配项,则显示提示项
        } else {
            listdata = new ArrayAdapter<String>(this, R.layout.matching_tags_list, dataTemp);
            listView.setAdapter(listdata);
            listView.setVisibility(View.VISIBLE);//若找到相应的匹配项,则显示匹配列表
            titleTags.setVisibility(View.GONE);
            tagesView.setVisibility(View.GONE);
            promptView.setVisibility(View.GONE);
        }
    }

    // 消息处理
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GlobalDef.MSG_GETTAGS_SUSS:
                    loadTagesInfo(msg);
                    break;
                case GlobalDef.MSG_GETTAGS_FAIL:
                    tagesView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };
}
