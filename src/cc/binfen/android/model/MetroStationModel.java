package cc.binfen.android.model;

/**
 * METROSTATION TABLE
 * @author vints
 *
 */
public class MetroStationModel {
	//数据库字段
	private String id;		//地铁站id		ID		text
	private String oId;		//地铁站查找id	OID		text
	private String name;	//地铁站名称		NAME   	text
	private String lineNo;		//地铁站所在线路		LINENO	Integer
	private Integer position;	//地铁站在该线路对应的编号	POSITION	Integer
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getoId() {
		return oId;
	}
	public void setoId(String oId) {
		this.oId = oId;
	}
}
