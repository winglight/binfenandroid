package cc.binfen.android.common.service.api;

import java.io.Serializable;

public class FeedbackRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6339505331971164330L;
	public static String JSON_ANDROID_SID = ""; 
	public static String JSON_ANDROID_VIPNAME="";
	public static String JSON_ANDROID_EMAIL="";
	public static String JSON_ANDROID_FEEDBACKCONTENT="";
	
	/**
	 * 用户---->反馈
	 */
	private int fid; 					//id
	private String  android_sid; 		//android自带的id 
	private String vipname;			//姓名 
	private String email;				//邮件
	private String feedbackContent;		//回复内容
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public String getAndroid_sid() {
		return android_sid;
	}
	public void setAndroid_sid(String android_sid) {
		this.android_sid = android_sid;
	}
	 
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFeedbackContent() {
		return feedbackContent;
	}
	public void setFeedbackContent(String feedbackContent) {
		this.feedbackContent = feedbackContent;
	}
	public String getVipname() {
		return vipname;
	}
	public void setVipname(String vipname) {
		this.vipname = vipname;
	}

}
