package com.ztesoft.dubbo.mp.task.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;

@SuppressWarnings("serial")
@DBObj(tn = TenantInfo.TABLE_CODE)
public class TenantInfo extends VO {

	public static final IVOMeta META = IVOMeta.getInstance(TenantInfo.class);
	public static final String TABLE_CODE = "TENANT_INFO";
	public static final String PK_ID = "tenant_code";
	
	@DBField
	public String tenant_group_code;
	
	@DBField
	public String tenant_code;
	
	@DBField
	public String tenant_name;
	
	@DBField
	public String state;
	
	@DBField
	public String create_time;
	
	@DBField
	public String comments;
	
	@DBField
	public String state_time;
	
}
