package com.ztesoft.dubbo.mp.sys.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.IncreaseField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = QuartzScheduleJob.TABLE_CODE)
public class QuartzScheduleJob extends VO implements IVO {

	private static final long serialVersionUID = -1978980977830317967L;

	public static final IVOMeta META = IVOMeta.getInstance(QuartzScheduleJob.class);

	public static final String TABLE_CODE = "quartz_schedule_job";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField @IncreaseField
	public String job_id;

	@DBField
	public String job_name;

	@DBField
	public String job_group;

	@DBField
	public String job_class;

	@DBField
	public String method_name;

	@DBField
	public String job_status;

	@DBField
	public String cron_expression;

	@DBField
	public String is_concurrent;

	@DBField
	public String is_sync;

	@DBField
	public String spring_id;

	@DBField
	public String create_time;

	@DBField
	public String update_time;

	@DBField
	public String description;

	@DBField
	public String job_type;

	@DBField
	public String init_exec;

	@DBField
	public String sort;


}
