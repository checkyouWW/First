package com.ztesoft.crmpub.bpm.flow.action;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;

public class AbstractFlowAction {

	protected MsgBean msg;
	public void setMsg(MsgBean msg){
		 this.msg = msg;
	}
	
	public MBpmBoFlowInst loadFlowInst() {

		MBpmBoFlowInst flowInst = (MBpmBoFlowInst) MBpmBoFlowInst.getDAO()
				.findById(msg.getFlowId());
 
		if (flowInst == null) {
			throw new RuntimeException("流程实例不存在");
		}

		if (!BPMConsts.BO_STATE.ACTIVITY.equals(flowInst.bo_state)) {
			throw new RuntimeException("流程处于非激活状态，不允许修改流程!");
		}
	
		msg.setBoId(flowInst.bo_id);
		msg.setBoTypeId(flowInst.bo_type_id);
		msg.setBoTitle(flowInst.bo_title);
		return flowInst;
	}
}