package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;

@SuppressWarnings("serial")
@DBObj(tn = CDataColumn.TABLE_CODE)
public class CDataColumn extends VO {
	
	public static final IVOMeta META = IVOMeta.getInstance(CDataColumn.class);

	public static final String TABLE_CODE = "C_DATA_COLUMN";
	public static final String PK_ID = "COLUMN_ID";
	
	@DBField  @IDField
	public String column_id;
	
	@DBField
	public String service_id;
	
	@DBField
	public String column_code;
	
	@DBField
	public String column_name;
	
	@DBField
	public String comments;
	
	@DBField
	public String column_type;
	
	@DBField
	public String column_length;
	
	@DBField
	public String is_dst;
	
	@DBField
	public String dst_algorithm;
	
	@DBField
	public String src_column_id;
	
	@DBField
	public String is_acct;
	
	@DBField
	public String seq;
	
}
