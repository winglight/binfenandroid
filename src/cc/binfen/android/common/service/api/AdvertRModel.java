package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * 广告model
 * revision: add Serializable by sunny
 * @author sunny
 *
 */
public class AdvertRModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -21065435036592108L;
	
	//JSON Name Map to serverside
	public static String JSON_ADVERT_CONTENT = "";
	public static String JSON_ADVERT_LINK = ""; 

	private String advertContent;	//广告内容：文字或图片
	private String advertLink;		//广告链接，url
	
	public String getAdvertContent() {
		return advertContent;
	}
	public void setAdvertContent(String advertContent) {
		this.advertContent = advertContent;
	}
	public String getAdvertLink() {
		return advertLink;
	}
	public void setAdvertLink(String advertLink) {
		this.advertLink = advertLink;
	}
}
