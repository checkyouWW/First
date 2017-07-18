/**
 * 
 */
package com.ztesoft.crmpub.bpm.flow.action;

import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;

/**
 * @author major
 *
 */
public interface IFlowAction {

	public void setMsg(MsgBean msg);

	public boolean execute() throws Exception;
	
	public MBpmBoFlowInst loadFlowInst();

}
