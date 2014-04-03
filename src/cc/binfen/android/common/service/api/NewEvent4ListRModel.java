package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * @author sunny
 * 最新活动信息model
 * revision: add Serializable by Michael
 */
public class NewEvent4ListRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4024610524024226724L;
	
	//JSON Name Map to serverside
	public static String JSON_EVENT_ID = "";
	public static String JSON_EVENT_PHOTO = "";
	
	private String eventId;		//活动id
	private String eventPhoto;	//最新活动信息图片
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getEventPhoto() {
		return eventPhoto;
	}
	public void setEventPhoto(String eventPhoto) {
		this.eventPhoto = eventPhoto;
	}
}
