package cc.binfen.android.common.service.api;

import java.io.Serializable;

public class CardRModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7793406781454567751L;
	
	//JSON Name Map to serverside
	public static String JSON_CARD_ID = "";
	public static String JSON_CARD_PHOTO = "";
	public static String JSON_CARD_NAME = "";
	
	private String cardId;
	private String cardPhoto;
	private String cardName;
	
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getCardPhoto() {
		return cardPhoto;
	}
	public void setCardPhoto(String cardPhoto) {
		this.cardPhoto = cardPhoto;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
}
