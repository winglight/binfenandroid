package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.UpOrDownRModel;

public class UpOrDownRModelFunctions {
	
	public static List<UpOrDownRModel> getUpOrDownRModel(JSONArray jsonArrayAlbums) throws JSONException {
		int n = jsonArrayAlbums.length();

		if( n < 1){
			throw new JSONException("No objects in array");
		}

		List<UpOrDownRModel> artists = new ArrayList();
		UpOrDownRModelBuilder artistBuilder = new UpOrDownRModelBuilder();
		
		for(int i=0; i < n; i++){
			artists.add(artistBuilder.build(jsonArrayAlbums.getJSONObject(i)));
		}
		
		return artists;
	}

}
