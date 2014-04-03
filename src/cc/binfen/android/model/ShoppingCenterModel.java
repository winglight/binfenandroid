package cc.binfen.android.model;

/**
 * 购物中心model
 * @author vints
 *
 */
public class ShoppingCenterModel {
	//数据库字段
	private String id;		//购物中心id 	ID	Integer
	private String centerName;//购物中心名称	CENTERNAME	text
	private String streetNo;	//所属购物街id	STREET_NO Integer
	private String picUri;	//购物中心图片uri	PICURI text
	private String desc;	//购物中心介绍	DESC	text
	private String bannerPic;	//购物中心横幅广告	BANNER_PIC text
	private String promoteCounts; //优惠数	CENTER_PROMOTE_COUNT text
	
	public String getPromoteCounts() {
		return promoteCounts;
	}
	public void setPromoteCounts(String promoteCounts) {
		this.promoteCounts = promoteCounts;
	}
	public String getBannerPic() {
		return bannerPic;
	}
	public void setBannerPic(String bannerPic) {
		this.bannerPic = bannerPic;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getStreetNo() {
		return streetNo;
	}
	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}
	public String getPicUri() {
		return picUri;
	}
	public void setPicUri(String picUri) {
		this.picUri = picUri;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
