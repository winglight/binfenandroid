package cc.binfen.android.model;
/**
 * 
 * @author Kandy
 * TABLE：MY_COMMENT
 * 用户的评论
 */
public class MyCommentModel {
	private String id;							//MY_COMMENT.ID 
	private String userId;					//MY_COMMENT.USERID 用户id
	private String promotedId;					//MY_COMMENT.PROMOTEID 优惠id
	private String commentContent;			//MY_COMMENT.COMMENTCONTENT 评论内容
	private int createTime;					//MY_COMMENT.CREATE_TIME 评论创建时间
	private int cardId;						//MY_COMMENT.CARD_ID  卡id
	private String pay;						//MY_COMMENT.PAY 总消费额
	private String save;					//MY_COMMENT.SAVE 打折后节省额度
	private String picName;					//MY_COMMENT.PICNAME 用户评论后牌照的 图片名称
	private String nickName;				//MY_COMMENT.NICKNAME 用户昵称
	private String picPath;					//MY_COMMENT.PICPATH 上传图片的路径
	
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
	public String getPromotedId() {
		return promotedId;
	}
	public void setPromotedId(String promotedId) {
		this.promotedId = promotedId;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public int getCreateTime() {
		return createTime;
	}
	public void setCreateTime(int createTime) {
		this.createTime = createTime;
	}
	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public String getPay() {
		return pay;
	}
	public void setPay(String pay) {
		this.pay = pay;
	}
	public String getSave() {
		return save;
	}
	public void setSave(String save) {
		this.save = save;
	}
	public String getPicName() {
		return picName;
	}
	public void setPicName(String picName) {
		this.picName = picName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
}
