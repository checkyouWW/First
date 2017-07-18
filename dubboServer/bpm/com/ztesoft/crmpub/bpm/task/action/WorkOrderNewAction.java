package com.ztesoft.crmpub.bpm.task.action;

import appfrm.app.util.StrUtil;
import appfrm.resource.dao.impl.DaoUtils;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;


/**
 * 目前应该没使用到 joshui
 * @author major
 *
 */
@Deprecated
public class WorkOrderNewAction extends WorkOrderBaseAction  implements IWorkOrderAction{
	
	/**
	 * 生成环节对应的所有工单
	 */
	public boolean execute() throws Exception {
		String woTypeId = msg.getWoTypeId();//业务单类型ID
		if(StrUtil.isEmpty(woTypeId)){
			throw new RuntimeException("无法创建工单,原因：传入的boTypeId或woTypeId为空");
		}

		// 1. 加载环节规格数据
		SBpmBoFlowTache flowTache = msg.getFlowTache();
		
		String curDbTime = DaoUtils.getDBCurrentTime();

		MBpmWoTask woTask = new MBpmWoTask();
		woTask.wo_type_id = woTypeId;
		woTask.work_type = flowTache.work_type;
		woTask.task_type = flowTache.task_type;
		woTask.wo_state = BPMConsts.WO_STATE_READY;// READY 就绪
		woTask.state_date = curDbTime;
		woTask.task_title = StrUtil.isNotEmpty(msg.getTaskTitle()) ? msg.getTaskTitle() : msg.getBoTitle() + "-" + flowTache.tache_name;
		woTask.task_content = msg.getBoTitle();
		woTask.tache_code = flowTache.tache_code;
		woTask.tache_name = flowTache.tache_name;
		woTask.flow_id = msg.getFlowId();
		woTask.bo_id = msg.getBoId();
		woTask.bo_type_id = msg.getBoTypeId();
		woTask.create_date = curDbTime;
		woTask.create_oper_id = msg.getOperId();
		woTask.dispatch_date = curDbTime;
		woTask.dispatch_oper_id = msg.getOperId();
		woTask.getDao().insert(woTask);

		msg.setWoTask(woTask);
		
		
		return true;
	}

}
