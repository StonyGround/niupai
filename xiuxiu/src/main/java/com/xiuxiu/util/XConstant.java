package com.xiuxiu.util;

import android.os.Environment;

// 工程相关常量
public class XConstant {

    public static final int CHECK_PERMISSION=0;

    // 页面布局
    public static final int HOME_VIEW = 0;// 首页
    public static final int TOPICS_VIEW = 1;// 广场
    public static final int RECORD_VIEW = 2;// 录制
    public static final int DISCOVER_VIEW = 3;// 发现
    public static final int PERSON_VIEW = 4;// 个人
    //public static final int LOGIN_VIEW   = 5;// 登陆

    // 视频采集参数
    public static final int FRAME_SRC_WIDTH = 640;
    public static final int FRAME_SRC_HEIGHT = 480;
    public static final int FRAME_DST_HEIGHT = 640;
    public static final int FRAME_DST_WIDTH = 480;

    // 摄像头前置/后置
    public static final int CAMERA_BACK = 0;
    public static final int CAMERA_FRONT = 1;
    // 画中画映射系数
    public static final double PIP_MAPPING_COEF = 0.2846;

    // 触屏类型>>下拉刷新/上拉加载
    public static final int PULL_TO_UP = 19;
    public static final int PULL_TO_DOWN = 20;

    // 主题/滤镜/伴奏/模板/配乐
    public static final int CLASS_ID_THEME = 0;
    public static final int CLASS_ID_FILTER = 1;
    public static final int CLASS_ID_ACCSONG = 2;
    public static final int CLASS_ID_TEMPLATE = 3;
    public static final int CLASS_ID_INCMUSIC = 4;

    // 播放状态
    public static final int PLAYER_STATUS_START = 0;// 开始
    public static final int PLAYER_STATUS_PAUSE = 1;// 暂停
    public static final int PLAYER_STATUS_RESUME = 2;// 恢复
    public static final int PLAYER_STATUS_STOP = 3;// 停止
    public static final int PLAYER_STATUS_RESTART = 4;// 停止
    public static final int PLAYER_STATUS_SEEK = 5;// 停止
    public static final int PLAYER_STATUS_PAUSE_RESUME = 6;//暂停或恢复
    public static final int PLAYING_TYPE_LISTEN = 0;// 试听
    public static final int PLAYING_TYPE_SINGING = 1;// 演唱
    public static final int PLAYER_STATUS_ADD = 7;// 音量加
    public static final int PLAYER_STATUS_SUBTRACT = 8;// 演唱减

    public static final String MUSIC_DURATION = "com.jhjj9158.niupaivideo.service.MUSIC_DURATION";// 当前音乐时长
    public static final String MUSIC_CURRENT_TIME = "com.jhjj9158.niupaivideo.service.MUSIC_CURRENT_TIME";// 当前音乐播放时间
    public static final String MUSIC_COMPLETION = "com.jhjj9158.niupaivideo.service.MUSIC_COMPLETION";  // 当前音乐播放完毕

    // 录制状态
    public static final int RECORD_STATUS_START = 0;// 开始
    public static final int RECORD_STATUS_STOP = 1;// 停止
    public static final int RECORD_STATUS_FINISH = 2;// 完成
    public static final int RECORD_STATUS_ERROR = 3;// 异常
    public static final int RECORD_STATUS_PIP = 4;// 画中画录制模式
    public static final int RECORD_COUNTDOWN_FINISHED = 5;// 倒计时动画
    public static final int RECORD_TIME = 6; //录制时长
    public static final int RECORD_STATUS_ERROR_AUDIO = 7;// 音频异常

    // 录制模式
    public static final int RECORD_MODE_NOR = 0;// 正常录制
    public static final int RECORD_MODE_PIP = 1;// 画中画录制

    // 录制时长
    public static final int RECORD_NOR_MIN_TIME = 2 * 1000;// 录制视频的最小时长
    public static final int RECORD_NOR_MAX_TIME = 30 * 1000 + 150;// 正常录制时最大录制时长
    public static final int RECORD_ACC_MAX_TIME = 30 * 1000 + 150;// 使用伴奏时最大录制时长
    public static final int RECORD_PIP_MAX_TIME = 60 * 1000 - 150;// 使用画中画时最大录制时长
    public static final int INVALID_VALUE = -999;// 无效值

    // 工作资源默认存放路径
    public final static String MAIN_FILE_PATH = Environment.getExternalStorageDirectory() + "/Android/data/com.jhjj9158.niupaivideo/";
    public final static String DCIM_FILE_PATH = Environment.getExternalStorageDirectory() + "/DCIM/";
    public final static String VIDEO_FILE_PATH = MAIN_FILE_PATH + "video/";
    public final static String LOCAL_VIDEO_FILE_PATH = MAIN_FILE_PATH + "localvideo/";
    public final static String LOCAL_VIDEO_OUTPUT_FILE_PATH = LOCAL_VIDEO_FILE_PATH + "output/";
    public final static String LOCAL_VIDEO_COPE_FILE_PATH = LOCAL_VIDEO_FILE_PATH + "copevideos/";
    //public final static String MUSIC_FILE_PATH = MAIN_FILE_PATH + "music/";
    public final static String TEMPLATE_FILE_PATH = MAIN_FILE_PATH + "template/";
    public final static String PICTURE_FILE_PATH = MAIN_FILE_PATH + "picture/";
    public final static String CACHE_FILE_PATH = MAIN_FILE_PATH + "cache";

    public final static String RES_VIDEO_FILE_PATH = TEMPLATE_FILE_PATH + "video/";
    public final static String RES_MUSIC_FILE_PATH = TEMPLATE_FILE_PATH + "music/";
    public final static String RES_THEME_FILE_PATH = TEMPLATE_FILE_PATH + "theme/";
    public final static String RES_THEME_ZIP_FILE_PATH = RES_THEME_FILE_PATH + "zip/";
    public final static String RES_THEME_ICON_FILE_PATH = RES_THEME_FILE_PATH + "icon/";
    public final static String RES_ICON_FILE_PATH = TEMPLATE_FILE_PATH + "icon/";//图标
    public final static String RES_ACCLYRICS_FILE_PATH = TEMPLATE_FILE_PATH + "acc_lyrics/";//AccSong歌词
    public final static String RES_ACC_MUSIC_FILE_PATH = TEMPLATE_FILE_PATH + "acc_music/";//Acc伴奏
    public final static String RES_TMPLYRICS_FILE_PATH = TEMPLATE_FILE_PATH + "text/";//视频模板歌词

    // 缓存目录
    public final static String IMAGE_CACHE_FILE_PATH = MAIN_FILE_PATH + "cache/image_cache/";//图片缓存
    public final static String VIDEO_CACHE_FILE_PATH = MAIN_FILE_PATH + "cache/videos_cache/";
    public final static String VIDEO_CACHE_TEMP_FILE_PATH = MAIN_FILE_PATH + "cache/videotemp_cache/";
    public final static String VIDEO_TEMP_CACHE_FILE_PATH = MAIN_FILE_PATH + "cache/videotemp_cache/";
    public final static String VIDEO_THUMB_CACHE_FILE_PATH = MAIN_FILE_PATH + "cache/videothumb_cache/";
    public final static String ADVAPK_CACHE_FILE_PATH = MAIN_FILE_PATH + "cache/apk_cache/";//广告app
    public final static String SEARCH_CACHE_FILE_PATH = MAIN_FILE_PATH + "cache/search_cache/";//广场搜索记录缓存

    // 网络状态
    public static final int NET_WORK_NULL = -1;// 无网络
    public static final int NET_WORK_WIFI = 0;// wifi
    public static final int NET_WORK_MOBILE = 1;// 其他网络
    //头像图片存放图片的宽高
    public static final int IMAGE_USERABATAR_WIDTH = 480;//宽
    public static final int IMAGE_USERABATAR_HEIGHT = 480;//高
    //主页背景存放图片的宽高
    public static final int IMAGE_HOMEPAGEBG_WIDTH = 640;//宽
    public static final int IMAGE_HOMEPAGEBG_HEIGHT = 320;//高
    //帐号安全起始页索引
    public static final int ACCOUNT_SECURITY_NO_BINDPHONE = 0;//未绑定手机
    public static final int ACCOUNT_SECURITY_IS_BINDDPHONE = 1;//已绑定手机
    public static final int ACCOUNT_SECURITY_RETRIEVE_PASSWORD = 2;//找回密码
    public static final int ACCOUNT_SECURITY_RESET_PASSWORD = 3;//重置密码
    public static final int ACCOUNT_SECURITY_EXCHANGE = 4;//兑换

    //播放设置
    public static final int AUTOPLAY_ONLYWIFI = 0;//仅wifi自动播放
    public static final int AUTOPLAY_ALWAYS = 1;//始终自动播放
    public static final int AUTOPLAY_FORBID = 2;//禁止自动播放
    public static final String PLAY_MODE = "playmode";//播放模式

    //配置文件
    public static final String CONFIG_FILENAME = "91pai_setting";

    //打赏模式
    public static final String SPONSOR_MODE = "sponsormode";

    //引导配置文件
    public static final String GUIDE_NAME = "first_pref";
    public static final String GUIDE_MUSIC = "guide_music";
    public static final String GUIDE_STICKER = "guide_sticker";
    public static final String GUIDE_THEME = "guide_theme";
    public static final String GUIDE_DUBBING = "guide_dubbing";

    //下载状态
    public static final int DOWNLOAD_STATUS_UNLOAD = 0;//尚未加载
    public static final int DOWNLOAD_STATUS_LOADING = 1;//下载中
    public static final int DOWNLOAD_STATUS_COMPLETED = 2;//下载完成
    public static final int DOWNLOAD_STATUS_ERROR = 3;//下载错误
    public static final int DOWNLOAD_STATUS_STOP = 4;//下载停止
    public static final String CURRENT_SELECT_MUSIC = "current_select_music";
    //记录安装状态的文件名
    public static final String INSTALLSTATE_FILENAME = "INSTALLSTATE_XIUXIU";

    //兑换方式
    public static final int EXCHANGE_QQCOINS = 1;  //Q币
    public static final int EXCHANGE_CALLS = 2;    //话费
    public static final int EXCHANGE_ALIPAY = 3;   //支付宝

    // 计算经纬度的常量
    public static final double EARTH_RADIUS = 6378.137;// 地球赤道半径(km)
    public static final double DEFAULT_LON = 108.55;// 默认经度(约为中国版图中心)
    public static final double DEFAULT_LAT = 34.32;// 默认纬度

    //登录状态
    public static final int LOGOUT = 0;//注销
    public static final int LOGIN = 1;//登录

    // 发送短信接口参数
    public static final int BINDING_CODE = 1;// 绑定
    public static final int CHANGE_PASSWORD_CODE = 2;// 改密码
    public static final int EXCHANGE_CODE = 3;// 兑换
    public static final int VALIDTIME = 90;//验证码有效时长(秒)

    // 昵称的字符数限制
    public static final int MAX_COUNT = 14;// 最大输入字数为14(中文一个字符占两个)
    public static final int MIN_COUNT = 4;// 最小输入字数为4

    // APP_KEY  sina
    public static final String SINA_APP_KEY = "4252920573";
    // 微信
    public static final String WX_APP_ID = "wx25af7b2b8865a14c";
    // 分享标志
    public static final int QQ_SHARE = 0;
    public static final int WECHAT_SHARE = 1;
    public static final int FRIENDS_SHARE = 2;
    public static final int TXWB_SHARE = 3;
    public static final int WEIBO_SHARE = 4;
    public static final int QZONE_SHARE = 5;

    // 需要过滤掉的关键字
    public static final String flitkey1[] = {
            "我操", "我日你妈", "mm", "jj", "bb", "脱", "拖", "托", "操你妈", "你好难看呀", "我想操你",
            "我想干你", "你中奖了", "jb", "sb", "几吧", "煞笔", "www.", "com.", "@",
            "http://", "baobei", "admin", "&", "%", "9158", "就约我吧", "我草", "叼", "卧槽", "色情", "情色",
            "a片", "毛片", "女优", "av", "妓女", "鸭子"};

    public static final String paString = "系.*统|客.*服|公.*告|中.*奖|消.*息|官.*方|大.*江|运.*营|技.*术|巡.*管|代.*理|[9９].*[1１].*[5５].*[8８]";
    public static final String paString2 = "统.*系|服.*客|告.*公|奖.*中|息.*消|方.*官|江.*大|营.*运|术.*技|管.*巡|理.*代|[9９].*[1１].*[5５].*[8８]";

    // 保存AccessToken信息文件名
    public static final String PREFERENCES_NAME = "com_xiuxiu_tencent_share";

    //音乐
    /*http://service.niupaisp.com/works/getVoiceInfoList?mType=3&tags=1&begin=4&num=20&secret=075EE85B880EB94A06E924F50C243654&versioncode
	=1.0.0&
	client=iphone&unique=MkNENDI2MDMtMTc1NC00NDYxLTlBOEMtOUYwNTBFN0VBRjM5&aes=false*/
    public static final String HOST = "http://service.niupaisp.com/";
    public static final String DOWNLOAD_HOST = "http://upload.niupaisp.com/";
    public static final String GET_VOICE_INFO_LIST = "works/getVoiceInfoList";
    public static final String SEARCH_VOICE_INFO_LIST = "works/searchVoiceInfoList";
    public static final String GET_MATERIAL_TYPE = "works/getMaterialTypeNew";
    public static final String SECRET = "075EE85B880EB94A06E924F50C243654";
    public static final String VERSIONCODE = "1.0.0";
    public static final String CLIENT = "iphone";
    public static final String UNIQUE = "MkNENDI2MDMtMTc1NC00NDYxLTlBOEMtOUYwNTBFN0VBRjM5";

    //下载
    public static final String DIR_MUSIC = "/drm_music";
    public static final String DIR_MUSIC_ACC = "/drm_music/acc/";
    public static final String DIR_LRC = "/drm_music/lrc/";
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:45.0) Gecko/20100101 Firefox/45.0";

    //视频渲染
    public static final int VIDEO_RENDER_START = 1;
    public static final int VIDEO_RENDER_PAUSE = 2;

    public static final String NICKNAME = "nickName";

    public static final int MUSIC_ACTIVITY_RESULT = 1;
    public static final int TEXT_ACTIVITY_RESULT = 2;

    public static final String FROM_TYPE_MY = "from_type_my";

    public static final String WEIXIN_APP_ID = "wx17181f643ff9a6c8";
}