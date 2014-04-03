package cc.binfen.android.common.service.api;

import java.io.Serializable;
import java.util.List;

import cc.binfen.android.model.BusinessModel;

/**
 * @author sunny
 * 最新活动信息model
 * revision: add Serializable by Michael
 */
public class NewActivitysRModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4024610524024226724L;
	private String firstPhoto;			//最新活动信息图片1
	private String secondPhoto;			//最新活动信息图片2
	List<BusinessModel> businessList;	//活动所包含的商铺集合
	
	public String getFirstPhoto() {
		return firstPhoto;
	}
	public void setFirstPhoto(String firstPhoto) {
		this.firstPhoto = firstPhoto;
	}
	public String getSecondPhoto() {
		return secondPhoto;
	}
	public void setSecondPhoto(String secondPhoto) {
		this.secondPhoto = secondPhoto;
	}
	public List<BusinessModel> getBusinessList() {
		return businessList;
	}
	public void setBusinessList(List<BusinessModel> businessList) {
		this.businessList = businessList;
	}
}
