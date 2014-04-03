package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * 用于接收服务器查询商铺返回的商户信息
 * revision: add Serializable by Michael
 * @author sunny
 *
 */
public class Business4ListRModel implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1898373305219149551L;
	
	//JSON Name Map to serverside
	public static String JSON_BID = "id";
	public static String JSON_BNAME = "name";
	public static String JSON_DESCRIBE = "introduction";
	public static String JSON_STARS = "shop_star";
	public static String JSON_DISCOUNT_MIN_VALUE = "min_value";
	public static String JSON_DISCOUNT_MAX_VALUE = "max_value";
	public static String JSON_CARD_MEMBER = "member_groups";
	public static String JSON_CARD_ID = "member_group_id";
	public static String JSON_CARD_NAME = "member_group_name";
	public static String JSON_DISTANCE = "distance";
	
	private String bid; 				//商铺id		business.id	Integer
	private String bName;				//商铺名称 	business.B_NAME	text
	private String describe;			//描述	business.B_DESCRIBE	text
	private double stars;				//星级数(用于排序)	business.STARS	integer
	private String discount;			//打折数(用于商铺列表显示打折起迄)
	private String disCardsName;		//参与打折的卡名称(用于商铺列表显示参与打折的卡的名称)
	private int distance;				//与用户之间的距离

	
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
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public double getStars() {
		return stars;
	}
	public void setStars(double stars) {
		this.stars = stars;
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
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
}
