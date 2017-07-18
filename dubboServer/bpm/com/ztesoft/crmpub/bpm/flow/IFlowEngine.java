/**
 * 
 */
package com.ztesoft.crmpub.bpm.flow;

import com.ztesoft.crmpub.bpm.vo.MsgBean;

/**
 * 流程引擎，负责流程的创建、流转(状态变迁、调用任务单引擎)、归档
 * eventHandler通过put消息的方式通知流程引擎，实现eventHandler-流程引擎 解耦
 * 每一类消息对应一种流程动作action
 * @author major
 *
 */
public interface IFlowEngine {

   //发送消息
   public boolean putMsg(MsgBean msg) throws Exception;

}
