package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.NewActivitysRModel;

/**
 * @author Michael
 */
public class NewActivitysRModelFunctions {
	public static List<NewActivitysRModel> getNewActivitysRModels(JSONArray jsonArrayNewActivitysRModels) throws JSONException {
		int n = jsonArrayNewActivitysRModels.length();
		List<NewActivitysRModel> reviews = new ArrayList();
		NewActivitysRModelBuilder reviewBuilder = new NewActivitysRModelBuilder();
		
		for(int i=0; i < n; i++){
			reviews.add(reviewBuilder.build(jsonArrayNewActivitysRModels.getJSONObject(i)));
		}
		
		return reviews;
	}
}
