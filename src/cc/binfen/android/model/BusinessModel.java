/**
 * 
 */
package cc.binfen.android.model;

import java.util.List;

/**
 * @author sunny
 *	用途：商铺详细信息model，记录商铺相关属性
 *	修改内容：
 */
public class BusinessModel {
	//商铺module
	private String bid; 					//商铺id		business.id	text
	private String bName;				//商铺名称 	business.B_NAME	text
	private String telphone;			//电话	business.TELPHONE	text
	private String address;				//地址	business.ADDRESS	text
	private String mainProducts;		//主要商品	business.MAIN_PRODUCTS	text
	private String describe;			//描述	business.B_DESCRIBE	text
	private String photo;				//商铺照片	business.PHOTO	text
	private String personCostAvg;		//人均消费	business.PERSON_COST_AVG	text
	private String metorStation;		//地铁站	
	private String businessHours;		//营业时间	buseinsss.BUSINESS_HOURS	text
	private String netFriendReferrals;	//网友推介	business.NET_FRIEND_REFERRALS	text
	private String specialty;			//特色	business.SPECIALTY	text
	private double longitude; 			//经度	business.LONGITUDE	double
	private double latitude;			//纬度	business.LATITUDE	double
	private int consumeType;			//消费类型	business.CONSUME_TYPE	integer
	private int price;					//价格(用于排序)	business.PRICE	integer
	private int stars;					//星级数(用于排序)	business.STARS	integer
	private int commemts;				//点评数(用于排序)	business.COMMENTS	integer
	private String discount;			//打折数(非DB字段，用于商铺列表显示打折起迄)
	private String disCardsName;		//参与打折的卡名称(非DB字段，用于商铺列表显示参与打折的卡的名称)
	private String distance;			//与用户之间的距离(非DB字段)
	private List<PromoteModel> promoteList;	//该商户所享受的优惠集合
	private List<PromoteCommentModel> commentList;	//该商户所有的点评集合
	
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
	public String getMainProducts() {
		return mainProducts;
	}
	public void setMainProducts(String mainProducts) {
		this.mainProducts = mainProducts;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getPersonCostAvg() {
		return personCostAvg;
	}
	public void setPersonCostAvg(String personCostAvg) {
		this.personCostAvg = personCostAvg;
	}
	public String getBusinessHours() {
		return businessHours;
	}
	public void setBusinessHours(String businessHours) {
		this.businessHours = businessHours;
	}
	public String getNetFriendReferrals() {
		return netFriendReferrals;
	}
	public void setNetFriendReferrals(String netFriendReferrals) {
		this.netFriendReferrals = netFriendReferrals;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getConsumeType() {
		return consumeType;
	}
	public void setConsumeType(int consumeType) {
		this.consumeType = consumeType;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getStars() {
		return stars;
	}
	public void setStars(int stars) {
		this.stars = stars;
	}
	public int getCommemts() {
		return commemts;
	}
	public void setCommemts(int commemts) {
		this.commemts = commemts;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getDisCardsName() {
		return disCardsName;
	}
	public void setDisCardsName(String disCardsName) {
		this.disCardsName = disCardsName;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public List<PromoteModel> getPromoteList() {
		return promoteList;
	}
	public void setPromoteList(List<PromoteModel> promoteList) {
		this.promoteList = promoteList;
	}
	public List<PromoteCommentModel> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<PromoteCommentModel> commentList) {
		this.commentList = commentList;
	}
	public String getMetorStation() {
		return metorStation;
	}
	public void setMetorStation(String metorStation) {
		this.metorStation = metorStation;
	}
}
