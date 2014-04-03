package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.NewEvent4DetailRModel;

public class NewEvent4DetailRModelFunctions {
	
	public static List<NewEvent4DetailRModel> getNewActivitysRModels(JSONArray jsonArrayNewActivitysRModels) throws JSONException {
		int n = jsonArrayNewActivitysRModels.length();
		List<NewEvent4DetailRModel> reviews = new ArrayList();
		NewEvent4DetailRModelBuilder reviewBuilder = new NewEvent4DetailRModelBuilder();
		
		for(int i=0; i < n; i++){
			reviews.add(reviewBuilder.build(jsonArrayNewActivitysRModels.getJSONObject(i)));
		}
		
		return reviews;
	}
}
