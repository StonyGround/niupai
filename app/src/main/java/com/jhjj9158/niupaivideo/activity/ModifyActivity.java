package com.jhjj9158.niupaivideo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.UserDetailBean;
import com.jhjj9158.niupaivideo.bean.UserInfoBean;
import com.jhjj9158.niupaivideo.bean.UserPostBean;
import com.jhjj9158.niupaivideo.dialog.DialogPicSelector;
import com.jhjj9158.niupaivideo.dialog.DialogProgress;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.FileUtils;
import com.jhjj9158.niupaivideo.utils.ToolUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.jhjj9158.niupaivideo.utils.Contact.IMAGE_PICKER;

public class ModifyActivity extends BaseActivity {

    @Bind(R.id.toolbar_back)
    ImageView toolbarBack;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.modify_headimg)
    CircleImageView modifyHeadimg;
    @Bind(R.id.modify_name)
    EditText modifyName;
    @Bind(R.id.modify_signature)
    EditText modifySignature;
    @Bind(R.id.modify_save)
    TextView modifySave;
    @Bind(R.id.modify_gender)
    TextView modifyGender;

    private UserInfoBean.DataBean userInfo;
    String headImgPath;
    private DialogProgress progress;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.getInt("code") == 100) {
                            CommonUtil.showTextToast(ModifyActivity.this, "修改成功");
                            Picasso.with(ModifyActivity.this).load(new File(headImgPath)).placeholder(R.drawable.me_user_admin).into
                                    (modifyHeadimg);
                            CommonUtil.updateInfo(ModifyActivity.this);
                        } else {
                            CommonUtil.showTextToast(ModifyActivity
                                    .this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.getInt("code") == 100) {
                            CommonUtil.showTextToast(ModifyActivity.this, "修改成功");
                            CommonUtil.updateInfo(ModifyActivity.this);
                            finish();
                        } else {
                            CommonUtil.showTextToast(ModifyActivity.this, "您输入的名称或签名大于总长度，" + jsonObject.getString("msg"));
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case Contact.NET_ERROR:
                    CommonUtil.showTextToast(ModifyActivity.this, "网络超时");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();

        toolbarTitle.setText("修改资料");
        userInfo = getIntent().getParcelableExtra("userInfo");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(99, 00, 00, 00));
        }

        initView();
//        byteLength();
        InputFilter[] emojiFiltersName = {emojiFilter};
        modifyName.setFilters(new InputFilter[]{emojiFilter});
        InputFilter[] emojiFiltersSignature = {emojiFilter};
        modifySignature.setFilters(new InputFilter[]{emojiFilter});
    }

    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                CommonUtil.showTextToast(ModifyActivity.this,"不支持输入Emoji表情符号，请见谅！");
                return "";
            }
            return null;
        }
    };

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_modify, null);
    }

    private String name;
    private String signature;

    //判断字节长度 签名 名称
    private void byteLength() {
        //签名
        modifySignature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = modifySignature.getText();
                int len = editable.toString().getBytes().length;

                int mMaxBytes = 60;
                if (len > mMaxBytes) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = ToolUtils.getWholeText(str, mMaxBytes);
                    modifySignature.setText(newStr);
                    editable = modifySignature.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                    CommonUtil.showTextToast(ModifyActivity.this, "您输入的签名大于总长度");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //名称
        modifyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = modifyName.getText();
                int len = editable.toString().getBytes().length;

                int mMaxBytes = 30;
                if (len > mMaxBytes) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = ToolUtils.getWholeText(str, mMaxBytes);
                    modifyName.setText(newStr);
                    editable = modifyName.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                    CommonUtil.showTextToast(ModifyActivity.this, "您输入的名称大于总长度");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void initView() {
        String headImage = userInfo.getHeadimg();
        try {
            name = URLDecoder.decode(userInfo.getNickName(),"utf-8");
            signature = URLDecoder.decode(userInfo.getUserTrueName(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!headImage.contains("http")) {
            headImage = "http://" + headImage;
        }
        Picasso.with(this).load(headImage).placeholder(R.drawable.me_user_admin).into(modifyHeadimg);
        modifyName.setText(name);
//        modifyName.setSelection(name.length());
        modifySignature.setText(signature);
//        modifySignature.setSelection(signature.length());
        modifySignature.setSingleLine(false);
        if (userInfo.getUserSex().equals("1")) {
            modifyGender.setText("男");
        } else if (userInfo.getUserSex().equals("2")) {
            modifyGender.setText("女");
        } else {
            modifyGender.setText("未知");
        }
    }

    @OnClick({R.id.toolbar_back, R.id.modify_save, R.id.modify_headimg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.modify_save:
                String et_name = modifyName.getText().toString().trim();
                String et_signature = CommonUtil.replaceBlank(modifySignature.getText().toString());
                if (TextUtils.isEmpty(et_name)) {
                    CommonUtil.showTextToast(this, "名称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(et_signature)) {
                    CommonUtil.showTextToast(this, "签名不能为空");
                    return;
                }

                if (!et_name.equals(name)) {
                    saveInfo(1, et_name);
                } else if (!et_signature.equals(signature)) {
                    saveInfo(2, et_signature);
                } else {
                    finish();
                }
                break;
            case R.id.modify_headimg:
                if (!CommonUtil.checkPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                        .CAMERA, Manifest.permission
                        .READ_EXTERNAL_STORAGE})) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest
                            .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                            .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Contact.CHECK_PERMISSION);
                } else {
//                    DialogPicSelector dialogPicSelector = new DialogPicSelector(this);
//                    InitiView.initiBottomDialog(dialogPicSelector);
//                    dialogPicSelector.show();
                    Intent intent = new Intent(this, ImageGridActivity.class);
                    startActivityForResult(intent, IMAGE_PICKER);
                }
                break;
        }
    }

    private void saveInfo(int chooseSelect, String modifyString) {
        UserPostBean userPostBean = new UserPostBean();
        userPostBean.setOpcode("UpdateUserInfor");
        userPostBean.setUseridx(userInfo.getUseridx());
        userPostBean.setName(modifyString);
        userPostBean.setChooseSelect(chooseSelect);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(userPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(jsonUser, Contact.KEY))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(Contact.USER_INFO)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = Contact.NET_ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = 2;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case Contact.REQUEST_TAKE_PHOTO:
                        File temp = new File(Environment.getExternalStorageDirectory() + "/" +
                                DialogPicSelector.imageDir);
//                        Uri uri = FileProvider.getUriForFile(this, getApplicationContext()
//                                .getPackageName() + ".provider", temp);
                        DialogPicSelector.photoZoom(this, Uri.fromFile(temp));
                        break;
                    case Contact.REQUEST_PHOTO_ZOOM:
                        DialogPicSelector.photoZoom(this, data.getData());
                        break;
                    case Contact.REQUEST_PHOTO_RESULT:
                        if (data != null) {
                            Uri resultUri = data.getData();
                            if (resultUri == null) {
                                resultUri = Uri.parse(data.getAction());
                            }
                            headImgPath = FileUtils.getRealFilePath(this, resultUri);
                            setHeadImag(headImgPath);
                        }
                        break;
                }
                break;
            case ImagePicker.RESULT_CODE_ITEMS:
                if (data != null && requestCode == IMAGE_PICKER) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    headImgPath = images.get(0).path;
                    setHeadImag(headImgPath);
                } else {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Contact.CHECK_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, ImageGridActivity.class);
                    startActivityForResult(intent, Contact.IMAGE_PICKER);
                } else {
                    new AlertDialog.Builder(this).setMessage("请允许牛拍获取您的相机、相册权限，以确保您能更换新的头像！")
                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
        }
    }

    private void setHeadImag(String headImgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(headImgPath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);

        String type = options.outMimeType;
        if (TextUtils.isEmpty(type)) {
            type = "未能识别的图片";
        } else {
            type = type.substring(6, type.length());
        }


        UserPostBean userPostBean = new UserPostBean();
        userPostBean.setOpcode("UpdateUserImage");
        userPostBean.setUseridx(userInfo.getUseridx());
        userPostBean.setType(type);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(userPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(jsonUser, Contact.KEY))
                    .add("base64", encodeString)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(Contact.USER_INFO)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("ModifyActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("ModifyActivity");
    }

}
