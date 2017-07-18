package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MBpmWoTaskExec.TABLE_CODE)
public class MBpmWoTaskExec extends VO implements IVO {
    
    public static final IVOMeta META = IVOMeta.getInstance(MBpmWoTaskExec.class);
    public static final String TABLE_CODE = "BPM_WO_TASK_EXEC";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
    @DBField @IDField
    public String  task_exec_id;
    
    @DBField
    public String  wo_id;
    
    @DBField
    public String  task_worker;
    
    @DBField
    public String  worker_type;
    
    public String  worker_type_original; // 最开始的人员类型，来自bpm_wo_dispatch_rule表的dest_worker_type字段，某些情况下和worker_type不一致。 joshui
    
    @DBField(type=DBField.TYPE_DATE)
    public String  exec_date;
    
    @DBField
    public String exec_state;
    
    @DBField
    public String  wo_type_id;
    
    @DBField
    public String  work_type;
    
    @DBField
    public String  task_type;
    
    @DBField
    public String  task_title;
    
    @DBField
    public String  task_content;
    
    @DBField
    public String  tache_code;
    
    @DBField
    public String  tache_name;
    
    @DBField @RootField
    public String  flow_id;
    
    @DBField
    public String  bo_id;
    
    @DBField
    public String  bo_type_id;
    
    @DBField
    public String  dispatch_oper_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  dispatch_date;
    
    @DBField
    public String  exec_interval;
    
    @DBField
    public String  resp_content;
}
