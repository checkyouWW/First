package com.ztesoft.crmpub.bpm.task.action;

import com.ztesoft.crmpub.bpm.consts.MsgConsts;

/**
 * 任务工单动作工厂类
 * @author major
 *
 */
public class WorkOrderActionFactory {

	public static IWorkOrderAction getWorkOrderAction(String msgAction) {
		IWorkOrderAction workOrderAction = null;
		if (MsgConsts.ACTION_WO_NEW.equals(msgAction)) { // 工单 新建			
			workOrderAction = new WorkOrderNewAction();
		}else if (MsgConsts.ACTION_WO_DISPATCH.equals(msgAction)) { // 工单改派			
			workOrderAction = new WorkOrderDispatchAction();
		}else if (MsgConsts.ACTION_WO_FINISH.equals(msgAction)) {		// 正常回单
			workOrderAction = new WorkOrderFinishAction();
		}else if ( MsgConsts.ACTION_WO_FAIL.equals(msgAction)){  // 异常回单
			workOrderAction = new WorkOrderFailAction();
		}else if ( MsgConsts.ACTION_WO_WITHDRAW.equals(msgAction)){  // 撤单
			workOrderAction = new WorkOrderWithdrawAction();
		} 
		return workOrderAction;
	}
}
