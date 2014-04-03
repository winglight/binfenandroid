package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * 记录上传到服务器的商铺信息报错的信息
 * revision: add Serializable by Michael
 * @author sunny
 *
 */
public class BusinessMessageErrorRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5084366019387103888L;
	
	//JSON Name Map to serverside
	public static String JSON_BID = "";
	public static String JSON_ERROR_TYPE = "";
	public static String JSON_REMARK = "";
	public static String JSON_EMAIL = "";
	public static String JSON_CREATE_AT = "";
	public static String JSON_ANDROID_SID = "";
	public static String JSON_USER_EMAIL = "";
	
	//错误信息module
	private String bid;			//报错商铺id	ERROR_LOG.BID	integer
	private int errorType;		//报错类型	ERROR_LOG.ERROR_TYPE	integer
	private String remark;		//错误描述	ERROR_LOG.REMARK text
	private String email;		//报错回复的email ERROR_LOG.EMAIL	text
	private long createAt;		//报错时间	ERROR_LOG.CREATE_AT	long
	private String androidSid;  //android自带的id
	private String userEmail;   //用户email
	
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public int getErrorType() {
		return errorType;
	}
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getCreateAt() {
		return createAt;
	}
	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}
	public String getAndroidSid() {
		return androidSid;
	}
	public void setAndroidSid(String androidSid) {
		this.androidSid = androidSid;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
