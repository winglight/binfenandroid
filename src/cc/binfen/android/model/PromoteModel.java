/**
 * 
 */
package cc.binfen.android.model;


/**
 * @author sunny
 *	用途：优惠model，记录优惠相关属性
 *	修改内容：
 */
public class PromoteModel {
	
	private String pid;				//优惠id		PROMOTES.id	integer
	private String bid;				//享受优惠店铺的id	PROMOTES.BID	integer
	private String discount;		//打折数		PROMOTES.DISCOUNT	text
	private String discountDes;		//打折描述	PROMOTES.DISCOUNT_DES	text
	private long disStartTime;		//打折起时间	PROMOTES.DIS_START_TIME	long
	private long disEndTime;		//打折迄时间	PROMOTES.DIS_END_TIME	long
	private int consumeType;		//消费类型	PROMOTES.CONSUME_TYPE	text
	private int upCount;			//顶的总数	PROMOTES.UP_COUNT	text
	private int downCount;			//踩的总数	PROMOTES.DOWN_COUNT	text
	private String[] cardsPhoto;	//享受优惠的卡的图片
	private String[] cardsName;		//享受优惠的卡名称
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getDiscountDes() {
		return discountDes;
	}
	public void setDiscountDes(String discountDes) {
		this.discountDes = discountDes;
	}
	public long getDisStartTime() {
		return disStartTime;
	}
	public void setDisStartTime(long disStartTime) {
		this.disStartTime = disStartTime;
	}
	public long getDisEndTime() {
		return disEndTime;
	}
	public void setDisEndTime(long disEndTime) {
		this.disEndTime = disEndTime;
	}
	public int getConsumeType() {
		return consumeType;
	}
	public void setConsumeType(int consumeType) {
		this.consumeType = consumeType;
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
}
