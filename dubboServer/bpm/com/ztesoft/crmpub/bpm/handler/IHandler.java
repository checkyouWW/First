package com.ztesoft.crmpub.bpm.handler;

import com.ztesoft.crmpub.bpm.vo.MsgBean;

/**
 * 事件接口类，BPM各环节及流程配置的事件类都必须继承该类
 * @author liu.yuming
 */
public interface IHandler {
	void setMsg(Object obj);
	/**
	 * 这个类和AbstractEventHandler、ICallBackHandler似乎是重复的
	 * 按照遗留代码的设计意图，handler分两种，一种是事件处理类（比如新增流程），一种是回调处理类（比如某环节执行结束后）
	 * 按照现有设计IHandler的意图，应该再新建个AbstractHandler类抽象一下的，实现类集成抽象类
	 * @param msg
	 * Author : joshui
	 * Date ：2014-7-3
	 */
	void setMsgBean(MsgBean msg);
	<T>T excuete() throws Exception ;
}
