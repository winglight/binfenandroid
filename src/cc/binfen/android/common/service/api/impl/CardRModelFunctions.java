package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.CardRModel;

public class CardRModelFunctions {
	public static List<CardRModel> getPlaylists(JSONArray jsonArrayReviews) throws JSONException {
		int n = jsonArrayReviews.length();
		List<CardRModel> playlists = new ArrayList();
		CardRModelBuilder playlistBuilder = new CardRModelBuilder();
		
		for(int i=0; i < n; i++){
			playlists.add(playlistBuilder.build(jsonArrayReviews.getJSONObject(i)));
		}
		
		return playlists;
	}
}
