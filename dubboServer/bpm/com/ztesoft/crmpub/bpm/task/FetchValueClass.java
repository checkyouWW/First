package com.ztesoft.crmpub.bpm.task;

import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;

/**
 * 取值类
 * 自定义取值类必须从FetchValueClass 继承
 * @author lirx
 *
 */
public abstract class FetchValueClass {
	protected SBpmBoFlowDef flowDef = null;
	protected SBpmBoFlowTache tache = null;
	protected MsgBean msg = null;
	public void setFlowTache(SBpmBoFlowTache tache) {
		this.tache = tache;
	}
	public void setFlowDef(SBpmBoFlowDef flowDef){
		this.flowDef = flowDef;
	}
	public void setMsg(MsgBean msg) {
		this.msg = msg;
	}
	public abstract String execute() throws Exception;

}
