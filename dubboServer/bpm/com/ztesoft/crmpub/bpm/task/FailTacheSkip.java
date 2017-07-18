package com.ztesoft.crmpub.bpm.task;

import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;

/**
 * 当前环节不通过判断直接打回到提单环节
 * @author liu.yuming
 *
 */
public class FailTacheSkip extends FetchValueClass{
	
	public String execute() throws Exception{
		SBpmBoFlowTache curTache = this.tache;
		boolean target = this.flowDef.isFirstTache(curTache.tache_code);
		if(target)return "false";
		
        return "true";
	}
}
