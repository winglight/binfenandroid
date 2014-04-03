package cc.binfen.android.model;
/**
 * 

* @author kandy
 * updatedate:2011-12-27
 * 用途：我的名片用到的model
 *
 */
public class MyBizCardModel {
	private String id; 
	private String  android_sid; 		//android自带的id  			USER_INFO.ANDROID_SID  text
	private String  soft_version;		//软件版本号  				    USER_INFO.SOFT_VERSION text
	private String nickname; 			//昵称  						 USER_INFO.NICKNAME  text
	private String email;           	//邮件                                                                                  USER_INFO.EMAIL     text
	private String cityid;				// 非数据库字段    用户所属城市id 
	private String cityName;			//非数据库字段     城市中文名称             					    
	private String cityPinYin;			//非数据库字段   城市英文名称  
	private String userpassward;		//用户密码                                                                                     USER_INFO.USERPASSWORD   text
	private String usercard_ids;		//存放用户卡id，格式为（XXX,YYY,ZZZ）               USER_INFO.USERCARD_IDS   text
	private String vipname;				//用户名                   					     USER_INFO.VIPNAME        text

	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityPinYin() {
		return cityPinYin;
	}
	public void setCityPinYin(String cityPinYin) {
		this.cityPinYin = cityPinYin;
	}
	
	public String getUserpassward() {
		return userpassward;
	}
	public void setUserpassward(String userpassward) {
		this.userpassward = userpassward;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	 
	public String getCityid() {
		return cityid;
	}
	public void setCityid(String cityid) {
		this.cityid = cityid;
	}
	public String getSoft_version() {
		return soft_version;
	}
	public void setSoft_version(String soft_version) {
		this.soft_version = soft_version;
	}
	public String getAndroid_sid() {
		return android_sid;
	}
	public void setAndroid_sid(String android_sid) {
		this.android_sid = android_sid;
	}
	public String getUsercard_ids() {
		return usercard_ids;
	}
	public void setUsercard_ids(String usercard_ids) {
		this.usercard_ids = usercard_ids;
	}
	public String getVipname() {
		return vipname;
	}
	public void setVipname(String vipname) {
		this.vipname = vipname;
	}
} 