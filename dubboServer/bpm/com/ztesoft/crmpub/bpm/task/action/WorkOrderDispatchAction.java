package com.ztesoft.crmpub.bpm.task.action;

import java.util.List;

import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DaoUtils;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;

/**
 * 工单改派
 * 目前应该没使用到 joshui
 * @author lirx
 * 
 */
@Deprecated 
public class WorkOrderDispatchAction  extends WorkOrderBaseAction  implements IWorkOrderAction {

	public boolean execute() throws Exception {
		MBpmWoTask woTask = getWoTask();
		List<IVO> taskExecutors= MBpmWoTaskExec.getDAO().query(" wo_id =? ", woTask.wo_id);
		
		String curDbTime = DaoUtils.getDBCurrentTime();
		for (IVO vo : taskExecutors){
			MBpmWoTaskExec executor = (MBpmWoTaskExec) vo;
			executor.set("worker_type",  msg.getWorkerType());
			executor.set("task_worker",  msg.getTaskWorker());
			executor.set("dispatch_oper_id",  msg.getOperId());
			executor.set("dispatch_date",  curDbTime);
			executor.set("exec_state", BPMConsts.WO_STATE_READY);
			
			String[] fields = (String[])executor.updateFieldSet.toArray(new String []{});			
			executor.getDao().updateParmamFieldsByIdSQL(fields).update(executor);
			executor.updateFieldSet.clear();
		}
		
		woTask.set("dispatch_oper_id",  msg.getOperId());
		woTask.set("dispatch_date",  curDbTime);		
		woTask.set("wo_state", BPMConsts.WO_STATE_READY);
		String[] fields = (String[])woTask.updateFieldSet.toArray(new String []{});		
		woTask.getDao().updateParmamFieldsByIdSQL(fields).update(woTask);
		woTask.updateFieldSet.clear();
		
		return true;
	}

	public void setMsg(MsgBean msg) {
		this.msg = msg;
	}

}
