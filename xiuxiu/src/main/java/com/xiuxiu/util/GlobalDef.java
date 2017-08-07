package com.xiuxiu.util;
/**
 * @author chy
 * @version 创建时间：2014-07-20 下午16:23:29
 * 类说明 全局静态常量定义
 */
public class GlobalDef 
{
	public static final int WM_INIT_DATA = 1; 
	public static final int WM_REGISTER_SUSS = WM_INIT_DATA + 1; //注册成功
	public static final int WM_REGISTER_EXISTS = WM_INIT_DATA + 2; //注册用户名已经存在
	public static final int WM_REGISTER_FAIL = WM_INIT_DATA + 3; //注册失败
	public static final int WM_LOGIN_SUSS = WM_INIT_DATA + 4; //登陆成功
	public static final int WM_LOGIN_FAIL = WM_INIT_DATA + 5; //登陆失败
	public static final int WM_SETLOGIN_SUSS = WM_INIT_DATA + 6; //设置地理位置成功
	public static final int WM_SETLOGIN_FAIL = WM_INIT_DATA + 7; //设置地理位置失败
	public static final int WM_FANS_SUSS = WM_INIT_DATA + 8; //获取粉丝成功
	public static final int WM_FANS_FAIL = WM_INIT_DATA + 9; //获取粉丝失败
	public static final int WM_FOLLOWUSER_SUSS = WM_INIT_DATA + 10; //获取粉丝成功
	public static final int WM_FOLLOWUSER_FAIL = WM_INIT_DATA + 11; //获取粉丝失败
	public static final int WM_GETALLVIDEO_SUSS = WM_INIT_DATA + 12; //获取个人所有视频信息成功
	public static final int WM_GETALLVIDEO_FAIL = WM_INIT_DATA + 13; //获取个人所有视频信息失败
	public static final int WM_GETSINGLEVIDEOCOMMENT_SUSS = WM_INIT_DATA + 14; //获取单个视频评论信息成功
	public static final int WM_GETSINGLEVIDEOCOMMENT_FAIL = WM_INIT_DATA + 15; //获取单个视频评论信息失败
	public static final int WM_DOWNLOADVIDEO_SUSS = WM_INIT_DATA + 16; //下载视频成功
	public static final int WM_DOWNLOADVIDEO_FAIL = WM_INIT_DATA + 17; //下载视频失败
//	public static final int WM_DOWNLOADTHUMBVIDEO_SUSS = WM_INIT_DATA + 18; //下载视频缩略图成功
	public static final int WM_DOWNLOADTHUMBVIDEO_FAIL = WM_INIT_DATA + 19; //下载视频缩略图失败
	public static final int WM_DOWNLOADHOMEPUSHVIDEO_SUSS = WM_INIT_DATA + 20;//获取主页推荐视频成功
	public static final int WM_DOWNLOADHOMEPUSHVIDEO_FAIL = WM_INIT_DATA + 21;//获取主页推荐视频失败
	public static final int WM_GETHISUSERINFO_SUSS = WM_INIT_DATA + 22;//获取他的个人信息成功
	public static final int WM_GETHISUSERINFO_FAIL = WM_INIT_DATA + 23;//获取他的个人信息失败
	public static final int WM_GETCOMMENTINFO_SUSS = WM_INIT_DATA + 24;//获取评论成功
	public static final int WM_GETCOMMENTINFO_FAIL = WM_INIT_DATA + 25;//获取评论失败
	public static final int WM_GETPRAISEINFOBYVID_SUSS = WM_INIT_DATA + 26;//获取单个视频赞
	public static final int WM_GETPRAISEINFOBYVID_FAIL = WM_INIT_DATA + 27;//获取单个视频赞
	public static final int WM_GETSQUAREINFO_SUSS = WM_INIT_DATA + 28;//获取广场数据成功
	public static final int WM_GETSQUAREINFO_FAIL = WM_INIT_DATA + 29;//获取广场数据失败
	public static final int WM_GETINCOMEINFO_SUSS = WM_INIT_DATA + 30;//获取收益数据成功
	public static final int WM_GETINCOMEINFO_FAIL = WM_INIT_DATA + 31;//获取收益数据失败
	public static final int WM_GETPRAISEINFO_SUSS = WM_INIT_DATA + 32;//取用户所有的赞列表成功
	public static final int WM_GETPRAISEINFO_FAIL = WM_INIT_DATA + 33;//取用户所有的赞列表失败
	public static final int WM_GETTRENDSINFO_SUSS = WM_INIT_DATA + 34;//取用户动态列表列表失败
	public static final int WM_GETTRENDSINFO_FAIL = WM_INIT_DATA + 35;//取用户动态列表列表失败
	public static final int WM_LOGINOUT_SUSS = WM_INIT_DATA + 36; //退出登陆成功
	public static final int WM_LOGINOUT_FAIL = WM_INIT_DATA + 37; //退出登陆失败
	public static final int WM_GETCITYWIDE_SUSS = WM_INIT_DATA + 38; //获取同城信息成功
	public static final int WM_GETCITYWIDE_FAIL = WM_INIT_DATA + 39; //获取同城信息失败
	public static final int WM_ADDFOLLOW_SUSS = WM_INIT_DATA + 40; //关注成功
	public static final int WM_ADDFOLLOW_FAIL = WM_INIT_DATA + 41; //关注失败
	public static final int WM_GETUSERINFO_SUSS = WM_INIT_DATA + 42;//获取用户信息成功
	public static final int WM_GETUSERINFO_FAIL = WM_INIT_DATA + 43;//获取用户信息失败 
	public static final int WM_UPLOAD_USERINFO_SUSS = WM_INIT_DATA + 44;//修改个人信息成功
	public static final int WM_UPLOAD_USERINFO_FAIL = WM_INIT_DATA + 45;//修改个人信息失败
	public static final int WM_GETLABLEVIDEO_SUSS = WM_INIT_DATA + 46;//获取广场标签视频成功
	public static final int WM_GETLABLEVIDEO_FAIL = WM_INIT_DATA + 47;//获取广场标签视频失败
	public static final int WM_GETSEARCHVIDEO_SUSS = WM_INIT_DATA + 48;//获取广场搜索视频成功
	public static final int WM_GETSEARCHVIDEO_FAIL = WM_INIT_DATA + 49;//获取广场搜索视频失败
	public static final int WM_DELETEFOLLOW_SUSS = WM_INIT_DATA + 50; //取消关注成功
	public static final int WM_DELETEFOLLOW_FAIL = WM_INIT_DATA + 51; //取消关注失败
	public static final int WM_ADDCOMMENT_SUSS = WM_INIT_DATA + 52;//添加评论成功
	public static final int WM_ADDCOMMENT_FAIL = WM_INIT_DATA + 53;//添加评论失败
	public static final int WM_GETSINGLEVIDEOINFO_SUSS = WM_INIT_DATA + 54;//取单个视频信息成功
	public static final int WM_GETSINGLEVIDEOINFO_FAIL = WM_INIT_DATA + 55;//取单个视频信息失败
	public static final int WM_GETVERIFICATIONCODE_SUSS = WM_INIT_DATA + 56;//获取验证码成功
	public static final int WM_GETVERIFICATIONCODE_FAIL = WM_INIT_DATA + 57;//获取验证码失败
	public static final int WM_BINDINGPHONE_SUSS = WM_INIT_DATA + 58;//绑定手机成功
	public static final int WM_BINDINGPHONE_FAIL = WM_INIT_DATA + 59;//绑定手机成功
	public static final int WM_UPDATEBINDING_SUSS = WM_INIT_DATA + 60;//更改绑定成功
	public static final int WM_UPDATEBINDING_FAIL = WM_INIT_DATA + 61;//更改绑定失败
	public static final int WM_ADDPRAISE_SUSS = WM_INIT_DATA + 62;//点赞成功
	public static final int WM_ADDPRAISE_FAIL = WM_INIT_DATA + 63;//点赞失败
	public static final int WM_RESET_PASSWORD_SUSS = WM_INIT_DATA + 64;//重置密码成功
	public static final int WM_RESET_PASSWORD_FAIL = WM_INIT_DATA + 65;//重置密码失败
	public static final int WM_FORGET_PASSWORD_SUSS = WM_INIT_DATA + 66;//忘记密码获取用户信息成功
	public static final int WM_FORGET_PASSWORD_FAIL = WM_INIT_DATA + 67;//忘记密码获取用户信息失败
	public static final int WM_VERIFICATIONCODE_SUSS = WM_INIT_DATA + 68;//验证手机号跟发过去的验证码成功
	public static final int WM_VERIFICATIONCODE_FAIL = WM_INIT_DATA + 69;//验证手机号跟发过去的验证码失败
	public static final int WM_UPDATEPASSWORD_SUSS = WM_INIT_DATA + 70;//修改密码成功(忘记密码)
	public static final int WM_UPDATEPASSWORD_FAIL = WM_INIT_DATA + 71;//修改密码失败(忘记密码)
	public static final int WM_EXCHANGE_SUSS = WM_INIT_DATA + 72;//兑换成功
	public static final int WM_EXCHANGE_FAIL = WM_INIT_DATA + 73;//兑换失败
	public static final int WM_GETINCOMEWEEK_SUSS = WM_INIT_DATA + 74;//获取一周收益成功
	public static final int WM_GETINCOMEWEEK_FAIL = WM_INIT_DATA + 75;//获取一周收益失败
	public static final int WM_GETPRAISEVIDEO_SUSS = WM_INIT_DATA + 76;//获取TA个人主页赞列表成功
	public static final int WM_GETPRAISEVIDEO_FAIL = WM_INIT_DATA + 77;//获取TA个人主页赞列表失败
	public static final int WM_GETADVERTISEMENT_SUSS = WM_INIT_DATA + 78;//获取广告数据成功
	public static final int WM_GETADVERTISEMENT_FAIL = WM_INIT_DATA + 79;//获取广告数据失败
	public static final int WM_ADDFEEDBACK_SUSS = WM_INIT_DATA + 80;//意见反馈成功
	public static final int WM_ADDFEEDBACK_FAIL = WM_INIT_DATA + 81;//意见反馈失败
	public static final int WM_DELETE_VIDEO_SUSS = WM_INIT_DATA + 82;//删除视频成功
	public static final int WM_DELETE_VIDEO_FAIL = WM_INIT_DATA + 83;//删除视频失败
	public static final int WM_DELETE_COMMENT_SUSS = WM_INIT_DATA + 84;//删除评论成功
	public static final int WM_DELETE_COMMENT_FAIL = WM_INIT_DATA + 85;//删除评论失败
	public static final int WM_GETUSERMSGSETTING_SUSS = WM_INIT_DATA + 86;//获取用户消息设置成功
	public static final int WM_GETUSERMSGSETTING_FAIL = WM_INIT_DATA + 87;//获取用户消息设置失败
	public static final int WM_ADDPRAISE_FULL = WM_INIT_DATA + 88;//点赞超过10个
	public static final int WM_GET_RESURL_SUSS = WM_INIT_DATA + 89;//获取资源URL成功
	public static final int WM_GET_RESURL_FAIL = WM_INIT_DATA + 90;//获取资源URL失败
	
	public static final int MSG_UPLOAD_VIDEO_SUSS = 128;// 视频上传成功
	public static final int MSG_UPLOAD_VIDEO_FAIL = 129;// 视频上传失败
	public static final int MSG_UPLOAD_VIDEO_TIPS = 130;// 视频上传进度信息
	public static final int MSG_MAP_LOCATION_CODE = 131;// 地理位置信息
	public static final int MSG_UPLOAD_IMAGE_TIPS = 132;// 图片上传进度信息
	public static final int MSG_UPLOAD_IMAGE_SUSS = 133;// 图片上传成功
	public static final int MSG_UPLOAD_IMAGE_FAIL = 134;// 图片上传失败
	public static final int MSG_UPLOAD_USERINFO_TIPS = 135;// 个人信息上传进度信息
	public static final int MSG_GETTAGS_SUSS = 136;// 获取热门标签成功
	public static final int MSG_GETTAGS_FAIL = 137;// 获取热门标签失败
	public static final int MSG_NICKNAME_EXISTS = 138;// 昵称重复
	public static final int MSG_OPEN_APP_SUSS = 139;// app与服务器连接成功
	public static final int MSG_OPEN_APP_FAIL = 140;// app与服务器连接失败
	public static final int MSG_REPORT_USER_SUSS = 141;// 举报用户成功
	public static final int MSG_REPORT_USER_FAIL = 142;// 举报用户失败
	
	public static final int G_NSHOWNUM = 20; //粉丝/关注/推荐 列表每次加载时显示的条数
	public static final int VIDEO_ITEM_COUNT = 10;// 每次加载视频数目>>如:每次加载10条视频(2.6版本的微视为10条)
	public static final int COMMENT_CODE = 1099;// 评论页返回code
	
	// C-S交互
	/**
	 * 是否开启启动页封面更新（true启用，反之false）
	 */
	public static final boolean enableLauncherCover = true;	
	//public final static String KTV_APP_SECRET_KEY = "192c96beaec59d367329c70016e7a50f";
	//public String KTV_APP_SECRET_KEY;
	
	/**
	 * 用于控制api版本
	 */
	public final static String KTV_API_VERSION = "&versioncode=1.5.0&client=android";//"&versioncode=1.0.1";
	
	/**
	 * 渠道，版本默认值
	 */
	public final static String PACKAGE_NAME = "tiange.sina.voice";
	public final static String MYSELF_CHANNEL_MAIN = "MYSELF_CHANNEL_MAIN";
	public final static String MYSELF_CHANNEL_CHILD = "MYSELF_CHANNEL_CHILD";
	
	public final static String KTV_VERSION = "1.5.2";
	public final static String KTV_QID1 = "9158";
	public final static String KTV_QID2 = "ANDROID";
	
	public final static String INSTALL_FILE_NAME = "statistics_install";
	public final static String[] INSTALL_PARAM_NAME = {"isFirst"};
	
	public static String VOICE_URL = "http://ktv.9158.com/";
	
	/**
	 * 91拍url
	 */
	public final static String DOMAIN_91PAI = "http://service.91pai.net/";
	public final static String SHARE_91PAI = "http://www.91pai.net/mobile/";// 分享url
	public final static String UPLOAD_91PAI = "http://upload.91pai.net/";
	public static final String HOST = "http://service.niupaisp.com/";

	
	public static String KtvAPIDomain = "http://ktvapi.9158.com/";
	public static String KtvListenDomain = "http://ktv.9158.com/";		//new formal Listen
	public static String KtvUpLoadDomain = "http://ktvupload.9158.com/";
	public static String KtvMatchDomain = "http://ktv.9158.com:88/";
	
	public static String Hao123Domain = "http://m.hao123.com/n/v/yinyue?tn=sinashow";
	
	
	/**
	 * 播放器与UI界面/播放Service交互广播
	 */
	public static final String PLAY_BROADCAST_ACTION_NAME = "tiange.sina.voice.MusicPlayerNotification";
	/**
	 * 个人主页封面头动画交互广播
	 */
	public static final String COVER_BROADCAST_ACTION_NAME = "tiange.sina.voice.CoverActionBroadcast";
	
	/**
	 * UI界面/后台Service交互广播
	 */
	public static final String WORK_BROADCAST_ACTION_NAME = "tiange.sina.voice.WorkServiceNotification";
	
	//Comment
	//发表评论
	public static String KtvAPICommentAddComment = "Comment/AddComment.ashx";
	//删除评论
	public static String KtvAPICommentDeleteComment = "Comment/DeleteComment.ashx";
	//删除评论回复
	public static String KtvAPICommentDeleteCommentReply = "Comment/DeleteCommentReply.ashx";
	//获取评论回复
	public static String KtvAPICommentGetCommentReply = "Comment/GetCommentReply.ashx";
	//获取作品评论
	public static String KtvAPICommentGetWorkComments = "Comment/GetWorkComments.ashx";
	//回复评论
	public static String KtvAPICommentReplyComment = "Comment/ReplyComment.ashx";

	//KTVRelated
	//点歌，最热歌曲榜
	public static String KtvAPIKTVRelatedGetHotSongs = "KTVRelated/GetHotSongs.ashx";
	//点歌，最新歌曲列表
	public static String KtvAPIKTVRelatedGetNewestSongs = "KTVRelated/GetNewestSongs.ashx";
	//点歌，歌星列表
	public static String KtvAPIKTVRelatedGetArtistsList = "KTVRelated/GetSingersList.ashx";
	//点歌，歌曲列表
	public static String KtvAPIKTVRelatedGetSongList = "KTVRelated/GetSongList.ashx";
	//播放歌曲，获取用户相册列表
	public static String KtvAPIKTVRelatedGetUserPhotos = "KTVRelated/GetUserPhotos.ashx";
	//演唱歌曲，获取评分排名
	public static String KtvAPIKTVRelatedGetWorkScoreRank = "KTVRelated/GetWorkScoreRank.ashx";
	//歌星点歌，根据歌星名称获取歌曲列表
	public static String KtvAPIKTVRelatedGetArtistSongList = "KTVRelated/GetArtistSongList.ashx";
	//最热K歌榜，获取点歌分类
	public static String KtvAPIKTVRelatedGetSongTypeList = "KTVRelated/GetSongTypeList.ashx";
	//最热K歌榜，根据歌曲类别获取歌曲列表
	public static String KtvAPIKTVRelatedGetTypeSongList = "KTVRelated/GetTypeSongList.ashx";
	//点歌，根据关键字获取歌曲列表
	public static String KtvAPIKTVRelatedSearchSongList = "KTVRelated/SearchSongList.ashx";
	//获取搜索热词列表
	public static String KtvAPIKTVRelatedGetHotKeywordList = "v13/vod/hotkeywords";
	//获取点歌台内容分类信息
	public static String KtvAPIKTVRelatedGetKtvContentInfo = "v13/Vod/Topics";
	//获取banner信息列表
	public static String KtvAPIKTVRelatedGetBannerInfoList = "v13/Vod/BannerSelect";
	//获取歌手分类
	public static String KtvAPIKTVRelatedGetSingerTopics = "v13/Singer/topics";
	//某个歌手分类的详细歌手信息
	public static String KtvAPIKTVRelatedSelectSingerByTopic = "v13/Singer/SelectByTopic";
	//精选专题
	public static String KtvAPIKTVRelatedGetSongTopics = "v13/Song/topics";
	//某个精选专题的详细歌曲信息
	public static String KtvAPIKTVRelatedSelectSongByTopic = "v13/Song/SelectByTopic";
	//热歌排行
	public static String KtvAPIKTVRelatedGetSongRanking = "v13/Song/ranking";
	//某个热歌排行的详细歌曲信息
	public static String KtvAPIKTVRelatedSelectSongByRanking = "v13/Song/SelectByRanking";
	
	//User
	//拉入黑名单
	public static String KtvAPIUserAddToBlackList = "User/AddToBlackList.ashx";
	//取消关注用户
	public static String KtvAPIUserCancelFollowUser = "User/CancelFollowUser.ashx";
	//删除用户照片
	public static String KtvAPIUserDeleteUserPhoto = "User/DeleteUserPhoto.ashx";
	//关注用户
	public static String KtvAPIUserFollowUser = "User/FollowUser.ashx";
	//获取粉丝列表
	public static String KtvAPIUserGetFansList = "User/GetFansList.ashx";
	//获取关注用户列表
	public static String KtvAPIUserGetFollowList = "User/GetFollowList.ashx";
	//获取剩余的鲜花数
	public static String KtvAPIUserGetLeftFlowerCount = "User/GetLeftFlowerCount.ashx";
	//获取消息列表
	public static String KtvAPIUserGetNotice = "User/GetNotice.ashx";
	//获取用户关注,粉丝数
	public static String KtvAPIUserGetRelationNum = "User/GetRelationNum.ashx";
	//获取用户成就
	public static String KtvAPIUserGetUserAchievement = "User/GetUserAchievement.ashx";
	//获取金币数
	public static String KtvAPIUserGetUserGold = "User/GetUserGold.ashx";
	//获取用户信息
	public static String KtvAPIUserGetUserInfo = "User/GetUserInfo.ashx";
	//获取用户关注状态
	public static String KtvAPIUserGetUserRelation = "User/GetUserRelation.ashx";
	//获取用户统计数据
	public static String KtvAPIUserGetUserStatistics = "User/GetUserStatistics.ashx";
	//获取用户头衔
	public static String KtvAPIUserGetUserTitles = "User/GetUserTitles.ashx";
	//用户登录
	public static String KtvAPIUserLogin = "User/login.ashx";
	//用户注销
	public static String KtvAPIUserLogout = "User/logout.ashx";
	//解除黑名单
	public static String KtvAPIUserRemoveFromBlackList = "User/RemoveFromBlackList.ashx";
	//举报用户(苹果要求功能)
	public static String KtvAPIUserReport = "User/Report.ashx";
	//搜素用户
	public static String KtvAPIUserSearchUsers = "User/SearchUsers.ashx";
	//送鲜花
	public static String KtvAPIUserSendItem = "User/SendItem.ashx";
	//设置头像
	public static String KtvAPIUserSetUserHead = "User/SetUserHead.ashx";
	//更新用户信息
	public static String KtvAPIUserUpdateUserInfo = "User/UpdateUserInfo.ashx";
	//更新用户头衔
	public static String KtvAPIUserUpdateUserTitles = "User/UpdateUserTitles.ashx";
	//上传头像,照片
	public static String KtvAPIUserUploadPhoto = "User/uploadPhoto.ashx";
	//获取指定用户列表的所有用户信息
	public static String KtvAPIUserGetUserInfoList = "User/getUserInfoList.ashx";
	//获得用户金币值(积分)(新接口不需要后缀)
	public static String KtvAPIUserGetUserCoins = "User/GetUserCoins";
	//兑换中心验证接口
	public static String KtvAPIUserAuthorizeLoginExchange = "Authorize/LoginExchange";
	//搜索用户推荐
	public static String KtvAPIUserSelectByRecommend = "v13/User/SelectByRecommend";
	//新的获取消息列表
	public static String KtvAPIGetNoticeByUser = "v13/message/select";
	//1.4版获取消息列表 20130704
	public static String KtvAPIGetUserCommentSelect = "v14/comment/select";
	//1.5版新用户体系登录接口20130813
	public static String KtvAPIUserLoginV15 = "V15/User/login";
	//1.5绑定账号
	public static String KtvAPIUserBindV15 = "v15/User/Bind";
	//1.5解除绑定账号
	public static String KtvAPIUserUnBindV15 = "v15/User/UnBind";
	//1.5获取收到鲜花记录
	public static String KtvAPIGetUserFlowerSelect = "V15/Gift/select";
	
	//photo 20130521
	//新的上传图片接口(initType可取值为1 相册封面、2 头像、3 作品封面，为3的时候workid放到initData里)
	public static String KtvAPIPhotoNewUploadPhoto = "v13/photo/upload";
	//设置个人主页相册封面
	public static String KtvAPIPhotoSetCover = "v13/userphoto/setcover";
	//设置个人头像
	public static String KtvAPIPhotoSetHeadPhoto = "v13/user/setheadphoto";
	//设置作品封面
	public static String KtvAPIPhotoSetWorkCover = "v13/workphoto/setcover";
	//获取作品相册
	public static String KtvAPIPhotoSelectByWork = "v13/workphoto/Select";
	//设置作品照片
	public static String KtvAPIPhotoSetWorkPhoto = "v13/workphoto/Set";
	//删除作品照片
	public static String KtvAPIPhotoDeleteWorkPhoto = "v13/workphoto/Delete";
	//新的获取用户相册
	public static String KtvAPIPhotoSelectByUser = "v13/userphoto/select";
	
	//Works
	//删除作品
	public static String KtvAPIWorksDeleteWork = "Works/DeleteWork.ashx";
	//瞩目新星列表
	public static String KtvAPIWorksGetHotestUsers = "Works/GetHotestUsers.ashx";
	//人气新作列表
	public static String KtvAPIWorksGetHotestWorks = "Works/GetHotestWorks.ashx";
	//最新上传列表
//	public static String KtvAPIWorksGetNewestWorks = "Works/GetNewestWorks.ashx";
	//1.4版好声音，作品推荐接口
	public static String KtvAPIWorksGetNewestWorks = "v14/workranking/recommend";
	//排行榜详细情况
	public static String KtvAPIWorksGetRankContent = "Works/GetRankContent.ashx";
	//排行榜列表
	public static String KtvAPIWorksGetRankList = "Works/GetRankList.ashx";
	//取得某用户作品列表
	public static String KtvAPIWorksGetUserWorks = "Works/GetUserWorks.ashx";
	//上传录音
	public static String KtvAPIWorksUploadWork = "V15/Works/UploadWork";//"Works/UploadWork.ashx";
	//k歌相关->userwork info
	public static String KtvAPIWorksGetWork = "Works/GetWork.ashx";
	//获取同一首歌的作品接口
	public static String KtvAPIWorksGetSongWorks = "Works/GetSongWorks.ashx";
	//更新作品分享新浪微博id
	public static String KtvAPIWorksUpdateWorkStatusId = "works/UpdateWorkStatusId.ashx";
	//更新作品分享腾讯微博id
	public static String KtvAPIWorksUpdateWorkTencentId = "/V15/Works/UpdateWorkTencentId";
	//分享作品
	public static String KtvAPIWorksShareWork = "Works/ShareWork.ashx";
	//增加听歌计数
	public static String KtvAPIWorksAddWorkListenNum = "Works/AddWorkListenNum.ashx";
	//收藏作品(新接口不需要后缀)
	public static String KtvAPIWorksCollectWork = "Works/CollectWork";
	//取消收藏
	public static String KtvAPIWorksCancelCollect = "Works/CancelCollect";
	//新的获取收藏作品列表
	public static String KtvAPIWorksSelectWorksByCollect = "v13/Work/SelectByCollect";
	//新的获取用户作品列表
	public static String KtvAPIWorksSelectWorksByUser = "v13/Work/SelectByUser";
	//新的被关注用户的作品列表(动态-主人模式)
	public static String KtvAPIWorksSelectWorksByAttentionUser = "v13/Work/SelectByAttentionUser";
	//动态信息（客户模式包含发布，收藏，分享，评论4类）
	public static String KtvAPIWorksSelectUserMessage = "v13/UserAction/Select";
	
	//statistics
	//安装数统计
	public static String KtvAPIStatisticsClientInstall = "Statistics/ClientInstall.ashx";
	//登陆次数统计
	public static String KtvAPIStatisticsClientLogin = "Statistics/ClientLogin.ashx";
	//在线时长统计
	public static String KtvAPIStatisticsClientOnlineTime = "Statistics/ClientOnlineTime.ashx";
	//手机唯一码
	public static String KtvAPIStatisticsClientRecord = "Statistics/ClientRecord.ashx";
	//用户点歌次数统计
	public static String KtvAPIStatisticsAddSongDownloadNum = "Statistics/AddSongDownloadNum.ashx";
	//用户K歌次数统计
	public static String KtvAPIStatisticsAddUserSingNum = "Statistics/AddUserSingNum.ashx";
	//用户演唱保存录音次数统计
	public static String KtvAPIStatisticsAddUserSaveRecordNum = "Statistics/AddUserSaveRecordNum.ashx";

	//push
	//获取系统推送消息
	public static String KtvAPIPushGetPushMessages = "Push/GetPushMessages.ashx";
	//获取启动页更新信息
	public static String KtvAPIPushGetStartPageInfo = "v13/run/StartupScreen";//v13
	//获取最新的一条活动
	public static String KtvAPIPushGetNewestActivity = "v13/activity/new";
	
	//活动比赛
	public static String KtvMatch = "Activity/Activity.aspx";
	
	//礼物兑换
	public static String ExchangeGift = "gift/index";
	
	//帮助中心
	public static String KtvSupportCenterDomain = "web/help/index";
	
	//应用推荐
	public static String recommendDomain = "web/app/recommend";
	
	//渠道，版本信息
//	public static String KtvVersion = "1.0";
//	public static String KtvQID1 = "9158";
//	public static String KtvQID2 = "TEST";
	
	// 返回码
	/**成功代码*/
	public final static String ErrorCodeSuccess = "00000:ok";
	/**失败代码*/
	public final static String ErrorCodeError = "00000:failed";
	/**歌词下载错误*/
	public final static int  ErrorCodeLrcFail = 500;
	
	/***********以下是与API服务端交互的错误*************/
	
	/**未知错误*/
	public final static int ErrorCodeSystem = 10000;
	/**参数传递错误*/
	public final static int ErrorCodeParamPostError = 10001;
	/**Secret错误*/
	public final static int ErrorCodeSecretError = 10002;
	/**任务过多,系统繁忙*/
	public final static int ErrorCodeServerBusy = 10003;
	/**任务超时*/
	public final static int ErrorCodeTimeOut = 10004;
	/**不合法的微博用户*/
	public final static int ErrorCodeInvalidUser = 10005;
	/**数据库错误,请联系系统管理员*/
	public final static int ErrorCodeDataBaseError = 10006;
	/**数据未找到*/
	public final static int ErrorCodeDataNotFound = 10007;
	/**没有操作权限*/
	public final static int ErrorCodeNoPermission = 10008;
	
	/**评论间隔过短*/
	public final static int ErrorCodeFrequentComment = 20001;
	/**重复评论*/
	public final static int ErrorCodeDuplicateComment  = 20002;
	
	//鲜花
	/**鲜花不足*/
	public final static int ErrorCodeSendFlowerLowLimit = 30001;
	/**送花超过上限*/
	public final static int ErrorCodeSendFlowerUpLimit = 30002;
	
	//用户资料修改
	
	/**昵称被占用*/
	public final static int ErrorCodeNickNameRepeat = 40001;

}
