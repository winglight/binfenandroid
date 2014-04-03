package cc.binfen.android.common.service.api;

import java.io.Serializable;
import java.util.List;

/**
 * TABLE　NAME :USER_INFO 
 * 用于传递 用户资料（昵名、email、城市、版本号、地理位置（所属城市）、我的卡）到服务器
 * revision: add Serializable by Michael
 * @author kandy
 *
 */
public class AccountInfoRModel implements Serializable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -2661109752701429420L;
	
	//JSON Name Map to serverside
	public static String JSON_ANDROID_SID = ""; 
	public static String JSON_SOFT_VERSION = "";
	public static String JSON_NICKNAME = "";
	public static String JSON_EMAIL = "";
	public static String JSON_CITY = "";
	public static String JSON_USERCARDIDLIST = "";
	
	private String  android_sid; 			 //android自带的id
	private String  soft_version;			 //软件版本号
	private String nickname; 			 //昵称
	private String  emailAddress; 			 //用户E-mail
	private String city;	        	 //用户所属城市
	private List<String> userCardIdList; //我的卡的ID的集合
	
	
	public String getAndroid_sid() {
		return android_sid;
	}
	public void setAndroid_sid(String android_sid) {
		this.android_sid = android_sid;
	}
	public String getSoft_version() {
		return soft_version;
	}
	public void setSoft_version(String soft_version) {
		this.soft_version = soft_version;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public List<String> getUserCardIdList() {
		return userCardIdList;
	}
	public void setUserCardIdList(List<String> userCardIdList) {
		this.userCardIdList = userCardIdList;
	} 
	
}
