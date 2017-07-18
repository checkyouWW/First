/**
 * 
 */
package com.ztesoft.crmpub.bpm.flow.impl;

import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.flow.IFlowEngine;
import com.ztesoft.crmpub.bpm.flow.IFlowInst;
import com.ztesoft.crmpub.bpm.flow.action.FlowActionFactory;
import com.ztesoft.crmpub.bpm.flow.action.IFlowAction;
import com.ztesoft.crmpub.bpm.util.WoTaskHelper;
import com.ztesoft.crmpub.bpm.vo.MsgBean;

/**
 * @author major
 * 
 */
public class FlowEngineImpl implements IFlowEngine {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ztesoft.crmpub.bpm.flow.IFlowEngine#putMsg(com.ztesoft.crmpub.bpm
	 * .VO.FlowMsg)
	 */
	 @Override
	public boolean putMsg(MsgBean msg) throws Exception{
		
		boolean result = false;
        //保存消息
//		MsgBean.saveBoMsg(msg);
		
		if (MsgConsts.MSG_HANDLE_ASYN.equals(msg.getMsgHandleWay())){
			result =  handleAsynMsg(msg);//异步处理
		}else {
			result =  handleSyncMsg(msg);//同步处理
		}

		return result;
	}

	// 异步处理
	private boolean handleAsynMsg(MsgBean msg) {
		//TODO
		return true;
	}

	// 同步处理
	private boolean handleSyncMsg(MsgBean msg) throws Exception{
		return doHandleMsg(msg);
	}
	
	/**
	 * 处理消息
	 * @param msg
	 * @return
	 */
	private boolean doHandleMsg(MsgBean msg) throws Exception{
		IFlowAction flowAction = FlowActionFactory.getFlowAction(msg.getMsgAction());
		flowAction.setMsg(msg);
		return flowAction.execute();
	}
	

}