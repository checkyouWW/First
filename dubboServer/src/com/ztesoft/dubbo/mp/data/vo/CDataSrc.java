package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;

@SuppressWarnings("serial")
@DBObj(tn = CDataSrc.TABLE_CODE)
public class CDataSrc extends VO {

	public static final IVOMeta META = IVOMeta.getInstance(CDataSrc.class);

	public static final String TABLE_CODE = "C_DATA_SRC";
	public static final String PK_ID = "SRC_ID";
	
	@DBField  @IDField
	public String src_id;
	
	@DBField 
	public String service_id;
	
	@DBField 
	public String src_sys_code;
	
	@DBField 
	public String src_schema_code;
	
	@DBField 
	public String src_table_code;
	
	@DBField 
	public String extract_freq;
	
	@DBField 
	public String is_hist_extract;
	
	@DBField 
	public String extract_start_acct;
	
	@DBField 
	public String select_table_type;
	
}
