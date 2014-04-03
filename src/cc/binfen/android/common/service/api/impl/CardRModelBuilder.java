package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.CardRModel;

public class CardRModelBuilder extends JSONBuilder<CardRModel>{

	@Override
	public CardRModel build(JSONObject jsonObject) throws JSONException {
		CardRModel card = new CardRModel();
		card.setCardId(jsonObject.getString(root+CardRModel.JSON_CARD_ID));
		card.setCardName(jsonObject.getString(root+CardRModel.JSON_CARD_PHOTO));
		card.setCardPhoto(jsonObject.getString(root+CardRModel.JSON_CARD_NAME));
		return card;
	}

}
