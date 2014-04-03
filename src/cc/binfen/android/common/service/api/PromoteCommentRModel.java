package cc.binfen.android.common.service.api;

import java.io.Serializable;

/**
 * 优惠点评的远程Model
 * revision: add Serializable by Michael
 * @author vints
 *
 */
public class PromoteCommentRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7682103982692998248L;
	
	//JSON Name Map to serverside
	public static String JSON_USER_ID = "user_id";
	public static String JSON_USER_NAME = "user_name";
	public static String JSON_COMMENT_PICTURE = "file";
	public static String JSON_CONTENT = "content";
	public static String JSON_COMMENT_TIME = "time";
	public static String JSON_CARD_ID = "card_id";
	public static String JSON_CARD_NAME = "card_name";
	public static String JSON_CONSUME = "consume";
	
	private String userId;
	private String userName;
	private String commentPicture;
	private String content;
	private String commentTime;
	private String cardId;
	private String cardName;
	private double consume;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCommentPicture() {
		return commentPicture;
	}
	public void setCommentPicture(String commentPicture) {
		this.commentPicture = commentPicture;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCommentTime() {
		return commentTime;
	}
	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public double getConsume() {
		return consume;
	}
	public void setConsume(double consume) {
		this.consume = consume;
	}
}
