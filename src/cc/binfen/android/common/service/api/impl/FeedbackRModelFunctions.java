package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.FeedbackRModel;

/**
 * @author Michael
 */
public class FeedbackRModelFunctions {
	
	public static List<FeedbackRModel> getFeedbackRModels(JSONArray jsonArrayReviews) throws JSONException {
		int n = jsonArrayReviews.length();
		List<FeedbackRModel> radios = new ArrayList();
		FeedbackRModelBuilder radioBuilder = new FeedbackRModelBuilder();	
		
		// building radios
		for(int i=0; i < n; i++){ 
			radios.add(radioBuilder.build(jsonArrayReviews.getJSONObject(i)));
		}
		
		return radios;
	}
}
