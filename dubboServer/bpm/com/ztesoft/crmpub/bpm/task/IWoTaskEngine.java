package com.ztesoft.crmpub.bpm.task;

import com.ztesoft.crmpub.bpm.vo.MsgBean;

public interface IWoTaskEngine {

	// 发送任务消息
	public boolean putMsg(MsgBean msg) throws Exception;

}
