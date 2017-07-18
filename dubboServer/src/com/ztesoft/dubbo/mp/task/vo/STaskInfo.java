package com.ztesoft.dubbo.mp.task.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 任务实例信息
 *
 */
@DBObj(tn = STaskInfo.TABLE_CODE)
public class STaskInfo extends VO implements IVO {

	private static final long serialVersionUID = 3331213639824348905L;

	public static final IVOMeta META = IVOMeta.getInstance(STaskInfo.class);

	public static final String TABLE_CODE = "s_task_info";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String task_id;
	
	@DBField
	public String apply_id;
	
	@DBField
	public String task_code;
	
	@DBField
	public String task_name;
	
	@DBField
	public String task_desc;
	
	@DBField
	public String task_type;
	
	@DBField
	public String prior;
	
	@DBField
	public String run_class;
	
	@DBField
	public String input_path;
	
	@DBField
	public String out_path;
	
	@DBField
	public String run_param;
	
	@DBField
	public String run_command;
}
