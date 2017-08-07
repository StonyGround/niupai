package com.xiuxiu.util;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;

import com.xiuxiu.model.DubMusicDataInfo;
import com.xiuxiu.model.DubSearchMusicDataInfo;

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
 * Created by hzdykj on 2017/6/16.
 * 网络音乐下载工具类
 */
public class DownloadSearchUtils {

    private static final int SUCCESS_LRC = 1;//下载歌词成功
    private static final int FAILED_LRC = 2;//下载歌词失败
    private static final int SUCCESS_MP3 = 3;//下载歌曲成功
    private static final int FAILED_MP3 = 4;//下载歌曲失败
    private static final int SUCCESS_MP3_ACC = 8;//下载伴奏歌曲成功
    private static final int FAILED_MP3_ACC = 9;//下载伴奏歌曲失败
    private static final int GET_MP3_URL = 5;//获取音乐下载地址成功
    private static final int GET_FAILED_MP3_URL = 6;//获取音乐下载地址失败
    private static final int MUSIC_EXISTS = 7;//下载时,音乐已存在


    private static DownloadSearchUtils sInstance;
    private OnDownloadListener mListener;

    private ExecutorService mThreadPool;

    private DubSearchMusicDataInfo.ResultBean resultBeen;

    /**
     * 设置回调监听器对象
     *
     * @param mListener
     * @return
     */
    public DownloadSearchUtils setListener(OnDownloadListener mListener) {
        this.mListener = mListener;
        return this;
    }

    //获取下载工具的实例
    public synchronized static DownloadSearchUtils getsInstance() {
        if (sInstance == null) {
            try {
                sInstance = new DownloadSearchUtils();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return sInstance;
    }

    /**
     * 下载的具体业务方法
     *
     * @throws ParserConfigurationException
     */
    private DownloadSearchUtils() throws ParserConfigurationException {
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public void download(final DubSearchMusicDataInfo.ResultBean resultBeen) {
        this.resultBeen = resultBeen;
        final String title = new String(Base64.decode(resultBeen.getTitle().getBytes(), Base64.DEFAULT));
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUCCESS_LRC:
//                       if (mListener != null) mListener.onDowload(title +"的歌词下载成功");
                        break;
                    case FAILED_LRC:
//                       if (mListener != null) mListener.onFailed(title +"的歌词下载失败");
                        break;
                    case GET_MP3_URL:
                        downloadMusic(resultBeen, (String) msg.obj, this);
                        break;
                    case GET_FAILED_MP3_URL:
//                       if (mListener != null) mListener.onFailed(title +"的MP3下载失败");
                        break;
                    case SUCCESS_MP3:
                        if (mListener != null) mListener.onDowload(title);
                        if (!TextUtils.isEmpty(resultBeen.getLraudio())) {
                            downloadLRC(title, this);
                            downloadAccMusic(title, this);
                        }
                        break;
                    case FAILED_MP3:
//                       if (mListener != null) mListener.onFailed(title +"的MP3下载失败");
                        break;
                    case SUCCESS_MP3_ACC:
//                       if (mListener != null) mListener.onDowload(title +"的伴奏MP3已经下载");
                        break;
                    case FAILED_MP3_ACC:
//                       if (mListener != null) mListener.onFailed(title +"的伴奏MP3下载失败");
                        break;
                    case MUSIC_EXISTS:
//                       if (mListener != null) mListener.onFailed(title +"已存在");
                        break;
                }
            }
        };
        getDownloadMusicURL(resultBeen, handler);
    }

    //获取下载音乐的URL
    private void getDownloadMusicURL(final DubSearchMusicDataInfo.ResultBean resultBeen, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String AudioUrl = new String(Base64.decode(resultBeen.getAudioUrl().getBytes(), Base64.DEFAULT));
                System.out.println("AudioUrl" + AudioUrl);
                String url = AudioUrl;
                System.out.println("歌曲下载页面url = " + url);
                Message msg = handler.obtainMessage(GET_MP3_URL, url);
                msg.sendToTarget();

            }
        });
    }

    //下载歌词
    public void downloadLRC(final String title, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String lraUrl = new String(Base64.decode(resultBeen.getLraudio().getBytes(), Base64.DEFAULT));
                    System.out.println("歌词下载页面url = " + lraUrl);
//                    Document doc = Jsoup.connect(url).userAgent(XConstant.USER_AGENT).timeout(6000).get();
//                    Elements lrcUrls = doc.select("span.lyric-action");
                    File LrcDirFile = new File(XConstant.RES_ACCLYRICS_FILE_PATH);
                    System.out.println("LrcDirFile : " + LrcDirFile);
                    if (!LrcDirFile.exists()) {
                        LrcDirFile.mkdirs();
                    }
                    String[] lr = lraUrl.split("\\.(?=[^\\.]+$)");
//                    String lr = lraUrl.substring(lraUrl.lastIndexOf('.') + 1);
                    System.out.println(lr[0]);
                    System.out.println(lr[1]);
                    String target = LrcDirFile + "/" + title + "." + lr[1];
                    File fileTarget = new File(target);
                    if (fileTarget.exists()) {
                        handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                        return;
                    } else {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(lraUrl).build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            PrintStream ps = new PrintStream(new File(target));
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes, 0, bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_LRC, target).sendToTarget();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //下载MP3
    private void downloadMusic(final DubSearchMusicDataInfo.ResultBean resultBeen, final String url, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File musicDirFile = new File(XConstant.RES_MUSIC_FILE_PATH);
                if (!musicDirFile.exists()) {
                    musicDirFile.mkdirs();
                }
                String mp3url = url;
                String Title = new String(Base64.decode(resultBeen.getTitle().getBytes(), Base64.DEFAULT));
                String[] mp3 = mp3url.split("\\.(?=[^\\.]+$)");
                System.out.println(mp3[0]);
                System.out.println(mp3[1]);
                String target = musicDirFile + "/" + Title + "." + mp3[1];
                System.out.println(mp3url);
                System.out.println(target);
                File fileTarget = new File(target);
                if (fileTarget.exists()) {
                    handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                    return;
                } else {
                    //使用OkHttpClient组件
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(mp3url).build();
                    System.out.println(request);
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            PrintStream ps = new PrintStream(fileTarget);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes, 0, bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_MP3).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAILED_MP3).sendToTarget();
                    }
                }
            }
        });
    }

    //下载伴奏
    public void downloadAccMusic(final String title, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File musicDirFile = new File(XConstant.RES_ACC_MUSIC_FILE_PATH);
                    if (!musicDirFile.exists()) {
                        musicDirFile.mkdirs();
                    }
                    String accUrl = new String(Base64.decode(resultBeen.getBzaudiourl().getBytes(), Base64.DEFAULT));
//                    String Title= new String(Base64.decode(resultBeen.getTitle().getBytes(), Base64.DEFAULT));
                    if (TextUtils.isEmpty(accUrl)) return;
                    String[] acc = accUrl.split("\\.(?=[^\\.]+$)");
                    System.out.println(acc[0]);
                    System.out.println(acc[1]);
                    String target = musicDirFile + "/" + title + "." + acc[1];
                    File fileTarget = new File(target);
                    if (fileTarget.exists()) {
                        handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                        return;
                    } else {
                        //使用OkHttpClient组件
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(accUrl).build();
                        System.out.println(request);
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            PrintStream ps = new PrintStream(fileTarget);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes, 0, bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_MP3_ACC, target).sendToTarget();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //自定义下载事件监听器
    public interface OnDownloadListener {
        public void onDowload(String mp3Url);

        public void onFailed(String error);
    }
}
