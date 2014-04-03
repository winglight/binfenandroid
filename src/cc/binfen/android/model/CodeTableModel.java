package cc.binfen.android.model;
/**
 * 数据字典表model
 * @author sunny
 *
 */
public class CodeTableModel {
	private String id;					//id
	private String codeType;		//CODE_TABLE.CODE_TYPE 编码类型
	private String codeName;		//CODE_TABLE.CODE_NAME 编码名称
	private String codeValue;		//CODE_TABLE.CODE_VALUE 编码值
	private String extraValue1;		//CODE_TABLE.EXTRA_VALUE1 额外值1
	private String extraValue2;		//CODE_TABLE.EXTRA_VALUE2 额外值2
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	public String getExtraValue1() {
		return extraValue1;
	}
	public void setExtraValue1(String extraValue1) {
		this.extraValue1 = extraValue1;
	}
	public String getExtraValue2() {
		return extraValue2;
	}
	public void setExtraValue2(String extraValue2) {
		this.extraValue2 = extraValue2;
	}
}
