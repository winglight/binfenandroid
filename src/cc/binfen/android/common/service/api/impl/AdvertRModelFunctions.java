package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.AdvertRModel;

public class AdvertRModelFunctions {

	public static List<AdvertRModel> getPlaylists(JSONArray jsonArrayReviews) throws JSONException {
		int n = jsonArrayReviews.length();
		List<AdvertRModel> playlists = new ArrayList();
		AdvertRModelBuilder playlistBuilder = new AdvertRModelBuilder();
		
		for(int i=0; i < n; i++){
			playlists.add(playlistBuilder.build(jsonArrayReviews.getJSONObject(i)));
		}
		
		return playlists;
	}

}
