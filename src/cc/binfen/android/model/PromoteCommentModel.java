package cc.binfen.android.model;

/**
 * 优惠点评model
 * @author vints
 *
 */
public class PromoteCommentModel {
	//数据库字段
	private String id;		//点评信息序号	ID	INTEGER
	private String userId;		//用户id		USERID	text
	private String promoteId;	//优惠id		PROMOTEID	INTEGER
	private String content;		//点评内容	COMMENTCONTENT	text
	private Long createTime;	//点评时间	CREATETIME	date
	private String cardId;		//卡id		CARDID		Integer
	private String pay;		//花费的钱		PAY			text
	private String picPath;		//图片路径	PICPATH		text
	private String picName;		//图片名		PICNAME		text
	private String saveMoney;	//节省的钱	SAVE		text
	private String serverId;	//本点评在服务器端数据库的id	SERVERID	text
	private String nickName;	//昵称		NICKNAME	text
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPromoteId() {
		return promoteId;
	}
	public void setPromoteId(String promoteId) {
		this.promoteId = promoteId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getPay() {
		return pay;
	}
	public void setPay(String pay) {
		this.pay = pay;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	public String getPicName() {
		return picName;
	}
	public void setPicName(String picName) {
		this.picName = picName;
	}
	public String getSaveMoney() {
		return saveMoney;
	}
	public void setSaveMoney(String saveMoney) {
		this.saveMoney = saveMoney;
	}
}
