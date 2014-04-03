package cc.binfen.android.common.service.api;

/**
 * 用户数据model
 * @author sunny
 *
 */
public class UserRModel {
	//JSON Name Map to serverside
	public static String JSON_ID = "id";
	public static String JSON_ANDROID_SID = "androidSid";
	public static String JSON_SOFT_VERSION = "softVersion";
	public static String JSON_NICKNAME = "nickname";
	public static String JSON_EMAIL = "email";
	public static String JSON_CITY = "city";
	public static String JSON_USER_PASSWARD = "userPassward";
	public static String JSON_USER_CARDIDS = "userCardIds";
	
	private int id; 
	private String  androidSid; 		//android自带的id 
	private String  softVersion;		//软件版本号
	private String nickname; 			//昵称
	private String email;           	//邮件
	private String city;				//用户所属城市
	private String userPassward;		//用户密码
	private String userCardIds;			//USER_INFO.USERCARD_IDS
	private String vipname;				
	
	public String getVipname() {
		return vipname;
	}
	public void setVipname(String vipname) {
		this.vipname = vipname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAndroidSid() {
		return androidSid;
	}
	public void setAndroidSid(String androidSid) {
		this.androidSid = androidSid;
	}
	public String getSoftVersion() {
		return softVersion;
	}
	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getUserPassward() {
		return userPassward;
	}
	public void setUserPassward(String userPassward) {
		this.userPassward = userPassward;
	}
	public String getUserCardIds() {
		return userCardIds;
	}
	public void setUserCardIds(String userCardIds) {
		this.userCardIds = userCardIds;
	}
}
