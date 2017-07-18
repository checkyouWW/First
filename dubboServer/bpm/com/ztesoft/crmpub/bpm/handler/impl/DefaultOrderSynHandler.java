package com.ztesoft.crmpub.bpm.handler.impl;

import java.util.List;

import com.ztesoft.crmpub.bpm.handler.IHandler;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;
import appfrm.app.vo.IVO;

public class DefaultOrderSynHandler implements IHandler {
	private MBpmWoTask woTask = null;
	@Override
	public void setMsg(Object obj) {
		this.woTask = (MBpmWoTask)obj;
		
	}
	@Override
	public <T> T excuete() throws Exception {
		// TODO Auto-generated method stub
//		String checkFinish = "select count(*) colNum from BPM_WO_TASK_EXEC where WO_ID=? and EXEC_STATE <> 'FINISH' ";
		List<IVO> list = MBpmWoTaskExec.getDAO().query(" WO_ID=? and EXEC_STATE <> 'FINISH' ", this.woTask.wo_id);
		if(list != null && list.size()>0){
			return (T) new Boolean(false);
		}
		return (T) new Boolean(true);
	}
	public void setMsgBean(MsgBean msg) {
		// TODO Auto-generated method stub
		
	}


}
