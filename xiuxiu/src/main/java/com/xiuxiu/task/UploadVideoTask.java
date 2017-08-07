package com.xiuxiu.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xiuxiu.phttprequest.PMultiPartEntity;
import com.xiuxiu.phttprequest.PMultiPartEntity.PostProgressListener;
import com.xiuxiu.util.GlobalDef;
import com.xiuxiu.util.PCommonUtil;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;

// 上传视频
public class UploadVideoTask extends AsyncTask<Void, Integer, String> {
    private static final String TAG="UploadVideoTask";

    private Handler handler;
    // 用户id/视频描述信息/视频标签/地理位置/视频及封面文件路径
    private String userId;
    private String descriptions;
    private String tags;
    private String strLocation;
    private String videoFilePath;
    private String coverFilePath;
    private boolean isUploadStop = false;

    private long videoSize;//上传文件大小
    private long imageSize;
    private HttpPost httpPost = null;

    public int getVideoId() {
        return videoId;
    }

    private int videoId = -1;// 上传的视频ID

    private Context context;

    private int identify;

    private String imageScale;

    public UploadVideoTask(
            Context context, Handler _handler,
            String _userId, String _tags, String _descriptions,
            String _strLocation, String _videoFilePath, String _coverFilePath, int identify, String imageScale) {
        this.context = context;
        this.handler = _handler;
        this.userId = _userId;
        this.tags = _tags;
        this.descriptions = _descriptions;
        this.strLocation = _strLocation;
        this.videoFilePath = _videoFilePath;
        this.coverFilePath = _coverFilePath;
        this.identify = identify;
        this.imageScale = imageScale;
        if (TextUtils.isEmpty(this.imageScale)) {
            this.imageScale = "0.75";
        }
    }

    public boolean checkIsCancelled() {
        return isUploadStop;
    }

    // 上传视频封面
    private int uploadCover(HttpClient httpClient, HttpContext httpContext) {
        String result = null;
        String baseUrl = null, urlParams = null, urlSecret = null;

        baseUrl = XConstant.DOWNLOAD_HOST + "works/uploadVideoInfoNew";
        try {
            urlParams =
                    "?uidx=" + userId
                            + "&descriptions=" + URLEncoder.encode(URLEncoder.encode(descriptions, "UTF-8"), "UTF-8")
                            + "&tags=" + URLEncoder.encode(tags, "UTF-8")
                            + "&area=" + URLEncoder.encode(strLocation, "UTF-8")
                            + "&vrtype=1&videoSize=" + imageScale + "&imgScale=" + imageScale + "&identify=" + identify;
            urlSecret = PCommonUtil.generateAPIStringWithSecret(context, baseUrl, urlParams);
            httpPost = new HttpPost(urlSecret);
            PMultiPartEntity multipartContent = new PMultiPartEntity(new PostProgressListener() {
                @Override
                public void transferred(long num) {
                    if (checkIsCancelled()) {
                        httpPost.abort();
                        return;
                    }

                    publishProgress((int) (5.0f * num / imageSize));
                }
            });
            multipartContent.addPart("imgfile", new FileBody(new File(coverFilePath)));
            imageSize = multipartContent.getContentLength();
            httpPost.setEntity(multipartContent);

            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            if (null != httpResponse) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    if (null != inputStream) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[8 * 1024];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        inputStream.close();
                        result = baos.toString();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int nErrorCode = GlobalDef.ErrorCodeSystem;
        if (result != null) {
            JSONObject jsonObject = PCommonUtil.parserString2JsonObject(result);

            try {
                String errorCode = (null == jsonObject) ? GlobalDef.ErrorCodeError : jsonObject.getString("errorcode");
                Log.d("errorCode", errorCode + "-----");
                if (GlobalDef.ErrorCodeSuccess.equals(errorCode)) {
                    // 上传成功后的视频id
                    return jsonObject.getInt("result");
                }
            } catch (JSONException e) {
                // nErrorCode = GlobalDef.ErrorCodeSystem;
            }
        }

        return nErrorCode;
    }

    @Override
    protected String doInBackground(Void... params) {
        String baseUrl = null, urlParams = null, urlSecret = null;
        String result = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000 * 5);
            HttpContext httpContext = new BasicHttpContext();

            // 先上传封面
            videoId = uploadCover(httpClient, httpContext);
            if (videoId == GlobalDef.ErrorCodeSystem) {
                return result;
            }

            // 再上传视频
            baseUrl = XConstant.DOWNLOAD_HOST + "works/uploadVideo";
            urlParams = "?vid=" + videoId + "&uidx=" + userId;
            urlSecret = PCommonUtil.generateAPIStringWithSecret(context, baseUrl, urlParams);
            httpPost = new HttpPost(urlSecret);

            // multipart
            PMultiPartEntity multipartContent = new PMultiPartEntity(new PostProgressListener() {
                @Override
                public void transferred(long num) {
                    if (checkIsCancelled()) {
                        httpPost.abort();
                        return;
                    }

                    publishProgress((int) (5.0 + 95.0f * num / videoSize));
                }
            });
            multipartContent.addPart("videofile", new FileBody(new File(videoFilePath)));
            videoSize = multipartContent.getContentLength();
            httpPost.setEntity(multipartContent);

            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            if (null != httpResponse) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    if (null != inputStream) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[8 * 1024];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        inputStream.close();
                        result = baos.toString();
                    }
                }
            }
        } catch (Exception e) {
            // MyLog.showException(e);
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        // 更新UI
        Message msg = handler.obtainMessage();
        msg.what = GlobalDef.MSG_UPLOAD_VIDEO_TIPS;
        msg.obj = values[0];
        handler.sendMessage(msg);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (isCancelled()) {
            return;
        }

        int nErrorCode = 0;
        String videoPicUrl = "";// 视频封面

        if (result != null) {
            JSONObject jsonObject = PCommonUtil.parserString2JsonObject(result);
            // Log.d("zhang","上传视频:" + jsonObject);
            try {
                String errorCode = (null == jsonObject) ? GlobalDef.ErrorCodeError : jsonObject.getString("errorcode");
                if (GlobalDef.ErrorCodeSuccess.equals(errorCode)) {
                    // 返回值
                    // nResult = jsonObject.getInt("result");
                    videoPicUrl = PCommonUtil.decodeBase64(jsonObject.getString("videoPicUrl"));
                    // Log.d("zhang","上传视频封面:" + videoPicUrl);
                } else if (GlobalDef.ErrorCodeError.equals(errorCode)) {
                    nErrorCode = GlobalDef.ErrorCodeSystem;
                } else {
                    nErrorCode = jsonObject.getInt("errorcode");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            nErrorCode = GlobalDef.ErrorCodeSystem;
        }

        // 更新UI
        Message msg = handler.obtainMessage();
        msg.what = (nErrorCode == 0) ? GlobalDef.MSG_UPLOAD_VIDEO_SUSS : GlobalDef.MSG_UPLOAD_VIDEO_FAIL;
        msg.obj = videoPicUrl;
        msg.arg1 = videoId;
        handler.sendMessage(msg);
    }
}
