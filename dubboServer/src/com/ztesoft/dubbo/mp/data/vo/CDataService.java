package com.ztesoft.dubbo.mp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;

@SuppressWarnings("serial")
@DBObj(tn = CDataService.TABLE_CODE)
public class CDataService extends VO {

	public static final IVOMeta META = IVOMeta.getInstance(CDataService.class);

	public static final String TABLE_CODE = "C_DATA_SERVICE";
	public static final String PK_ID = "SERVICE_ID";
	
	@DBField  @IDField
	public String service_id;
	
	@DBField
	public String service_name;
	
	@DBField
	public String state;
	
	@DBField
	public String catalog_id;
	
	@DBField
	public String operator;
	
	@DBField
	public String create_time;
	
	@DBField
	public String apply_count;
	
	@DBField
	public String dispatch_count;
	
	@DBField
	public String query_count;
	
}
