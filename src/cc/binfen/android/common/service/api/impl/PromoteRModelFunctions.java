package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.PromoteRModel;

public class PromoteRModelFunctions {
	
	public static List<PromoteRModel> getPlaylists(JSONArray jsonArrayReviews) throws JSONException {
		int n = jsonArrayReviews.length();
		List<PromoteRModel> playlists = new ArrayList();
		PromoteRModelBuilder playlistBuilder = new PromoteRModelBuilder();
		
		for(int i=0; i < n; i++){
			playlists.add(playlistBuilder.build(jsonArrayReviews.getJSONObject(i)));
		}
		
		return playlists;
	}
	
	//解析参与优惠的卡的id、图片、名称
	public static Map<String,String[]> getDiscountCardsMessage(JSONArray jsonArrayReviews) throws JSONException{
		Map<String,String[]> cardMap=new HashMap();
		//卡id集合
		String[] cardsId=new String[jsonArrayReviews.length()];
		//卡图片集合
		String[] cardsPhoto=new String[jsonArrayReviews.length()];
		//卡名称集合
		String[] cardsName=new String[jsonArrayReviews.length()];
		for (int i = 0; i < jsonArrayReviews.length(); i++) {
			JSONObject cardObject=jsonArrayReviews.getJSONObject(i);
			cardsId[i]=cardObject.getString("member_group_id");
			cardsPhoto[i]=cardObject.getString("image");
			cardsName[i]=cardObject.getString("member_group_name");
		}
		cardMap.put("CARD_IDS", cardsId);
		cardMap.put("CARD_PHOTOS", cardsPhoto);
		cardMap.put("CARD_NAMES", cardsName);
		return cardMap;
	}
}
