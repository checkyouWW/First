package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;

@SuppressWarnings("serial")
@DBObj(tn = CDataColumnWhere.TABLE_CODE)
public class CDataColumnWhere extends VO{

	public static final IVOMeta META = IVOMeta.getInstance(CDataColumnWhere.class);

	public static final String TABLE_CODE = "c_data_column_where";
	public static final String PK_ID = "where_id";
	
	@DBField  @IDField  @IncreaseField
	public String where_id;
	
	@DBField
	public String column_id;
	
	@DBField
	public String column_code;
	
	@DBField
	public String service_id;
	
	@DBField
	public String ability_id;
	
	@DBField
	public String expression;
	
	@DBField
	public String create_new_partition;
	
	@DBField
	public String state;
	
	@DBField
	public String column_type;
	
	@DBField
	public String new_partition_code;
	
	@DBField
	public String where_type;
}
