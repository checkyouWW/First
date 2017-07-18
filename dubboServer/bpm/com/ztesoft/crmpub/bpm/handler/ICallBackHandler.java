package com.ztesoft.crmpub.bpm.handler;

import com.ztesoft.crmpub.bpm.vo.MsgBean;

public interface ICallBackHandler {
	  public boolean processMsg(MsgBean msg) throws Exception;
}
