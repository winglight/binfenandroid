package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * 记录上传到服务器的顶踩信息
 * revision: add Serializable by Michael
 * @author sunny
 *
 */
public class UpOrDownRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6175276321863200466L;
	
	//JSON Name Map to serverside
	public static String JSON_PID = "";
	public static String JSON_ACTION = "";
	public static String JSON_ACTIONTIME = "";
	public static String JSON_ANDROID_SID = "";
	public static String JSON_EMAILADDRESS = "";
	
	private String pid;		//优惠表id			UP_OR_DOWN.pid	integer
	private String action;	//动作：1.顶；2.踩	UP_OR_DOWN.action	text
	private long actionTime;//顶或踩的日期		UP_OR_DOWN.action_time	long
	private String  android_sid; 			 //android自带的id
	private String  emailAddress; 			 //用户E-mail
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getActionTime() {
		return actionTime;
	}
	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}
	public String getAndroid_sid() {
		return android_sid;
	}
	public void setAndroid_sid(String android_sid) {
		this.android_sid = android_sid;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
