package com.ztesoft.crmpub.bpm.task.action;

import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

public interface IWorkOrderAction {
	
		public boolean before() throws Exception;
		
	    public boolean execute() throws Exception;
	    
	    public boolean after() throws Exception;
	    
	    public void setMsg(MsgBean msg);
	    
	    public MBpmWoTask getWoTask();
	    
	    public SBpmWoType getWoType();
	    
}
