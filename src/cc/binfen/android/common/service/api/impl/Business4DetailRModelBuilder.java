package cc.binfen.android.common.service.api.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.binfen.android.common.Constant;
import cc.binfen.android.common.service.api.Business4DetailRModel;
import cc.binfen.android.common.service.api.PromoteCommentRModel;

public class Business4DetailRModelBuilder extends JSONBuilder<Business4DetailRModel> {

	@Override
	public Business4DetailRModel build(JSONObject jsonObject) throws JSONException {
		Business4DetailRModel business = new Business4DetailRModel();
		//商铺id
		business.setBid(jsonObject.getString(root+Business4DetailRModel.JSON_BID));
		//商铺名称
		business.setbName(jsonObject.getString(root+Business4DetailRModel.JSON_BNAME));
		//主要商品
		try{
			business.setMainProducts(jsonObject.getString(root+Business4DetailRModel.JSON_MAIN_PRODUCTS));
		}catch (Exception e) {
		}
		//特色
		try{
			business.setSpecialty(connectBusinessSpecialty(jsonObject.getJSONArray(root+Business4DetailRModel.JSON_SPECIALTY)));
		}catch (Exception e) {
			// TODO: handle exception
		}
		//营业时间
		business.setBusinessHours(jsonObject.getString(root+Business4DetailRModel.JSON_BUSINESS_HOURS));
		//人均消费
		business.setPersonCostAvg(jsonObject.getDouble(root+Business4DetailRModel.JSON_PERSON_COST_AVG));
		//地铁站
		try{
			business.setMetroStation(jsonObject.getString(root+Business4DetailRModel.JSON_METRO_STATION));
		}catch (Exception e) {
			// TODO: handle exception
		}
		//电话
		try{
		business.setTelphone(jsonObject.getString(root+Business4DetailRModel.JSON_TELPHONE));
		}catch (Exception e) {
			// TODO: handle exception
		}
		//地址
		business.setAddress(jsonObject.getString(root+Business4DetailRModel.JSON_ADDRESS));
		//商铺图片
		try{
			business.setPhoto(transPhotoJson(jsonObject.getJSONArray(root+Business4DetailRModel.JSON_PHOTO).getJSONObject(0)));
		}catch (Exception e) {
			// TODO: handle exception
		}
		//网友推介
		try{
		business.setNetFriendReferrals(jsonObject.getString(root+Business4DetailRModel.JSON_NET_FRIEND_REFERRALS));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		//商铺所在纬度
		try{
			JSONObject gpsLocation=jsonObject.getJSONObject(root+Business4DetailRModel.JSON_GPS_LOCATION);
			if(gpsLocation!=null && !"".equals(gpsLocation) ){
				business.setLatitude(gpsLocation.getDouble(root+Business4DetailRModel.JSON_LATITUDE)+"");
				business.setLongitude(gpsLocation.getDouble(root+Business4DetailRModel.JSON_LONGITUDE)+"");
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		//商铺被点评总数
		try{
		business.setCommentNum(jsonObject.getInt(root+Business4DetailRModel.JSON_COMMENT_NUM));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		//加入优惠list
		try{
		business.setPromoteCollection(PromoteRModelFunctions.getPlaylists(jsonObject.getJSONArray(root+Business4DetailRModel.JSON_PROMOTE_LIST)));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		//获取商铺最新的一条评论，如果有则加入商铺属性当中
		try{
		List<PromoteCommentRModel> promotes=PromoteCommentRModelFunctions.getPromoteCommentRModels(jsonObject.getJSONArray(root+Business4DetailRModel.JSON_COMMENT_MODEL));
		if(promotes!=null && promotes.size()>0){
			business.setCommentModel(promotes.get(0));
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return business;
	}
	
	//合并商铺的特色
	public String connectBusinessSpecialty(JSONArray array) throws JSONException{
		String businessSpecialty="";
		for (int i = 0; i < array.length(); i++) {
			JSONObject object=array.getJSONObject(i);
			businessSpecialty+="".equals(businessSpecialty) ? object.getString("name"):Constant.DUNHAO+object.getString("name");
		}
		return businessSpecialty;
	}
	
	private String transPhotoJson(JSONObject photo) throws JSONException{
		String photoUrl = "";
		photoUrl = photo.getString("url");
		return photoUrl;
	}

}
