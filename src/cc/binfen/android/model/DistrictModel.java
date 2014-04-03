package cc.binfen.android.model;

/**
 * District table
 * @author vints
 *
 */
public class DistrictModel {
	//数据库字段
	private String id;		//地区id		ID  Integer
	private String parentId;	//所属上一级地区id	parentId Integer
	private String district_name;	//名称	dis_name text
	/* 0国家 1省 2市 3区县 4地区 */
	private Integer level;	//地区所属层级    level  Integer
	private String promoteCount; //优惠数	DIS_PROMOTE_COUNT Integer
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getDistrict_name() {
		return district_name;
	}
	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getPromoteCount() {
		return promoteCount;
	}
	public void setPromoteCount(String promoteCount) {
		this.promoteCount = promoteCount;
	}
	
}
