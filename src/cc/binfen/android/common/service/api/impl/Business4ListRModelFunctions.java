package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.Business4ListRModel;

/**
 * @author Lukasz Wisniewski
 */
public class Business4ListRModelFunctions {
	
	public static List<Business4ListRModel> getPlaylists(JSONArray jsonArrayReviews) throws JSONException {
		int n = jsonArrayReviews.length();
		List<Business4ListRModel> playlists = new ArrayList();
		Business4ListRModelBuilder playlistBuilder = new Business4ListRModelBuilder();
		
		for(int i=0; i < n; i++){
			playlists.add(playlistBuilder.build(jsonArrayReviews.getJSONObject(i)));
		}
		
		return playlists;
	}
}
