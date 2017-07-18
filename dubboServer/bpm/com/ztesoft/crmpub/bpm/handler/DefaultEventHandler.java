/**
 * 
 */
package com.ztesoft.crmpub.bpm.handler;

import com.ztesoft.crmpub.bpm.flow.IFlowEngine;

/**
 * update流程状态的默认事件处理器，用于发起流程流转、状态变更、派单
 * @author major
 *
 */
public class DefaultEventHandler extends AbstractEventHandler {
	

	public DefaultEventHandler() {
		super();
		//this.nextState = FlowStateConsts.STATE_NEW;
	}

	/* (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.flow.impl.AbstractEventHander#postMsg(com.ztesoft.crmpub.bpm.VO.FlowMsg)
	 */
	@Override
	public boolean execute(IFlowEngine flowEngine) {
//		MsgBean msg=new MsgBean(this);
//		msg.setMsgType(MsgTypeConsts.MSG_UPDATE_STATE);
//		return flowEngine.putMsg(msg);
		return false;
	}
	


}
