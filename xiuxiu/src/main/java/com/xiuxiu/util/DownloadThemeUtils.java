package com.xiuxiu.util;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.xiuxiu.model.ThemeInfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hzdykj on 2017/7/4.
 */

public class DownloadThemeUtils {
    private static final int SUCCESS_THEME = 3;//下载主题成功
    private static final int FAILED_THEME = 4;//下载主题失败
    private static final int GET_THEME_URL = 5;//获取主题下载地址成功
    private static final int GET_FAILED_THEME_URL = 6;//获取主题下载地址失败
    private static final int THEME_EXISTS = 7;//下载时,主题已存在




    private static DownloadThemeUtils sInstance;
    private OnDownloadListener mListener;

    private ExecutorService mThreadPool;

    private ThemeInfo resultBeen ;

    /**
     *设置回调监听器对象
     * @param mListener
     * @return
     */
    public DownloadThemeUtils setListener(OnDownloadListener mListener){
        this.mListener = mListener;
        return this;
    }

    //获取下载工具的实例
    public synchronized static DownloadThemeUtils getsInstance(){
        if (sInstance == null){
            try {
                sInstance = new DownloadThemeUtils();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return  sInstance;
    }

    /**
     * 下载的具体业务方法
     * @throws ParserConfigurationException
     */
    private DownloadThemeUtils() throws ParserConfigurationException {
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public void download(final ThemeInfo resultBeen){
        this.resultBeen = resultBeen;
        final String title = new String(resultBeen.getThemeName());
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case GET_THEME_URL:
                        downloadTheme(resultBeen,(String)msg.obj,this);
                        break;
                    case GET_FAILED_THEME_URL:
                       if (mListener != null) mListener.onFailed("");
                        break;
                    case SUCCESS_THEME:
                       if (mListener != null) mListener.onDowload("");
                        break;
                    case THEME_EXISTS:
//                       if (mListener != null) mListener.onFailed(title +"已存在");
                        break;
                }
            }
        };
        getDownloadThemeURL(resultBeen, handler);
    }

    //获取下载主题的URL
    private void getDownloadThemeURL(final ThemeInfo resultBeen, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String AudioUrl= new String(resultBeen.getThemeZip());
                System.out.println("AudioUrl" + AudioUrl);
                String url = AudioUrl;
                System.out.println("主题下载页面url = " + url);
                Message msg = handler.obtainMessage(GET_THEME_URL, url);
                msg.sendToTarget();
            }
        });
    }

    //下载主题
    private void downloadTheme(final ThemeInfo resultBeen, final String url, final Handler handler){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File themeDirFile = new File(XConstant.RES_THEME_ZIP_FILE_PATH);
                if (!themeDirFile.exists()){
                    themeDirFile.mkdirs();
                }
                String themeUrl = url;
                if (TextUtils.isEmpty(themeUrl)) return;
                String Title= new String(resultBeen.getThemeName());
                String[] theme = themeUrl.split("\\.(?=[^\\.]+$)");
                System.out.println(theme[0]);
                System.out.println(theme[1]);
                String target = themeDirFile + "/" + Title + "." + theme[1];
                System.out.println(themeUrl);
                System.out.println(target);
                File fileTarget = new File(target);
                if (fileTarget.exists()){
                    handler.obtainMessage(THEME_EXISTS).sendToTarget();
                    return;
                }else {
                    //使用OkHttpClient组件
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(themeUrl).build();
                    System.out.println(request);
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()){
                            PrintStream ps = new PrintStream(fileTarget);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes,0,bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_THEME).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAILED_THEME).sendToTarget();
                    }
                }
            }
        });
    }


    //自定义下载事件监听器
    public interface OnDownloadListener {
        public void onDowload(String themeUrl);
        public void onFailed(String error);
    }
}
