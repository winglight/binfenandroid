package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.NewEvent4ListRModel;

/**
 * @author Michael
 */
public class NewEvent4ListRModelFunctions {
	public static List<NewEvent4ListRModel> getNewActivitysRModels(JSONArray jsonArrayNewActivitysRModels) throws JSONException {
		int n = jsonArrayNewActivitysRModels.length();
		List<NewEvent4ListRModel> reviews = new ArrayList();
		NewEvent4ListRModelBuilder reviewBuilder = new NewEvent4ListRModelBuilder();
		
		for(int i=0; i < n; i++){
			reviews.add(reviewBuilder.build(jsonArrayNewActivitysRModels.getJSONObject(i)));
		}
		
		return reviews;
	}
}
