package cc.binfen.android.common.service.api.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.NewEvent4DetailRModel;

public class NewEvent4DetailRModelBuilder extends JSONBuilder<NewEvent4DetailRModel>{
	
	@Override
	public NewEvent4DetailRModel build(JSONObject jsonObject) throws JSONException {
		NewEvent4DetailRModel eventDetail = new NewEvent4DetailRModel();
		eventDetail.setEventId(jsonObject.getString(root+NewEvent4DetailRModel.JSON_EVENT_ID));
		eventDetail.setEventDetailPhoto(jsonObject.getString(root+NewEvent4DetailRModel.JSON_EVENT_DETAIL_PHOTO));
		//商铺基本信息
		JSONArray businessArray=jsonObject.getJSONArray(root+NewEvent4DetailRModel.JSON_BUSINESS_ARRAY);
		eventDetail.setBusinessArray(Business4ListRModelFunctions.getPlaylists(businessArray));
		return eventDetail;
	}
	
}
