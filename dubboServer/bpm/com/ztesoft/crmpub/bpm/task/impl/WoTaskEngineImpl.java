package com.ztesoft.crmpub.bpm.task.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appfrm.app.util.StrUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DaoUtils;

import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.attr.util.SqlValUtil;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.task.IWoTaskEngine;
import com.ztesoft.crmpub.bpm.task.action.IWorkOrderAction;
import com.ztesoft.crmpub.bpm.task.action.WorkOrderActionFactory;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoDispatchRule;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

public class WoTaskEngineImpl implements IWoTaskEngine {
	@Override
	public boolean putMsg(MsgBean msg) throws Exception {
		boolean result = false;
		// 保存消息
//		MsgBean.saveBoMsg(msg);

		if (MsgConsts.MSG_HANDLE_ASYN.equals(msg.getMsgHandleWay())) {
			result = handleAsynMsg(msg);// 异步处理
		} else {
			result = handleSyncMsg(msg);// 同步处理
		}

		return result;
	}

	// 异步处理
	private boolean handleAsynMsg(MsgBean msg) {
		// TODO
		return true;
	}

	// 同步处理
	private boolean handleSyncMsg(MsgBean msg) throws Exception {
		return doHandleMsg(msg);
	}

	/**
	 * 处理消息
	 * 
	 * @param msg
	 * @return
	 */
	private boolean doHandleMsg(MsgBean msg) throws Exception {
		IWorkOrderAction workOrderAction = WorkOrderActionFactory
				.getWorkOrderAction(msg.getMsgAction());
		workOrderAction.setMsg(msg);
		workOrderAction.before();
		boolean result = workOrderAction.execute();
		workOrderAction.after();
		return result;
	}
	  
}
