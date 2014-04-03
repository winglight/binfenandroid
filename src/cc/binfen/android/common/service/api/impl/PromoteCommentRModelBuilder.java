package cc.binfen.android.common.service.api.impl;

import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.service.api.PromoteCommentRModel;

/**
 * @author Michael
 */
public class PromoteCommentRModelBuilder extends JSONBuilder<PromoteCommentRModel>{

	@Override
	public PromoteCommentRModel build(JSONObject jsonObject) throws JSONException {
		PromoteCommentRModel comment = new PromoteCommentRModel();
		//点评用户id
		comment.setUserId(jsonObject.getString(root+PromoteCommentRModel.JSON_USER_ID));
		//点评用户名称
		comment.setUserName(jsonObject.getString(root+PromoteCommentRModel.JSON_USER_NAME));
		//点评图片
		comment.setCommentPicture(jsonObject.getString(root+PromoteCommentRModel.JSON_COMMENT_PICTURE));
		//点评内容
		comment.setContent(jsonObject.getString(root+PromoteCommentRModel.JSON_CONTENT));
		//点评时间
		comment.setCommentTime(jsonObject.getString(root+PromoteCommentRModel.JSON_COMMENT_TIME));
		//点评所用的卡id
		comment.setCardId(jsonObject.getString(root+PromoteCommentRModel.JSON_CARD_ID));
		//点评所用的卡名称
		comment.setCardName(jsonObject.getString(root+PromoteCommentRModel.JSON_CARD_NAME));
		//花费
		comment.setConsume(jsonObject.getDouble(root+PromoteCommentRModel.JSON_CONSUME));
		
		return comment;
	}

}
