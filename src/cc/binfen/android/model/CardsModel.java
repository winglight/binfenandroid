package cc.binfen.android.model;
/**
 * 
 * @author kandy
 * updatedate:2011-12-27
 * 用途：卡model
 *
 */
public class CardsModel {
	
	private String id ; //card id
	private String card_name; 	//卡名称
	private String photo;  		//卡的图像
	private String cb_name;		//发卡商图片名称   cards
	private String cb_photo; 	//发卡商图片
	private String cardType;	//卡类别
	private int promoteCount;	//卡的优惠数
	private int collectCount;	//卡的收藏数
	
	public String getCb_name() {
		return cb_name;
	}
	public void setCb_name(String cb_name) {
		this.cb_name = cb_name;
	}
	public String getCb_photo() {
		return cb_photo;
	}
	public void setCb_photo(String cb_photo) {
		this.cb_photo = cb_photo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCard_name() {
		return card_name;
	}
	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public int getPromoteCount() {
		return promoteCount;
	}
	public void setPromoteCount(int promoteCount) {
		this.promoteCount = promoteCount;
	}
	public int getCollectCount() {
		return collectCount;
	}
	public void setCollectCount(int collectCount) {
		this.collectCount = collectCount;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
} 