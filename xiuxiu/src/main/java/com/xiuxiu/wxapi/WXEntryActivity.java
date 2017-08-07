
package com.xiuxiu.wxapi;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName();

    private IWXAPI api;
    private Context context;
    private int flag;//0 表示分享给微信好友哦，1表示分享到朋友圈,2表示收藏
    private String title;
    private String content;
    private String photo;
    private String contentCircle;
    private String link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 微信注册初始化
        api = WXAPIFactory.createWXAPI(context, XConstant.WEIXIN_APP_ID, true);
        api.registerApp(XConstant.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
        Log.e(TAG, "微信分享成功回调了" + "--------");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    public WXEntryActivity(Context context, int flag, String title, String content, String photo, String contentCircle, String link) {
        super();
        this.context = context;
        this.flag = flag;
        this.title = title;
        this.content = content;
        this.photo = photo;
        this.contentCircle = contentCircle;
        this.link = link;
        // 微信注册初始化
        api = WXAPIFactory.createWXAPI(context, XConstant.WEIXIN_APP_ID, true);
        api.registerApp(XConstant.WEIXIN_APP_ID);
    }

    /**
     * 分享到微信里边的内容，其中flag 0是朋友圈，1是好友，2是收藏
     * 分享前判断下是否有安装微信，没有就不提示用户
     */
    public void shareWX() {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "您还未安装微信客户端，请安装！", Toast.LENGTH_SHORT).show();
            return;
        }
        //1、创建WXWebpageObject对象，用于封装要发送的URL
        WXWebpageObject webpage = new WXWebpageObject();
        if (link != null) {
            webpage.webpageUrl = link;
        }

        //2、创建WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(webpage);

        if (flag == 0) {
            if (title != null) {
                msg.title = title;
            }

        } else if (flag == 1) {
            if (contentCircle != null) {
                msg.title = contentCircle;
            }
        } else if (flag == 2) {
            if (title != null) {
                msg.title = title;
            }
        }

        if (contentCircle != null) {
            msg.description = contentCircle;
        }

        //3、设置缩略图

        int MAX_SIZE_THUMBNAIL_BYTE = 1 << 15;
        Bitmap originalImg = BitmapFactory.decodeFile(photo);
        // thumbnail
        Bitmap thumbnailImg = originalImg;
        if (thumbnailImg.getByteCount() > MAX_SIZE_THUMBNAIL_BYTE) {
            double scale = Math.sqrt(1.0 * thumbnailImg.getByteCount() / MAX_SIZE_THUMBNAIL_BYTE);
            int scaledW = (int) (thumbnailImg.getWidth() / scale);
            int scaledH = (int) (thumbnailImg.getHeight() / scale);
            thumbnailImg = Bitmap.createScaledBitmap(originalImg, scaledW, scaledH, true);
        }
        msg.setThumbImage(thumbnailImg);

        //4、创建SendMessageToWX.Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag;
        api.sendReq(req);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e(TAG, "微信分享成功回调了" + "--------");
    }

    //微信回调
    @Override
    public void onResp(BaseResp resp) {   //分享之后的回调
        String result = "";
        switch (resp.errCode) {
            case 2: //正确返回
                result = "正确返回";
                Log.e(TAG, "微信分享成功回调了" + "----正确返回----");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL: //取消返回
                result = "取消返回";
                Log.e(TAG, "微信分享取消回调了" + "----取消返回----");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED: //失败返回
                result = "失败返回";
                Log.e(TAG, "微信分享失败回调了" + "----失败返回----");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED: //被拒绝
                result = "被拒绝返回";
                Log.e(TAG, "微信分享被拒绝回调了" + "----被拒绝----");
                break;
            default:
                result = "失败返回";
                Log.e(TAG, "微信分享失败回调了" + "----失败返回----");
                break;
        }
        Util.showTextToast(WXEntryActivity.this, result);
    }
}

