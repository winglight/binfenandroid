/**
 * 
 */
package cc.binfen.android.model;

/**
 * @author sunny
 * 用途：收藏model，记录收藏相关属性
 * 修改内容：
 */
public class CollectModel {
	//收藏module
	private String id;				//收藏ID		COLLECT.ID text
	private String pid;				//收藏优惠id	COLLECT.PID	text
	private String type;			//类别	COLLECT.TYPE text "collect":收藏；"viewed":浏览
	private String businessId;		//商铺id collect.BUSINESS_ID text
	private String businessName;	//商铺名称	COLLECT.BUSINESS_NAME text
	private String businessDes;		//商铺描述	COLLECT.BUSINESS_DES text
	private double businessStars;	//商铺星级数 	COLLECT.BUSINESS_STARS double
	private String businessDiscount;	//商铺优惠数 COLLECT.BUSINESS_DISCOUNT text
	private String businessDisCardsName;	//商铺优惠卡名称	COLLECT.BUSINESS_DIS_CARDS_NAME text
	private long createAt;			//创建日期 COLLECT.CREATE_AT long
	
	public long getCreateAt() {
		return createAt;
	}
	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBusinessDiscount() {
		return businessDiscount;
	}
	public void setBusinessDiscount(String businessDiscount) {
		this.businessDiscount = businessDiscount;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getBusinessDes() {
		return businessDes;
	}
	public void setBusinessDes(String businessDes) {
		this.businessDes = businessDes;
	}
	public double getBusinessStars() {
		return businessStars;
	}
	public void setBusinessStars(double businessStars) {
		this.businessStars = businessStars;
	}
	public String getBusinessDisCardsName() {
		return businessDisCardsName;
	}
	public void setBusinessDisCardsName(String businessDisCardsName) {
		this.businessDisCardsName = businessDisCardsName;
	}
	
}
