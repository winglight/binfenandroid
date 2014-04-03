package cc.binfen.android.common.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import cc.binfen.android.common.service.api.PromoteCommentRModel;

/**
 * @author Michael
 */
public class PromoteCommentRModelFunctions {
	public static List<PromoteCommentRModel> getPromoteCommentRModels(JSONArray jsonArrayPromoteCommentRModels) throws JSONException {
		int n = jsonArrayPromoteCommentRModels.length();
		List<PromoteCommentRModel> reviews = new ArrayList();
		PromoteCommentRModelBuilder reviewBuilder = new PromoteCommentRModelBuilder();
		
		for(int i=0; i < n; i++){
			reviews.add(reviewBuilder.build(jsonArrayPromoteCommentRModels.getJSONObject(i)));
		}
		
		return reviews;
	}
}
