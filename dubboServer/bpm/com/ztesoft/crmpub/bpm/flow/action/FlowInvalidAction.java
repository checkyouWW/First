/**
 * 
 */
package com.ztesoft.crmpub.bpm.flow.action;

import appfrm.resource.dao.impl.DaoUtils;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;


/**
 * @author li.jh
 *
 */

public class FlowInvalidAction extends AbstractFlowAction implements IFlowAction {
	
	public boolean execute() throws Exception{
		//1.加载流程实例	
		MBpmBoFlowInst flowInst = loadFlowInst();
		
		//2.更新流程状态
		flowInst.bo_state = BPMConsts.BO_STATE.INVALID;
		flowInst.bo_state_name = BPMConsts.BO_STATE.INVALID_NAME;
		flowInst.state_date = DaoUtils.getDBCurrentTime();
		flowInst.col1  = msg.getOperId();
		flowInst.getDao().updateParmamFieldsByIdSQL("bo_state", "bo_state_name","state_date", "col1").update(flowInst);

		//3.设置消息处理结果
		if (msg.getResult() == null) {
			msg.setResult(MsgConsts.BPM_SUCCESS);
			msg.setResultName(MsgConsts.INVALID_FLOW_SUCCESS_NAME);
		}

		//4.执行业务回调函数
		ICallBackHandler callBackHandler = msg.getCallBackHandler();
		if (callBackHandler != null) {
			callBackHandler.processMsg(msg);
		}
		return true;
	}
	
}
