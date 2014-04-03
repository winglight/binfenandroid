package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.BusinessMessageErrorRModel;

public class BusinessMessageErrorRModelFunctions {
	
	public static List<BusinessMessageErrorRModel> getBusinessMessageErrorRModel(JSONArray jsonArrayAlbums) throws JSONException {
		int n = jsonArrayAlbums.length();

		if( n < 1){
			throw new JSONException("No objects in array");
		}

		List<BusinessMessageErrorRModel> artists = new ArrayList();
		BusinessMessageErrorRModelBuilder artistBuilder = new BusinessMessageErrorRModelBuilder();
		
		for(int i=0; i < n; i++){
			artists.add(artistBuilder.build(jsonArrayAlbums.getJSONObject(i)));
		}
		
		return artists;
	}

}
