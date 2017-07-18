package com.ztesoft.dubbo.mp.task.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.impl.VO;


@SuppressWarnings("serial")
@DBObj(tn = CTaskService.TABLE_CODE)
public class CTaskService extends VO {
	
	public static final IVOMeta META = IVOMeta.getInstance(CTaskService.class);
	public static final String TABLE_CODE = "C_TASK_SERVICE";
	public static final String PK_ID = "SERVICE_ID";
	
	@DBField  @IDField
	public String service_id;
	
	@DBField
	public String task_name;
	
	@DBField
	public String task_code;
	
	@DBField
	public String engine_type;
	
	@DBField
	public String comments;
	
	@DBField
	public String instructions;
	
	@DBField
	public String state;
	
	@DBField
	public String apply_count;
	
	@DBField
	public String schedule_count;
	
	@DBField
	public String create_time;
	
	@DBField
	public String state_time;
	
}
