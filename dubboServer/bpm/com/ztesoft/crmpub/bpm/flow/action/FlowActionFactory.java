/**
 * 
 */
package com.ztesoft.crmpub.bpm.flow.action;

import com.ztesoft.crmpub.bpm.consts.MsgConsts;

/**
 * @author major
 *
 */
public class FlowActionFactory {
	public static IFlowAction getFlowAction(String msgAction){
			IFlowAction result;
			if (MsgConsts.ACTION_NEW_FLOW.equals(msgAction)){
				result = new FlowNewAction();
			}else if   (MsgConsts.ACTION_UPDATE_FLOW.equals(msgAction)){
				result = new FlowUpdateAction();
			}else if(MsgConsts.ACTION_INVALID_FLOW.equals(msgAction)){
				result = new FlowInvalidAction();
			}
			else {
				throw new RuntimeException("不正确的消息动作");
			}
			return result;
	}
}
