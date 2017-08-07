package com.xiuxiu.util;

import com.xiuxiu.R;

import java.util.HashMap;

public class Expressions
{
	public static int[] expressionImgs = new int[] {
		R.drawable.emo_1, R.drawable.emo_2, R.drawable.emo_3, R.drawable.emo_4,
		R.drawable.emo_5, R.drawable.emo_6, R.drawable.emo_7, R.drawable.emo_8,
		R.drawable.emo_9, R.drawable.emo_10, R.drawable.emo_11, R.drawable.emo_12,
		R.drawable.emo_13, R.drawable.emo_14, R.drawable.emo_15, R.drawable.emo_16,
		R.drawable.emo_17, R.drawable.emo_18, R.drawable.emo_19, R.drawable.emo_20,
		R.drawable.emo_21, R.drawable.emo_22, R.drawable.emo_23, R.drawable.emo_24,
		R.drawable.emo_25, R.drawable.emo_26, R.drawable.emo_27, R.drawable.emo_28,
		R.drawable.emo_29, R.drawable.emo_30, R.drawable.emo_31, R.drawable.emo_32,
		R.drawable.emo_33, R.drawable.emo_34, R.drawable.emo_35, R.drawable.emo_36,
		R.drawable.emo_37, R.drawable.emo_38, R.drawable.emo_39, R.drawable.emo_40,
		R.drawable.emo_41, R.drawable.emo_42, R.drawable.emo_43, R.drawable.emo_44,
		R.drawable.emo_45, R.drawable.emo_46, R.drawable.emo_47, R.drawable.emo_48,
		R.drawable.emo_49, R.drawable.emo_50, R.drawable.emo_51, R.drawable.emo_52,
		R.drawable.emo_53, R.drawable.emo_54, R.drawable.emo_55, R.drawable.emo_56,
		R.drawable.emo_57, R.drawable.emo_58, R.drawable.emo_59, R.drawable.emo_60,
		R.drawable.emo_61, R.drawable.emo_62, R.drawable.emo_63, R.drawable.emo_64,
		R.drawable.emo_65, R.drawable.emo_66, R.drawable.emo_67, R.drawable.emo_68,
		R.drawable.emo_69, R.drawable.emo_70, R.drawable.emo_71, R.drawable.emo_72,
		R.drawable.emo_73, R.drawable.emo_74, R.drawable.emo_75, R.drawable.emo_76,
		R.drawable.emo_77, R.drawable.emo_78, R.drawable.emo_79, R.drawable.emo_80,
		R.drawable.emo_81, R.drawable.emo_82, R.drawable.emo_83, R.drawable.emo_84,
		R.drawable.emo_85, R.drawable.emo_86, R.drawable.emo_87, R.drawable.emo_88,
		R.drawable.emo_89};

	public static String[] expressionNames = new String[]{
		"兔子", "熊猫", "给力", "神马", "浮云", "织", "围观", "威武", "嘻嘻", "哈哈",
        "爱你", "晕", "泪", "馋嘴", "抓狂", "哼", "可爱", "怒", "汗", "呵呵",
        "睡觉", "钱", "偷笑", "酷", "衰", "吃惊", "闭嘴", "鄙视", "挖鼻屎", "花心",
        "鼓掌", "失望", "帅", "照相机", "落叶", "汽车", "飞机", "爱心传递", "奥特曼","实习",
        "思考", "生病", "亲亲", "怒骂", "太开心", "懒得理你", "右哼哼", "左哼哼", "嘘", "委屈",
        "吐", "可怜", "打哈气", "顶", "疑问", "做鬼脸", "害羞", "不要", "good", "弱",
        "ok", "赞", "来", "耶", "心", "伤心", "握手", "猪头", "咖啡", "话筒",
        "月亮", "太阳", "干杯", "萌", "礼物", "互粉", "蜡烛", "绿丝带", "沙尘暴","钟",
        "自行车", "蛋糕", "围脖", "手套", "雪", "雪人", "温暖帽子", "微风"
	};
	
	private static HashMap<String, Integer> faces;

	public static HashMap<String, Integer> getfaces()
	{
		if (faces != null) 
		{
			return faces;
		}
		
		faces = new HashMap<String, Integer>();
		int length = expressionNames.length;
		for(int i = 0; i < length; i++)
		{
			faces.put(expressionNames[i], expressionImgs[i]);
		}

		return faces;
	}
}
