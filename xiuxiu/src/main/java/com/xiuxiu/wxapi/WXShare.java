package com.xiuxiu.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiuxiu.util.XConstant;

/**
 * Created by hzdykj on 2017/8/4.
 */

//微信分享
public class WXShare implements IWXAPIEventHandler {
    private IWXAPI api;
    private Context context;
    private int flag;//0 表示分享给微信好友哦，1表示分享到朋友圈,2表示收藏
    private String title;
    private String content;
    private String photo;
    private String contentCircle;
    private String link;


    public WXShare(Context context, int flag, String title, String content, String photo, String contentCircle, String link) {
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
    public void share2WX() {
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
        }else if (flag == 2) {
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

    //分享是否成功查询，可以通过IWXAPIEventHandler回调接口来处理
    @Override
    public void onReq(BaseReq rep) {
        Log.e("WXShare", "微信分享成功回调了" + "----正确返回----");
    }

    @Override
    public void onResp(BaseResp resp) {
        Toast.makeText(context, "openid = " + resp.openId, Toast.LENGTH_SHORT).show();

        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            Toast.makeText(context, "code =" + ((SendAuth.Resp) resp).code, Toast.LENGTH_SHORT).show();
        }

        String result = "";

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "-sucessful-";
                Log.e("WXShare", "微信分享成功回调了" + "----正确返回----");
                if (flag == 1) {
                    Toast.makeText(context, "朋友圈分享成功", Toast.LENGTH_LONG).show();
                } else if (flag == 0) {
                    Toast.makeText(context, "发送给朋友成功", Toast.LENGTH_LONG).show();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "-cancle-";
                if (flag == 1) {
                    Toast.makeText(context, "朋友圈分享取消", Toast.LENGTH_LONG).show();
                } else if (flag == 0) {
                    Toast.makeText(context, "发送给朋友取消", Toast.LENGTH_LONG).show();
                }

                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "-error code-";
                break;
            default:
                result = "-unknow  error-";
                break;
        }
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}