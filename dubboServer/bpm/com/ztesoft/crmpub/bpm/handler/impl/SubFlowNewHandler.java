package com.ztesoft.crmpub.bpm.handler.impl;

import appfrm.app.util.StrUtil;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.handler.IHandler;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;

/**
 * 子流程启动后，执行的事件处理类
 * Author : joshui
 * Date ：2014-7-3
 */
public class SubFlowNewHandler implements IHandler {
	private MsgBean msg = new MsgBean();
	
	@Override
	public void setMsg(Object msg) {
	}

	/* 
	 * 如果存在父工单实例id，更新父工单实例的状态为挂起，子流程处理完后，再通过环节事件，将
	 * 状态改为正常
	 * (non-Javadoc)
	 * @see com.ztesoft.crmpub.bpm.handler.IHandler#excuete()
	 */
	@Override
	public <T> T excuete() throws Exception {
		if (StrUtil.isEmpty(msg.getParentWoID())) {
			return null;
		}
		
		MBpmWoTask woTask = (MBpmWoTask) MBpmWoTask.getDAO()
			.findById(msg.getParentWoID());
		if (woTask != null) {
			woTask.wo_state = BPMConsts.WO_ACTION_SUSPEND;
			woTask.getDao().updateParmamFieldsByIdSQL("wo_state").update(woTask);
		}
		
		return null;
	}

	public void setMsgBean(MsgBean msg) {
		this.msg = msg;
	}

}
