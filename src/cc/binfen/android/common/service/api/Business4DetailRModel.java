package cc.binfen.android.common.service.api;

import java.io.Serializable;
import java.util.List;

/**
 * 用于接收服务器查询商铺返回的商户信息
 * revision: add Serializable by Michael
 * @author sunny
 *
 */
public class Business4DetailRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1898373305219149551L;
	
	//JSON Name Map to serverside
	public static String JSON_BID = "id";
	public static String JSON_BNAME = "name";
	public static String JSON_MAIN_PRODUCTS = "main_commodities";
	public static String JSON_SPECIALTY = "feature";
	public static String JSON_BUSINESS_HOURS = "openinghours";
	public static String JSON_PERSON_COST_AVG = "per_capita_spending";
	public static String JSON_METRO_STATION = "railway_station";
	public static String JSON_TELPHONE = "phone_number";
	public static String JSON_ADDRESS = "address";
	public static String JSON_PHOTO = "images";
	public static String JSON_NET_FRIEND_REFERRALS = "user_recommend";
	public static String JSON_GPS_LOCATION="gps_location";
	public static String JSON_LATITUDE = "lat";
	public static String JSON_LONGITUDE = "lng";
	public static String JSON_COMMENT_NUM = "comment_num";
	public static String JSON_PROMOTE_LIST = "privileges";
	public static String JSON_COMMENT_MODEL = "newest_comments";
	
	private String bid; 					//商铺id		business.id	Integer
	private String bName;				//商铺名称 	business.B_NAME	text
	private String mainProducts;		//主要商品	business.MAIN_PRODUCTS	text
	private String specialty;			//特色	business.SPECIALTY	text
	private String businessHours;		//营业时间	buseinsss.BUSINESS_HOURS	text
	private double personCostAvg;		//人均消费	business.PERSON_COST_AVG	text
	private String metroStation;		//地铁站 
	private String telphone;			//电话	business.TELPHONE	text
	private String address;				//地址	business.ADDRESS	text
	private String photo;				//商铺照片	business.PHOTO	text
	private String netFriendReferrals;	//网友推介	business.NET_FRIEND_REFERRALS	text
	private String latitude;			//商铺所在纬度
	private String longitude;			//商铺所在经度
	private int commentNum;				//商铺被点评总数
	private List<PromoteRModel> promoteCollection;	//该商户所享受的优惠集合
	private PromoteCommentRModel commentModel;	//该商户最新一条的点评记录
	
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getbName() {
		return bName;
	}
	public void setbName(String bName) {
		this.bName = bName;
	}
	public String getMainProducts() {
		return mainProducts;
	}
	public void setMainProducts(String mainProducts) {
		this.mainProducts = mainProducts;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getBusinessHours() {
		return businessHours;
	}
	public void setBusinessHours(String businessHours) {
		this.businessHours = businessHours;
	}
	public double getPersonCostAvg() {
		return personCostAvg;
	}
	public void setPersonCostAvg(double personCostAvg) {
		this.personCostAvg = personCostAvg;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getNetFriendReferrals() {
		return netFriendReferrals;
	}
	public void setNetFriendReferrals(String netFriendReferrals) {
		this.netFriendReferrals = netFriendReferrals;
	}
	public List<PromoteRModel> getPromoteCollection() {
		return promoteCollection;
	}
	public void setPromoteCollection(List<PromoteRModel> promoteCollection) {
		this.promoteCollection = promoteCollection;
	}
	public PromoteCommentRModel getCommentModel() {
		return commentModel;
	}
	public void setCommentModel(PromoteCommentRModel commentModel) {
		this.commentModel = commentModel;
	}
	public String getMetroStation() {
		return metroStation;
	}
	public void setMetroStation(String metroStation) {
		this.metroStation = metroStation;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
}
