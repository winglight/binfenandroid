package cc.binfen.android.model;
/**
 * 
 * @author Kandy
 * 用途：发卡商的model
 * updatedate:2011-12-27
 */
public class CardsBusinessModel {
	private String id;					 //发卡商id    cards.ID
	private String cb_name;			 //发卡商图片名称   cards
	private String type_id;				 //发卡商的类型id
	private String type_name;			//非数据库字段  发卡商类型名称 
	private String photo; 			//发卡商图片
	private String  isDeleted;	    //非数据库字段  用来处理是否已经存入钱包卡的状态
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCb_name() {
		return cb_name;
	}
	public void setCb_name(String cb_name) {
		this.cb_name = cb_name;
	}
	public String getType_id() {
		return type_id;
	}
	public void setType_id(String type_id) {
		this.type_id = type_id;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}
	 

}
