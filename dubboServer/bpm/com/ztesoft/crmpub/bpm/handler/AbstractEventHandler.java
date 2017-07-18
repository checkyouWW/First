/**
 * 
 */
package com.ztesoft.crmpub.bpm.handler;

import com.ztesoft.crmpub.bpm.flow.IFlowEngine;

/**
 * 事件处理器抽象基类
 * @author major
 *
 */
public abstract class AbstractEventHandler {
	protected String operId;   //操作工号
	protected String boTypeId; //流程类型
	protected String nextState; //下一个流程状态
	protected String nextWoTypeId;//下一个环节的工单类型
	protected ICallBackHandler callBackHandler;

	public ICallBackHandler getCallBackHandler() {
		return callBackHandler;
	}

	public void setCallBackHandler(ICallBackHandler callBackHandler) {
		this.callBackHandler = callBackHandler;
	}

	public abstract boolean execute(IFlowEngine flowEngine) throws Exception;
	
	public String getOperId() {
		return operId;
	}
	public void setOperId(String operId) {
		this.operId = operId;
	}
	
	
	public String getNextState() {
		return nextState;
	}

	public void setNextState(String nextState) {
		this.nextState = nextState;
	}

	public String getNextWoTypeId() {
		return nextWoTypeId;
	}

	public void setNextWoTypeId(String nextWoTypeId) {
		this.nextWoTypeId = nextWoTypeId;
	}
	
	public String getBoTypeId() {
		return boTypeId;
	}

	public void setBoTypeId(String boTypeId) {
		this.boTypeId = boTypeId;
	}

}
