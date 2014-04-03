package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * 用于接收服务器查询商铺返回的优惠信息
 * revision: add Serializable by sunny
 * @author sunny
 *
 */
public class PromoteRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5608573271733353051L;
	
	//JSON Name Map to serverside
	public static String JSON_PID = "id";
	public static String JSON_DISCOUNT_DESCRIBE = "description";
	public static String JSON_DISCOUNT_MIN_VALUE = "discount_min_value";
	public static String JSON_DISCOUNT_MAX_VALUE = "discount_max_value";
	public static String JSON_DISCOUNT_UNITS = "discount_units";
	public static String JSON_DISCOUNT_START_AT = "effective_since";
	public static String JSON_DISCOUNT_END_AT = "effective_until";
	public static String JSON_UP_COUNT = "top";
	public static String JSON_DOWN_COUNT = "step";
	public static String JSON_CARD_MEMBER = "member_groups";

	private String pid;					//优惠id		PROMOTES.id	integer
	private String discountDescribe;	//打折描述	PROMOTES.DISCOUNT_DES	text
	private double discountMinValue;	//打折最小数
	private double discountMaxValue;	//打折最大数
	private String discountUnits;		//打折单位
	private String discountStartAt;		//打折起时间	PROMOTES.DIS_START_TIME	text
	private String discountEndAt;		//打折迄时间	PROMOTES.DIS_END_TIME	text
	private int upCount;				//顶的总数	PROMOTES.UP_COUNT	text
	private int downCount;				//踩的总数	PROMOTES.DOWN_COUNT	text
	private String[] cardsId;			//享受优惠的卡的ID
	private String[] cardsPhoto;		//享受优惠的卡的图片
	private String[] cardsName;			//享受优惠的卡名称 
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getDiscountDescribe() {
		return discountDescribe;
	}
	public void setDiscountDescribe(String discountDescribe) {
		this.discountDescribe = discountDescribe;
	}
	public String getDiscountStartAt() {
		return discountStartAt;
	}
	public void setDiscountStartAt(String discountStartAt) {
		this.discountStartAt = discountStartAt;
	}
	public String getDiscountEndAt() {
		return discountEndAt;
	}
	public void setDiscountEndAt(String discountEndAt) {
		this.discountEndAt = discountEndAt;
	}
	public int getUpCount() {
		return upCount;
	}
	public void setUpCount(int upCount) {
		this.upCount = upCount;
	}
	public int getDownCount() {
		return downCount;
	}
	public void setDownCount(int downCount) {
		this.downCount = downCount;
	}
	public String[] getCardsPhoto() {
		return cardsPhoto;
	}
	public void setCardsPhoto(String[] cardsPhoto) {
		this.cardsPhoto = cardsPhoto;
	}
	public String[] getCardsName() {
		return cardsName;
	}
	public void setCardsName(String[] cardsName) {
		this.cardsName = cardsName;
	}
	public double getDiscountMinValue() {
		return discountMinValue;
	}
	public void setDiscountMinValue(double discountMinValue) {
		this.discountMinValue = discountMinValue;
	}
	public double getDiscountMaxValue() {
		return discountMaxValue;
	}
	public void setDiscountMaxValue(double discountMaxValue) {
		this.discountMaxValue = discountMaxValue;
	}
	public String getDiscountUnits() {
		return discountUnits;
	}
	public void setDiscountUnits(String discountUnits) {
		this.discountUnits = discountUnits;
	}
	public String[] getCardsId() {
		return cardsId;
	}
	public void setCardsId(String[] cardsId) {
		this.cardsId = cardsId;
	}
}
