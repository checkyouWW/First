package com.ztesoft.crmpub.bpm.task.action;

import appfrm.app.util.StrUtil;
import appfrm.resource.dao.impl.DaoUtils;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

/**
 * 撤单
 *
 */
public class WorkOrderWithdrawAction extends WorkOrderBaseAction implements IWorkOrderAction{
	public boolean execute() throws Exception {
		MBpmWoTask woTask = getWoTask();

		// 2.获取当前流程实例
		MBpmBoFlowInst flowInst = msg.getFlowInst();
		if (flowInst == null) {
			flowInst = (MBpmBoFlowInst) MBpmBoFlowInst.getDAO().findById(woTask.flow_id);
		}

		// 3.对当前环节回单
		String respContent = msg.getRespContent();
		
		if(msg.getIsNormal().equals("true")){
			woTask.set("wo_state", BPMConsts.WO_STATE_FINISH);
		}else{
			woTask.set("wo_state", BPMConsts.WO_STATE_WITHDRAW);
		}
		woTask.set("resp_content", respContent);
		woTask.set("resp_oper_id", msg.getOperId() );
		woTask.set("resp_result",MsgConsts.BPM_SUCCESS );
		woTask.set("state_date",DaoUtils.getDBCurrentTime() );
		String[] fields = woTask.updateFieldSet.toArray(new String[]{});
		MBpmWoTask.getDAO().updateParmamFieldsByIdSQL(fields).update(woTask);
		
		msg.setBoTitle(getBoTitleFromWO(woTask));
		if(msg.getIsNormal().equals("true")){
			flowInst.set("bo_state_name", BPMConsts.BO_STATE.STATE_END_NAME);
            flowInst.set("bo_state", BPMConsts.BO_STATE.END);
		}else{
			flowInst.set("bo_state_name", BPMConsts.BO_STATE.WITHDRAW_NAME);
            flowInst.set("bo_state", BPMConsts.BO_STATE.INVALID);
		}

		flowInst.set("state_date", DaoUtils.getDBCurrentTime());
		fields = flowInst.updateFieldSet.toArray(new String[]{});
		MBpmBoFlowInst.getDAO().updateParmamFieldsByIdSQL(fields).update(flowInst);
		
		// 当前环节执行人更新
		dealCurWoTaskExec(msg);
		
		//最后归档
		flowInst.finishOrder();
		
		//handler
		SBpmWoType woType = this.getWoType();
		if( null != woType && StrUtil.isNotEmpty(woType.fail_event_handler) ){
			Class specClass = Class.forName(woType.fail_event_handler);
			Object handlerObj = specClass.newInstance();
			if ( handlerObj  instanceof ICallBackHandler ) {
				ICallBackHandler handler = (ICallBackHandler)handlerObj;
				return handler.processMsg(msg);
			}else{
				throw new RuntimeException("工单定义【"+woType.wo_type_id+"】的失败处理器类型不正确！需要实现接口【ICallBackHandler】！") ;
			}
		}
		
		//将data_change_notify表归档到l_data_change_notify表
		//AppUtil.archiveChannelNotify(flowInst.bo_id, null, null, null);
		return true;
	}
}
