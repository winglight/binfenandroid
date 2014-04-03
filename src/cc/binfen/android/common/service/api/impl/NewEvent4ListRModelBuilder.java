package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.NewEvent4ListRModel;

/**
 * @author Michael
 */
public class NewEvent4ListRModelBuilder extends JSONBuilder<NewEvent4ListRModel>{

	@Override
	public NewEvent4ListRModel build(JSONObject jsonObject) throws JSONException {
		NewEvent4ListRModel event = new NewEvent4ListRModel();
		event.setEventId(jsonObject.getString(root+NewEvent4ListRModel.JSON_EVENT_ID));
		event.setEventPhoto(jsonObject.getString(root+NewEvent4ListRModel.JSON_EVENT_PHOTO));
		return event;
	}

}
