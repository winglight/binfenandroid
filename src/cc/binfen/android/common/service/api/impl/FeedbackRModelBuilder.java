package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.FeedbackRModel;

/**
 * @author kandy
 */
public class FeedbackRModelBuilder extends JSONBuilder<FeedbackRModel> {

	@Override
	public FeedbackRModel build(JSONObject jsonObject) throws JSONException {
		FeedbackRModel feedback = new FeedbackRModel();
		feedback.setAndroid_sid(jsonObject.getString(root+FeedbackRModel.JSON_ANDROID_SID));
		feedback.setVipname(jsonObject.getString(root+FeedbackRModel.JSON_ANDROID_VIPNAME));
		feedback.setEmail(jsonObject.getString(root+FeedbackRModel.JSON_ANDROID_EMAIL)); 
		return feedback;
	}

}
