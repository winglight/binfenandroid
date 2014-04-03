package cc.binfen.android.common;
/**
 * 常量类
 *
 */
public class Constant {
	public static final String COMMA=","; //英文逗号
	public static final String COMMA_ZH="，"; //英文逗号
	public static final String DUNHAO="、";//中文顿号
	public static final String LINE_CODE="-";//英文横线分隔符
	public static final String JUHAO="。";//中文句号
	public static final String KUOHAO_LEFT="(";	//左括号
	public static final String KUOHAO_RIGHT=")";//右括号
	public static final String point=".";//英文.号
	
	public static final String IMG_DIR="/sdcard/binfen/img/";	//下载图片保存目录
	public static final String COMMENT_IMG_DIR = IMG_DIR+"comment/";	//点评图片目录
	public static final String COMMENT_UPLOADIMG_DEFAULT = COMMENT_IMG_DIR+"comment_default.jpeg"; //点评的默认图片
	
	public static final String PREF_PARA_SUFFIX="Para";//保存Screen参数到Preference的后缀
	
	public static final String PREF_PARA_FIRST_RUN = "firstrun";//App是否首次运行的标志在pref中的key
	
	public static final String PREF_PARA_SHOW_HELP = "showhelp";//是否运行时显示帮助页面-玩转icard，的标志在pref中的key
	
	public static final String INTENT_SHOWSPLASH = "isShowSplash";//是否运行时SplashScreen的intent参数key值
	public static final String INTENT_SHOWGUIDE = "isShowGuide";//是否运行时GuideScreen的intent参数key值
	
	public static final int ANIM_DURATION = 100;//duration of animation
	
	public static final String INIT_DBNAME="binfen";//初始化数据库文件的名字
	
	public static final Integer CHOOSE_LOCALPICTURE=1;	//选择本地图片
	
	public static final short NEARBY_DISTRICT = 0;	//地区
	public static final short NEARBY_METRO = 1;	//地铁
	
	public static final String SEARCH_TYPE_KEYWORD="KEYWORDS";	//关键字查找
	public static final String SEARCH_TYPE_FULLNAME="FULLNAME";	//完整名字查找

	public static final String SORT_TYPE_PRICE="price";	//按价格排序
	public static final String SORT_TYPE_STARS="stars";	//按星级排序
	public static final String SORT_TYPE_COMMENTS="comments";	//按点评数排序
	
	public static final double EARTH_RADIUS = 6378.137;//计算距离用的，地球半径
	public static final int[] sortTypeItemsKey={1};//热门卡页面排序类型选择框key
	public static final int[] distanceItemsKey={1,2,3};//距离选择框key
	public static final int[] errorTypeItemsKey={1,2,3,4,5};//信息报错类型key
	public static final String[] DISTANCE_TYPE={"500","1000","1500"};//距离类型
	//code table 类型常量
	public static final String code_effective_city="EFFECTIVE_CITY";//有效城市
	public static final String code_consume_type="CONSUME_TYPE";//消费类型
	public static final String code_card_business_type="CARD_BUSINESS_TYPE";//发卡商类型 
	public static final String code_advised_card="ADVISED_CARD";//推荐卡
	public static final String code_hot_card_card="HOT_CARD_SORT";//热门卡 
	
	
	public static final int TEXT_MAX = 140;	//评论内容最大字数
	
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";	//日期时间格式
	public static final String DATE_FORMAT = "yyyy.MM.dd";	//日期格式 
	public static final long DOUBLE_CLICK_TIME = 500; // 小于500毫秒的点击间隔将确定为一次双击
	public static final Integer PER_PAGE_NUM = 10;	//商户列表每页读取的条数
	
	public static final String DB_VERSION="DB_VERSION";//数据库版本在字典表中的key
	
}
