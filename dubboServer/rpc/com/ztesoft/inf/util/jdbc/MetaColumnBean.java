package com.ztesoft.inf.util.jdbc;

/**
 * @author JM.Wong
 * @Date 2016年10月27日 下午6:13:32 
 * @version V1.0
 * @Description 
 */
public class MetaColumnBean {

	private String columnCode;//字段名称
	private String columnType;//字段类型
	private String columnSeq;//字段顺序
	private String columnLength;//字段长度
	
	public String getColumnCode() {
		return columnCode;
	}
	public void setColumnCode(String columnCode) {
		this.columnCode = columnCode;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public String getColumnSeq() {
		return columnSeq;
	}
	public void setColumnSeq(String columnSeq) {
		this.columnSeq = columnSeq;
	}
	public String getColumnLength() {
		return columnLength;
	}
	public void setColumnLength(String columnLength) {
		this.columnLength = columnLength;
	}
	
}
