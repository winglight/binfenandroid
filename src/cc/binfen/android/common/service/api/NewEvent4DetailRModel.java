package cc.binfen.android.common.service.api;

import java.io.Serializable;
import java.util.List;

public class NewEvent4DetailRModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1618164399838487406L;
	
	//JSON Name Map to serverside
	public static String JSON_EVENT_ID = "";
	public static String JSON_EVENT_DETAIL_PHOTO = "";
	public static String JSON_BUSINESS_ARRAY = "";
	
	private String eventId;				//活动id
	private String eventDetailPhoto;	//最新活动信息图片
	private List<Business4ListRModel> businessArray;//活动商品信息list

	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getEventDetailPhoto() {
		return eventDetailPhoto;
	}
	public void setEventDetailPhoto(String eventDetailPhoto) {
		this.eventDetailPhoto = eventDetailPhoto;
	}
	public List<Business4ListRModel> getBusinessArray() {
		return businessArray;
	}
	public void setBusinessArray(List<Business4ListRModel> businessArray) {
		this.businessArray = businessArray;
	}
}
