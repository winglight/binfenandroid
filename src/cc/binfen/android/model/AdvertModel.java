package cc.binfen.android.model;
/**
 * 广告model
 * @author sunny
 *
 */
public class AdvertModel {
	
	private String id;				//ADVERT.ID
	private int advertPosition;		//ADVERT.ADVERT_POSITION INTEGER  广告位 
	private long startDate;			//ADVERT.START_DATE   开始日期
	private long endDate;			//ADVERT.END_DATE     结束日期
	private String advertText;		//ADVERT.ADVERT_TEXT  广告内容
	private String advertImage;		//ADVERT.ADVERT_IMAGE 广告图片
	private boolean allowClick;		//ADVERT.ALLOW_CLICK text 是否可以点击
	private int clickType;			//ADVERT.CLICK_TYPE 点击类型
	private String clickContent;	//ADVERT.CLICK_CONTENT 点击内容
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getAdvertPosition() {
		return advertPosition;
	}
	public void setAdvertPosition(int advertPosition) {
		this.advertPosition = advertPosition;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public String getAdvertText() {
		return advertText;
	}
	public void setAdvertText(String advertText) {
		this.advertText = advertText;
	}
	public String getAdvertImage() {
		return advertImage;
	}
	public void setAdvertImage(String advertImage) {
		this.advertImage = advertImage;
	}
	public boolean isAllowClick() {
		return allowClick;
	}
	public void setAllowClick(boolean allowClick) {
		this.allowClick = allowClick;
	}
	public int getClickType() {
		return clickType;
	}
	public void setClickType(int clickType) {
		this.clickType = clickType;
	}
	public String getClickContent() {
		return clickContent;
	}
	public void setClickContent(String clickContent) {
		this.clickContent = clickContent;
	}
}
