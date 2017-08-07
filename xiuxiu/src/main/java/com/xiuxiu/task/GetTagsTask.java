package com.xiuxiu.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xiuxiu.phttprequest.PHttpRequest;
import com.xiuxiu.util.AESUtil;
import com.xiuxiu.util.GlobalDef;
import com.xiuxiu.util.PCommonUtil;
import com.xiuxiu.util.XConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author zhangyu
 *         获取热门标签
 *         2014-9-15
 */
public class GetTagsTask extends AsyncTask<Void, String, String> {

    private static final String TAG = "GetTagsTask";

    private Handler handler = null;
    private Context context;

    public GetTagsTask(Context context, Handler _handler) {
        this.context = context;
        this.handler = _handler;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        PHttpRequest request = PHttpRequest.requestWithURL(
                PCommonUtil.generateAPIStringWithSecret(context, XConstant.HOST + "works/getVRVedioType?opr=3", ""));

        String result = request.startSyncRequestString();
        Log.d(TAG, "doInBackground: " + result);
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected void onPostExecute(String result) {
        if (isCancelled() || handler == null) {
            return;
        }

//        JSONObject jsonObject = PCommonUtil.parserString2JsonObject(result);

        int nErrorCode = 0;
        String[] strings = null;

        try {
            JSONObject jsonObject= new JSONObject(AESUtil.decode(result));
            String errorCode = (null == jsonObject) ? GlobalDef.ErrorCodeError : jsonObject.getString("errorcode");
            if (GlobalDef.ErrorCodeSuccess.equals(errorCode)) {
                nErrorCode = 1;
                JSONArray jsonResultList = jsonObject.getJSONArray("result");
                int resultListLen = jsonResultList.length();
                strings = new String[resultListLen];
                for (int i = 0; i < resultListLen; i++) {
                    String sTags = PCommonUtil.decodeBase64(jsonResultList.getJSONObject(i).optString("vrname"));
                    strings[i] = sTags;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage();
        if (nErrorCode == 1) {
            msg.what = GlobalDef.MSG_GETTAGS_SUSS;
            msg.obj = strings;
        } else {
            msg.what = GlobalDef.MSG_GETTAGS_FAIL;
        }

        handler.sendMessage(msg);
    }
}
