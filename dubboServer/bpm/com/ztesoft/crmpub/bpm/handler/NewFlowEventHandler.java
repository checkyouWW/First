/**
 * 
 */
package com.ztesoft.crmpub.bpm.handler;

import com.ztesoft.crmpub.bpm.flow.IFlowEngine;

/**
 * 新建流程事件处理器，用于界面或接口调用发起流程
 * @author major
 *
 */
public class NewFlowEventHandler extends AbstractEventHandler {


	public NewFlowEventHandler() {
		super();
		//this.nextState = FlowStateConsts.STATE_NEW;
	}

	/* (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.flow.impl.AbstractEventHander#postMsg(com.ztesoft.crmpub.bpm.VO.FlowMsg)
	 */
	@Override
	public boolean execute(IFlowEngine flowEngine) throws Exception{
//		MsgBean msg=new MsgBean(this);
//		msg.setMsgType(MsgConsts.MSG_NEW_FLOW);
//		return flowEngine.putMsg(msg);
		return false;
	}
	


}
