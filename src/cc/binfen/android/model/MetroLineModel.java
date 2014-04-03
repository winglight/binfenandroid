package cc.binfen.android.model;

/**
 * METROLINE model
 * @author vints
 *
 */
public class MetroLineModel {
	//数据库字段
	private String id;		//记录在表中的序号	ID	Integer
	private String lineNo;		//地铁线编号		LINENO	 Integer
	private String lineName;	//地铁线名称		LINENAME	text
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
}
